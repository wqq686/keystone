package com.keystone.transpot.impl;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.keystone.transpot.api.IoSession;
import com.keystone.transpot.api.IoWorker;
import com.keystone.transpot.api.Reactor;


/**
 * 
 * @author wuqq
 *
 */
public class DefaultReactor extends Thread implements Reactor {

	/**
	 * cancel keys的个数阀值，超过这个数值调用一次selectNow一次性清除
	 */
	private static final int CLEANUP_INTERVAL = 256;
	
	/**
	 * 
	 */
	private static final int SELECT_WAIT = 500 ;
	
	/**
	 * 
	 */
	protected volatile Selector selector;

	/**
	 * 
	 */
	private IoWorker ioWorker ;
	
	/**
	 * channel、ops、tcpsession
	 */
	private LinkedBlockingQueue<Object[]> register = new LinkedBlockingQueue<Object[]>() ;
	
	/**
	 * 
	 */
	private final AtomicBoolean wakeup = new AtomicBoolean() ;
	
	/**
	 * 
	 */
	private int cancelledKeys ;
	
	
	/**
	 * 
	 * @throws IOException
	 */
	public DefaultReactor() throws IOException {
		super() ;
		selector = Selector.open();
	}

	/**
	 * 
	 */
	@Override
	public Reactor setIoWorker(IoWorker ioWorker) {
		this.ioWorker = ioWorker ;
		return this ;
	}




	@Override
	public Selector getSelector() {
		return this.selector ;
	}
	

	
	
	@Override
	public void run() {
		//Just for Simple.
		while(!interrupted())
		{
			try
			{
				//1. 
				processRegister() ;
				
				//1. select
				wakeup.set(false); 
				selector.select(SELECT_WAIT) ;
//				selector.select() ;
				cancelledKeys = 0 ;
				
				//2. process
				processSelectedKeys() ;
				
				//3. close cancel(close socketfd)
				clearCancelKeys(false) ;
			}
			catch(Exception e) 
			{
				e.printStackTrace(); 
			}
		}
		
		
		if(interrupted())
		{
			try
			{
				selector.close();
			}catch(Exception ignore){}
		}
	}
	
	
	
	/**
	 * 
	 */
	@Override
	public void registerSession(IoSession session, int ops) throws IOException {
        this.registerChannel(session.getChannel(), ops, session) ;
    }
	
	
	/**
	 * 
	 * @param channel
	 * @param ops
	 * @param attachment
	 */
	@Override
    public void registerChannel(SelectableChannel channel, int ops, Object attachment) throws IOException{
        if(Thread.currentThread() == this)
        {
        	SelectionKey key = channel.register(selector, ops, attachment);
        	if (attachment!=null && attachment instanceof IoSession)
        	{
				((IoSession)attachment).setKey(key);
			}
        }
        else
        {
			this.register.offer(new Object[]{ channel, ops, attachment });
			this.tryWakeup() ;
        }
    }
	
	

    /**
     * 
     */
	@Override
	public void unRegisterChannel(SelectableChannel channel) {
		if(channel!=null)
		{
			try
			{
				//1. cancel Key
				SelectionKey key = channel.keyFor(selector);
				if (key != null) {
	                key.cancel();
	                this.cancelledKeys++;
	            }
				
				//2. close socket
				closeSocket(channel) ;
				
				//3. close channel
				if(channel.isOpen()) {
					channel.close();
				}
				
				//4. try wakeup. 这里没有采用gecko处理方式.因为wakeup之后马上就会重新进行select, 可以真正关闭channel(close socketfd)
				if(this.cancelledKeys>CLEANUP_INTERVAL) {
					tryWakeup() ;
				}
				
			} catch (Throwable ignore) {}
		}
	}

	
	
	/**
	 * 
	 * @param channel
	 */
	private void closeSocket(SelectableChannel channel) {
		if(channel!=null &&  channel instanceof SocketChannel)
		{
			Socket socket = ((SocketChannel) channel).socket();
			if(!socket.isClosed())
			{
				if (!socket.isOutputShutdown()) {
					try{socket.shutdownOutput();}catch(IOException ignore){}
				}
				if (!socket.isInputShutdown()) {
					try{socket.shutdownInput();}catch(IOException ignore){}
	            }
				
				try{socket.close();}catch(IOException ignore){}
			}
		}
	}
	
	
	
	/**
	 * 
	 */
	private void tryWakeup() {
        if(wakeup.compareAndSet(false, true))
		{
			this.selector.wakeup();
		}
    }
	
	
	
	
	/**
	 * 
	 */
	private void processSelectedKeys() {
		Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
		SelectionKey key = null ;
		while (iter.hasNext()) 
		{
			key = iter.next();
			iter.remove();
			try
			{
				
				//1. isValid ?
				if (!key.isValid()) 
				{
					disConnectEvent(key, null) ;
	                continue;
	            }
				
				//2. Update the last operation time for SessionManager(TODO:)
				if (key.attachment() != null && key.attachment() instanceof IoSession)
				{
					((IoSession)key.attachment()).updateLastOperationTime();
				}
				
				//3. Dispatch I/O event
				dispatchEvent(key); 
				
			}
			catch (CancelledKeyException ignore) {}
			catch (Exception e) 
			{
				disConnectEvent(key, e) ;
				if(selector.isOpen()) continue ;
				else break ;
			}
		}
	}

	
	
	
	
	/**
	 * 
	 * @param key
	 * @param e
	 */
	private void disConnectEvent(SelectionKey key, Exception e) {
		if (key.attachment() != null && key.attachment() instanceof IoSession)
        {
			((IoSession)key.attachment()).close();
        }
        else
        {
            key.cancel();
        }
		if(e!=null) e.printStackTrace() ;
		//TODO:dispath exception event...
	}
	


	/**
	 * 
	 * @param key
	 * @throws IOException 
	 */
	private void dispatchEvent(SelectionKey key) throws IOException {
		if (key.isConnectable())
		{	
			ioWorker.processConnect(key);
			return ;
		}
		else if (key.isAcceptable())
		{
			ioWorker.processAccept(key);
			return ;
		}
		
		if(key.isWritable())
		{
            // Remove write interest
            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
            foundIoSession(key).write() ;
        }
		
		if(key.isReadable())
        {
        	// Remove read interest
            key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
            foundIoSession(key).read() ;
        }
	}


	/**
	 * 
	 */
	private void processRegister() {
		Object[] array = null; final Selector selector = this.selector ;  
		while ((array = this.register.poll()) != null)
		{
			try 
			{
				SelectableChannel channel = (SelectableChannel) array[0];
	            if (channel.isOpen()) 
	            {
	            	int ops = ((Integer)array[1]).intValue(); Object attachment = array[2];
	            	SelectionKey key = channel.register(selector, ops, attachment);
	                if (attachment!=null && attachment instanceof IoSession) 
					{
						((IoSession)attachment).setKey(key);
					}
	            }
	        }
			catch(ClosedChannelException ignore) {}//TODO:ignore ex?
			catch(Throwable t) {t.printStackTrace();}
		}
	}
	

	
	
	
	/**
	 * 
	 * @param force
	 * @throws IOException
	 */
	private void clearCancelKeys(boolean force) throws IOException {
        if (force || this.cancelledKeys > CLEANUP_INTERVAL) 
        {
            final Selector selector = this.selector;
            selector.selectNow(); //real close fd
            this.cancelledKeys = 0;
        }
    }
	
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	private IoSession foundIoSession(SelectionKey key) {
		return (IoSession) key.attachment() ;
	}
	

}
