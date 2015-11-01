package com.keystone.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

import com.keystone.support.common.CommonUtils;
import com.keystone.transpot.api.EventWorker;
import com.keystone.transpot.api.IoSession;
import com.keystone.transpot.api.IoWorker;
import com.keystone.transpot.api.MessageHandler;
import com.keystone.transpot.api.ReactorManager;
import com.keystone.transpot.api.codec.ProtocolException;
import com.keystone.transpot.api.codec.ProtocolFactory;
import com.keystone.transpot.impl.DefaultIoSession;

public class KeystoneClient {

	/**
	 * 
	 */
	private ClientConfig clientConfig ;
	
	/**
	 * 
	 */
	private String host ;
	
	/**
	 * 
	 */
	private int port ;
	
	/**
	 * 
	 */
	private long connectTimeout = 1000 ;
	
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private long readTimeout = 1000 ;
	
	/**
	 * 
	 */
	private ReactorManager reactorManager ;
	
	/**
	 * 
	 */
	private IoSession session ;
	
	/**
	 * 
	 */
	private ProtocolFactory protocolFactory;
	
	/**
	 * 
	 */
	private MessageHandler messageHandler ;
	
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
	 * @param host
	 * @param port
	 * @param connectTimeout
	 * @param readTimeout
	 * @param clientConfig
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public KeystoneClient(String host, int port, long connectTimeout, long readTimeout, ClientConfig clientConfig) throws IOException, InterruptedException {
		this.host = host ;
		this.port = port ;
		this.connectTimeout = connectTimeout ;
		this.readTimeout = readTimeout ;
		this.clientConfig = clientConfig ;
//		initReactorManager() ;
		start() ;
		reConnect() ;
	}
	
	
	
	/**
	 * 
	 */
	private void start() {
		//1. instance
		reactorManager = CommonUtils.newInstance(clientConfig.getReactorManagerClassName()) ;
		ioWorker = CommonUtils.newInstance(clientConfig.getIoWorkerClassName()) ;
		eventWorker = CommonUtils.newInstance(clientConfig.getEventWorkerClassName()) ;
		protocolFactory = CommonUtils.newInstance(clientConfig.getProtocolFactoryClassName()) ;
		messageHandler = CommonUtils.newInstance(clientConfig.getMessageHandlerClassName()) ;
		
		ioWorker.setReactorManager(reactorManager) ;
		
		eventWorker.setProtocolFactory(protocolFactory).setMessageHandler(messageHandler) ;
		
		reactorManager.setNIOConfig(clientConfig).setIoWorker(ioWorker).setEventWorker(eventWorker) ;
		
		reactorManager.start() ;
	}
	
	
	/**
	 * 
	 * @param request
	 * @return
	 * @throws ProtocolException 
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public void invoke(Object request) throws ProtocolException, IOException, InterruptedException {
		ensureConnected();
		session.asyncWrite(request) ;
	}
	
	
	
	
	
	/**
	 * 
	 * @return
	 */
	protected boolean isNotConnected() {
		if(session==null) return true ;
		IoSession.Status status = session.getStatus() ;
		return status==IoSession.Status.NOT_CONNECT || status == IoSession.Status.CLOSED ;
	}
	

	
	/**
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	protected void ensureConnected() throws IOException, InterruptedException {
		if (isNotConnected()) reConnect();
	}
	
	
	/**
	 * 
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	protected synchronized void reConnect() throws IOException, InterruptedException {
		//0. Don't send connect request if it is connecting.
		if (isNotConnected())
		{
			SocketAddress remoteAddress = new InetSocketAddress(this.host, this.port);
			
			//1. Create socket channel
			SocketChannel channel = SocketChannel.open();
			channel.configureBlocking(false);
			channel.socket().setTcpNoDelay(true);
	        channel.socket().setReceiveBufferSize(48*1024);
	        channel.socket().setSendBufferSize(48*1024);
	        channel.connect(remoteAddress);
	        
			//2. Create NioSession for each client connection
	        IoSession client = new DefaultIoSession().setChannel(channel).setIoWork(ioWorker).setEventWork(eventWorker) ;
			//3. Register event
			reactorManager.nextReactor().registerSession(client, SelectionKey.OP_CONNECT);

			//4. Wait to connect
			if(!client.waitToConnect(connectTimeout, TimeUnit.MILLISECONDS))
			{
				client.close();//TODO:asyncClose();
				throw new IOException("connect timed out to " + this.host + ":" + this.port);
			}
			
			IoSession.Status status = client.getStatus() ;
			if(status==IoSession.Status.NOT_CONNECT || status==IoSession.Status.CLOSED)
			{
				client.close();//TODO:.asyncClose();
				throw new IOException("connect failed to " + this.host + ":" + this.port);				
			}
			
			this.session = client;
		}
	}
	
	
	public void close() throws IOException {
		if(this.session!=null) {
			this.session.close(); 
			this.session.getChannel().close();
		}
	}
}