package com.keystone.transpot.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.keystone.support.common.ExceptionUtils;
import com.keystone.transpot.api.EventWorker;
import com.keystone.transpot.api.IoBuffer;
import com.keystone.transpot.api.IoSession;
import com.keystone.transpot.api.IoWorker;

public class DefaultIoSession implements IoSession {

	/**
	 * 
	 */
	private SelectionKey key ;
	
	/**
	 * 
	 */
	private SelectableChannel channel ;
	
	/**
	 * 
	 */
	private InetSocketAddress remoteAddress ;
	
	/**
	 * 
	 */
	private IoWorker ioWorker ;
	
	/**
	 * 
	 */
	private EventWorker eventWorker ;
	
	/**
	 * 
	 */
	private IoBuffer recvBuffer ;
	
	/**
	 * 
	 */
	private BlockingQueue<ByteBuffer> writeQueue = new LinkedBlockingQueue<>() ;
	
	/**
	 * 
	 */
	private Status status = Status.NOT_CONNECT ;
	
	/**
	 * 
	 */
	private long lastOperationTime ;
	
	/**
	 * for client
	 */
	private CountDownLatch connectedLatch = new CountDownLatch(1);
	
	
	/**
	 * 
	 */
	@Override
	public int read() throws IOException {
		int len = ioWorker.processRead(this) ;
		try
		{
			if(len>0)
			{
				eventWorker.recvMessage(this);
				ioWorker.interestRead(this);//TODO:add this...
			}
		}
		finally
		{
			if(len<0) close() ;
		}
		return len ;
	}

	
	
	/**
	 * 
	 */
	@Override
	public int write() throws IOException {
		return ioWorker.processWrite(this) ;
	}


	
	
	/**
	 * 
	 */
	@Override
	public void asyncWrite(Object packet) throws IOException {
		if(packet==null) return ;
		ByteBuffer buf = eventWorker.sendMessage(packet, this);
		if(buf!=null)
		{
			if(getWriteQueue().offer(buf))
			{
				ioWorker.interestWrite(this) ;
			}
			else
			{
				throw new IOException("the writeQueue is Full!!!") ;
			}
		}
	}

	
	
	
	@Override
	public IoBuffer getRecvBuffer() {
		return this.recvBuffer ;
	}


	@Override
	public IoSession setRecvBuffer(IoBuffer recvBuffer) {
		this.recvBuffer = recvBuffer ;
		return this ;
	}

	
	
	/**
	 * 
	 */
	@Override
	public SelectableChannel getChannel() {
		return this.channel ;
	}

	
	/**
	 * 
	 */
	@Override
	public IoSession setChannel(SelectableChannel channel) {
		this.channel = channel ;
		return this ;
	}

	
	/**
	 * 
	 */
	@Override
	public SelectionKey getKey() {
		return this.key ;
	}

	
	/**
	 * 
	 */
	@Override
	public IoSession setKey(SelectionKey key) {
		this.key = key ;
		return this ;
	}

	
	
	@Override
	public boolean isClosed() {
		return status == Status.CLOSED ;
	}

	
	
	/**
	 * 
	 */
	@Override
	public synchronized void close() {
		ioWorker.processClose(this) ;
	}

	
	
	/**
	 * 
	 */
	@Override
    public InetSocketAddress getRemoteSocketAddress() {
        if (remoteAddress == null && channel instanceof SocketChannel) {
        	remoteAddress = (InetSocketAddress) ((SocketChannel) this.channel).socket().getRemoteSocketAddress();
        }
        return remoteAddress;
    }
	
	
	
	
	
	
	@Override
	public void flush() {
		throw ExceptionUtils.unsupportedMethodException() ;
	}

	
	
	
	
	/**
	 * 
	 */
	@Override
	public BlockingQueue<ByteBuffer> getWriteQueue() {
		return writeQueue ;
	}

	
	
	/**
	 * 
	 */
	@Override
	public long getLastOperationTime() {
		return this.lastOperationTime ;
	}

	
	/**
	 * 
	 */
	@Override
	public long updateLastOperationTime() {
		this.lastOperationTime = System.currentTimeMillis() ;
		return this.lastOperationTime ;
	}

	
	/**
	 * 
	 */
	@Override
	public IoSession setStatus(Status status) {
		this.status = status ;
		return this ;
	}

	/**
	 * 
	 */
	@Override
	public Status getStatus() {
		return status;
	}

	/**
	 * 
	 */
	@Override
	public void finishConnect() {
		this.connectedLatch.countDown();
	}	





	@Override
	public IoSession setEventWork(EventWorker eventWorker) {
		this.eventWorker = eventWorker ;
		return this ;
	}





	@Override
	public IoSession setIoWork(IoWorker ioWorker) {
		this.ioWorker = ioWorker ;
		return this ;
	}
	
	
	
	/**
	 * 
	 * @param connectTimeout MILLISECONDS
	 * @return
	 * @throws InterruptedException 
	 */
	@Override
	public boolean waitToConnect(long connectTimeout, TimeUnit unit) throws InterruptedException {
		if(connectedLatch.getCount()>0)
		{
			return this.connectedLatch.await(connectTimeout, unit);
		}
		return true ;
	}



	

}
