package com.keystone.transpot.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;

import com.keystone.transpot.api.IoBuffer;
import com.keystone.transpot.api.IoSession;
import com.keystone.transpot.api.IoSession.Status;
import com.keystone.transpot.api.IoWorker;
import com.keystone.transpot.api.ReactorManager;

public class DefaultIoWorker implements IoWorker {

	
	/**
	 * 
	 */
	private ReactorManager reactorManager ;

	/**
	 * 
	 */
	@Override
	public void setReactorManager(ReactorManager reactorManager) {
		this.reactorManager = reactorManager ;
	}

	
	/**
	 * 
	 */
	@Override
	public IoSession processAccept(SelectionKey key) throws IOException {
		//1. Accept TCP request
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		SocketChannel channel = server.accept();
		channel.socket().setTcpNoDelay(true);
		channel.configureBlocking(false);//Yes, We Just Use No-Block-IO

		//2. Create TCPSession for each TCP connection
		IoSession session = new DefaultIoSession()
		.setChannel(channel)
		.setStatus(IoSession.Status.SERVER_CONNECTED)
		.setIoWork(this)
		.setEventWork(reactorManager.getEventWorker()) ;
		
		//3. Register channel with the specified session
		reactorManager.nextReactor().registerChannel(channel, SelectionKey.OP_READ, session);
		
		return session ;
	}

	
	
	/**
	 * 
	 */
	@Override
	public void processConnect(SelectionKey key) throws IOException {
		//1. Get the client channel
		SocketChannel client = (SocketChannel) key.channel();
		
		//2. Set the session status
		IoSession session = foundIoSession(key) ;
		if (session == null) throw new IOException("The session is null when connecting to ...");
		try 
		{
			if(client.finishConnect())
			{
				key.interestOps(SelectionKey.OP_READ);
				session.setStatus(Status.CLIENT_CONNECTED);				
			}
//			else
//			{
//				throw new IOException("connect to server failed...") ;
//			}
		} 
		finally 
		{
			session.finishConnect();//for the client
		}
	}

	

	@Override
	public void processClose(IoSession session) {
		reactorManager.foundReactor(session.getKey()).unRegisterChannel(session.getChannel()) ;
	}

	
	
	/**
	 * 
	 */
	@Override
	public int processRead(IoSession session) throws IOException {
		if(session.getRecvBuffer()==null)
		{
			session.setRecvBuffer(IoBuffer.allocate(reactorManager.getNIOConfig().getRecvBufferSize())) ;
		}
		return readFromChannel((SocketChannel)session.getChannel(), session.getRecvBuffer()) ;
	}

	
	
	/**
	 * 
	 */
	@Override
	public int processWrite(IoSession session) throws IOException {
		return writeToChannel(session) ;
	}

	
	/**
	 * 
	 */
	@Override
	public void interestRead(IoSession session) throws IOException {
		reactorManager.foundReactor(session.getKey()).registerSession(session, SelectionKey.OP_READ) ;
	}

	
	/**
	 * 
	 */
	@Override
	public void interestWrite(IoSession session) throws IOException {
		reactorManager.foundReactor(session.getKey()).registerSession(session, SelectionKey.OP_WRITE) ;
	}


	
	/**
	 * 
	 * @param key
	 * @return
	 */
	private IoSession foundIoSession(SelectionKey key) {
		return (IoSession) key.attachment() ;
	}
	
	
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	private int readFromChannel(SocketChannel channel, IoBuffer recvBuffer) throws IOException {
		recvBuffer = recvBuffer == null ? IoBuffer.allocate(1024*4) : recvBuffer ;
		int readBytes = 0, ret = 0; 
		ByteBuffer rbuf = ByteBuffer.allocate(1024*2);
		
		while((ret = ((SocketChannel)channel).read(rbuf)) > 0)
		{
			rbuf.flip();
			readBytes += rbuf.remaining();
			//TODO:--->
			recvBuffer.put(rbuf.array(), rbuf.position(), rbuf.remaining());
			rbuf.clear();
		}
		if(readBytes==0)
		{
			readBytes = blockingRead(channel, recvBuffer) ;
		}
		return ret < 0  ? ret : readBytes;
	}
	
	
	
	/**
	 * 
	 * @param channel
	 * @param recvBuffer
	 * @return
	 * @throws IOException
	 */
	private int blockingRead(SocketChannel channel, IoBuffer recvBuffer) throws IOException {
		int readBytes = 0;
		final Selector readSelector = Selector.open();
		SelectionKey tmpKey = null;
		try
		{
			if (channel.isOpen()) 
			{
				tmpKey = channel.register(readSelector, 0);
				tmpKey.interestOps(tmpKey.interestOps() | SelectionKey.OP_READ);
				final int code = readSelector.select(500);
				tmpKey.interestOps(tmpKey.interestOps() & ~SelectionKey.OP_READ);
				if (code > 0) {
					int n = 0;
					ByteBuffer rbuf = ByteBuffer.allocate(1024 * 2);
					do {
						n = ((ReadableByteChannel) channel).read(rbuf);
						if (n > 0) {
							rbuf.flip();
							readBytes += rbuf.remaining();
							recvBuffer.put(rbuf.array(), rbuf.position(), rbuf.remaining());
							rbuf.clear();
						}
					} while (n > 0 && recvBuffer.remaining() > 0);
				}
			}
		} finally {
			if (tmpKey != null) {
				tmpKey.cancel(); tmpKey = null;
			}
			if (readSelector != null) {
				readSelector.selectNow();// Cancel the key.
			}
		}
		return readBytes;
	}
	
	
	
	
	
	/**
	 * @throws IOException 
	 * 
	 */
	public int writeToChannel(IoSession session) throws IOException {
		SocketChannel channel = (SocketChannel) session.getChannel() ;
		Queue<ByteBuffer> writeQueue = session.getWriteQueue() ;
		SelectionKey key = session.getKey() ;  
		int writeCount = 0; 
//		long start = System.currentTimeMillis() ;
		while(true)
		{
			
			if(writeQueue.isEmpty())
			{
				//1. interest Read
				key.interestOps(key.interestOps() | SelectionKey.OP_READ) ;
				
				//2. Checke Again Write Event
				if(!writeQueue.isEmpty())
				{
					key.interestOps(key.interestOps() | SelectionKey.OP_WRITE) ;
				}
				break ;// Simple Deal...
			}
			
			
			ByteBuffer wbuf = writeQueue.peek();
			
			//block write
			for(;wbuf.hasRemaining();)
			{ 
				((SocketChannel)channel).write(wbuf);
			}
			
			writeCount++; 
			writeQueue.remove();
		}
		
//		System.out.println("writeCount=" + writeCount + ", cost=" + TimeUnit.SECONDS.convert((System.currentTimeMillis()-start), TimeUnit.MILLISECONDS));
		return writeCount;
	}
	
	
	
	
	/**
	 * 
	 * @param buffer
	 * @return
	 */
	protected IoBuffer resetIoBuffer(IoBuffer buffer) {
		IoBuffer newBuffer = null;
		if (buffer != null && buffer.remaining() > 0)
		{
			int len = buffer.remaining();
    		byte[] bb = new byte[len];
    		buffer.get(bb);
    		newBuffer = IoBuffer.wrap(bb);
    		newBuffer.position(len);
		}
		return newBuffer;
	}







}
