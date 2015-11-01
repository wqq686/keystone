package com.keystone.transpot.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * IOSession与底层的传输层类型无关, 表示通信双端的连接.
 * 一般NIO Session都会提供如下方法:
 * 1. 提供用户自定义属性, 可以用于在过滤器和处理器之间交换用户自定义协议相关信息.
 * 2. 最重要的两个方法就是read和write, 这两个方法都是异步执行, 如要真正完成必须在其结果上进行等待. 
 * 3. 关闭会话的方法close也是异步执行的, 也就是应等待返回的CloseFuture, 此外, 还有另一种关闭方式closeOnFlush, 它和close的区别是会先flush掉写请求队列中的请求数据, 但同样是异步的。
 * 
 * 我们这里主要是提供给RPC调用的NIO传输层, 所以不搞那么复杂.
 * 1. NIO
 * 2. 不提供异步读, 读方法就是真实读(从channel中读取数据), 
 * 异步读最主要是要提供封装了session的future, 最终通过future来进行对读事件操作, 太罗嗦了
 * 基于NIO Reactor的异步特性, 注册读事件, 
 * @author wuqq
 *
 */
public interface IoSession {

	static enum Status {NOT_CONNECT, SERVER_CONNECTED, CLIENT_CONNECTED, CLOSED} ;
	
	
	
	
	/**
	 * read from channel
	 * 
	 * @return
	 * @throws IOException
	 */
	int			read() throws IOException ;
	
	

	/**
	 * write to channel
	 * @throws IOException
	 */
	int		write() throws IOException ;
	
	
	
	/**
	 * write to writeQueue
	 * 
	 * @param packet
	 * @throws IOException
	 */
	void			asyncWrite(Object packet) throws IOException ;
	
	
	/**
	 * 
	 * @return
	 */
	IoBuffer		getRecvBuffer() ;
	
	/**
	 * 
	 * @param recvBuffer
	 * @return
	 */
	IoSession		setRecvBuffer(IoBuffer recvBuffer) ;
	
	/**
	 * 
	 * @return
	 */
	SelectableChannel getChannel() ;
	
	
	/**
	 * 
	 * @param channel
	 */
	IoSession 			setChannel(SelectableChannel channel) ;
	
	
	/**
	 * 
	 * @return
	 */
	SelectionKey	getKey()  ;
	
	
	/**
	 * 
	 * @param key
	 */
	IoSession			setKey(SelectionKey key) ;
	

    /**
     * 
     * @param status
     */
	IoSession 			setStatus(IoSession.Status status);
    
    
    /**
     * 
     * @return
     */
    Status 			getStatus() ;

    /**
     * Return last operation timestamp,operation include read,write,idle
     * 
     * @return
     */
    long 			getLastOperationTime();

	
    /**
     * update last operation timestamp.
     * @return
     */
    long 			updateLastOperationTime();
    
    
    /**
     * 
     * @param eventWorker
     * @return
     */
    IoSession		setEventWork(EventWorker eventWorker) ;
    
    /**
     * 
     * @param ioWorker
     * @return
     */
    IoSession		setIoWork(IoWorker ioWorker) ;
    
    
    
    /**
     * Check if session is closed
     * 
     * @return
     */
    boolean 		isClosed();


    /**
     * Close session
     */
    void 			close();


    /**
     * Return the remote end's InetSocketAddress
     * 
     * @return
     */
    InetSocketAddress getRemoteSocketAddress();


    /**
     * 
     * @return
     */
    BlockingQueue<ByteBuffer> getWriteQueue() ;
    

    /**
     * Flush the write queue,this method may be no effect if OP_WRITE is running.
     */
    void 			flush();
    
    
    /**
     * 
     */
    void 			finishConnect() ;
    
    
    
    /**
     * 
     * @param connectTimeout
     * @param unit
     * @return
     * @throws InterruptedException
     */
    boolean 		waitToConnect(long connectTimeout, TimeUnit unit) throws InterruptedException ;

}
