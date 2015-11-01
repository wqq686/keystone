package com.keystone.server;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.Collection;
import java.util.Properties;

import com.keystone.server.admin.AdminContextConfig;
import com.keystone.server.config.AppContextConfig;
import com.keystone.server.config.KeystoneServerConfig;
import com.keystone.server.config.parser.KeystoneServerConfigParser;
import com.keystone.server.contexts.KeystoneAppContext;
import com.keystone.server.contexts.KeystoneServiceZookeeperRegister;
import com.keystone.server.contexts.services.ServiceSkeleton;
import com.keystone.share.locations.Locators;
import com.keystone.share.locations.ServiceLocation;
import com.keystone.share.locations.ServiceRegister;
import com.keystone.share.services.Router;
import com.keystone.support.common.CommonUtils;
import com.keystone.support.common.ExceptionUtils;
import com.keystone.support.common.IOUtils;
import com.keystone.support.common.StringUtils;
import com.keystone.support.logger.LogFlushWorker;
import com.keystone.transpot.api.EventWorker;
import com.keystone.transpot.api.IoWorker;
import com.keystone.transpot.api.MessageHandler;
import com.keystone.transpot.api.NIOConfig;
import com.keystone.transpot.api.ReactorManager;
import com.keystone.transpot.api.codec.ProtocolFactory;
import com.keystone.transpot.impl.DefaultReactorManager;



/**
 * 
 * @author wuqq
 *
 */
public class KeystoneServer {
	
	/** */
	private String serverRoot = null ;
	
	/** */
	private ServerSocketChannel serverChannel ;
	
	/**
	 * 
	 */
	public void start() {
		try
		{
			//1. init system log
			initSystemLogger() ;
			
			Properties jvm = System.getProperties();
			
			System.out.println("[JAVA][java.home=" + jvm.getProperty("java.home")+"]");
			System.out.println("[JAVA][java.specification.version=" + CommonUtils.getJvmVersion()+"]\n");
			
			System.out.println("[KEY-STONE-SERVER] initSystemLogger OK.");
			System.out.println("[KEY-STONE-SERVER] [SERVER ROOT PATH=" + getServerRoot()+"]\n");
			
			//1. load config
			KeystoneServerConfig conf = loadConfig() ;
			System.out.println("[KEY-STONE-SERVER] load config successed...") ;
			//2. start appContainer
			startAppContexts(conf) ;
			
			//3. start Reactor Manager
			startReactorManager(conf) ;
			
			//4. start NIO Server
			startNIOServer(conf) ;
			
			//5. start Session Manager 
			startSessionManager(conf) ;
			
			//6. register server hook 
			registerServerHook() ;
			
			//7. start services register
			startServiceRegister(conf);
			
			System.out.println("[KEY-STONE-SERVER] host="+conf.getHost()+", port="+conf.getTcpPort()+" started successfully.\n");
			
		}
		catch(Throwable t)
		{
			ExceptionUtils.fullError("[KEY-STONE-SERVER] failed to start ksserver...", t, "\n") ;
			LogFlushWorker.flush();
			System.exit(-1) ;
		}
	}
	
	
	
	
	/**
	 * 
	 * @throws IOException
	 */
	private void initSystemLogger() throws IOException {
		String logRoot = IOUtils.mergePaths(getServerRoot(), "log");
		KeystoneLoggerManager.start(logRoot) ;
	}
	
	
	/**
	 * @return
	 * @throws IOException
	 */
	private KeystoneServerConfig loadConfig() throws IOException {
		String serverXml = IOUtils.mergePaths(serverRoot, "conf", "server.xml") ;
		KeystoneServerConfig sc = KeystoneServerConfigParser.parseServerXml(serverXml) ;
		checkConfig(sc) ;
		KeystoneResourcesManager.start(sc);
		return sc ;
	}
	
	
	/**
	 * 
	 * @param conf
	 */
	private void checkConfig(KeystoneServerConfig conf) {
		try 
		{
			if(conf.getHost()==null) {
				conf.setHost(InetAddress.getLocalHost().getHostAddress()) ;
			}
			
			String appsRoot = conf.getAppsRoot() ;
			if(CommonUtils.isEmpty(appsRoot)) {
				appsRoot = IOUtils.mergePaths(getServerRoot(), "apps") ;
				conf.setAppsRoot(appsRoot);
			}
			
			conf.setServerRoot(getServerRoot());
		} catch(Exception e){ throw new IllegalStateException(e);}
	}
	
	
	
	
	
	
	/**
	 * 
	 * @param conf
	 * @throws IOException
	 */
	private void startAppContexts(KeystoneServerConfig conf) throws IOException {
		//1. 
		scanAppContexts(conf) ;
		
		//2. start config context
		startContexts(conf) ;
		
		//3. 
		KeystoneStoreManager.start();
	}
	
	
	
	
	private void scanAppContexts(final KeystoneServerConfig conf) throws IOException {
		File apps = new File(conf.getAppsRoot());
		apps.listFiles(new FileFilter() {
			public boolean accept(File app) {
				//TODO:1. just for filter local debug.
				if(app.getName().contains("svn")) return false ;
				KeystoneServerConfigParser.parseContextConf(conf, app) ;
				return false;
			}
		});
	}




	/**
	 * 
	 * @param conf
	 * @throws IOException
	 */
	private void startContexts(KeystoneServerConfig conf) throws IOException {
		for(AppContextConfig acc : conf.getContextList()) {
			deployAppContext(acc);
		}
		AdminContextConfig admin = new AdminContextConfig(conf.getAdminContextName(), getServerRoot(), 10, 100, 100, -1, 100, 100) ;
		deployAppContext(admin);
		
	}
	
	
	
	
	/**
	 * 
	 * @param acc
	 */
	private void deployAppContext(AppContextConfig acc) {
		try
		{
			System.out.println("[KEY-STONE-DEPLOY:"+acc.getContextName()+"] begin...") ;
			KeystoneAppContext appContext = KeystoneAppContextDeployManager.deployAppContext(acc) ;
			System.out.println("[KEY-STONE-DEPLOY:"+appContext.getContextName()+"] success.\n") ;
		} catch(Throwable t) {
			ExceptionUtils.fullError("[KEY-STONE-DEPLOY:"+acc.getContextName()+"] failed with:", t, "\n") ;
		}
	}
	
	
	/**
	 * 
	 * @param conf
	 * @return
	 * @throws IOException
	 */
	private ReactorManager startReactorManager(NIOConfig conf) throws IOException {
		IoWorker ioWorker = CommonUtils.newInstance(conf.getIoWorkerClassName()) ;
		EventWorker eventWorker = CommonUtils.newInstance(conf.getEventWorkerClassName()) ;
		ReactorManager reactorManager = new DefaultReactorManager() ;
		MessageHandler messageHandler = CommonUtils.newInstance(conf.getMessageHandlerClassName()) ;
		ProtocolFactory protocolFactory = CommonUtils.newInstance(conf.getProtocolFactoryClassName()) ;
		
		//1. set
		ioWorker.setReactorManager(reactorManager) ;
		//2. set
		eventWorker.setProtocolFactory(protocolFactory) ;
		eventWorker.setMessageHandler(messageHandler) ;
		//3. reactor
		reactorManager.setNIOConfig(conf).setIoWorker(ioWorker).setEventWorker(eventWorker) ;
		reactorManager.start() ;
		KeystoneResourcesManager.registerReactorManager(reactorManager) ;
		return reactorManager ;
	}
	
	
	
	/**
	 * 
	 * @param conf
	 * @throws IOException
	 */
	private void startNIOServer(KeystoneServerConfig conf) throws IOException {
		String[] hostes = conf.getHost().split(",");
		for(String host : hostes)
		{
			host = StringUtils.emptyOrTrim(host) ;
			if(StringUtils.isEmpty(host)) continue ;
			
			InetSocketAddress socketAddress = new InetSocketAddress(host, conf.getTcpPort()) ;
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
	        serverChannel.socket().bind(socketAddress, conf.getBacklog());
			serverChannel.configureBlocking(false);
			serverChannel.socket().setReceiveBufferSize(128*1024);//TODO:config
			KeystoneResourcesManager.getReactorManager().registerChannel(serverChannel, SelectionKey.OP_ACCEPT, null);
			this.serverChannel = serverChannel ;
		}
	}
	
	
	
	/**
	 * Start session manager service.
	 * 
	 * @param conf
	 */
	private void startSessionManager(KeystoneServerConfig conf) {
		KeystoneSessionManager.getInstance()
		.setSessionTimeout(conf.getSessionTimeout())
		.setSessionCheckInterval(conf.getSessionCheckInterval()).start() ;
	}
	
	
	
	/**
	 * 
	 */
	private void registerServerHook() {
		Thread shutdownHook = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					//1. stop Session Manager 
					stopSessionManager() ;
					
					//2. stop NIO Server
					stopNIOServer() ;
					
					//3. start Reactor Manager
					stopReactorManager() ;

					//4. stop appContainer
					stopAppContexts() ;
					
					//5. stop thread pool
					KeystoneExecutors.shutdown();
					
					System.out.println("[KEY-STONE-SERVER] stopped successfully.\n");
				} catch (Throwable t) {
					ExceptionUtils.fullError("[KEY-STONE-SERVER] stopping with ex:", t, "\n");
				}
			}
		}, "ksserver-shutdown-thread") ;
		Runtime.getRuntime().addShutdownHook(shutdownHook);
	}
	
	
	
	/**
	 * 这块代码放在这里貌似也有点...后续改为ServerEventListener更合理
	 * @param conf
	 */
	private void startServiceRegister(KeystoneServerConfig conf) {
		//1. start service register
		if(!CommonUtils.isEmpty(conf.getZookeeperAddresses())) {
			ServiceRegister register = new KeystoneServiceZookeeperRegister(conf.getZookeeperAddresses()) ;//这里没有进行依赖注入...后续处理吧
			KeystoneResourcesManager.setServiceRegister(register);
			
			String host = conf.getHost() ; int port = conf.getTcpPort() ;
			for(KeystoneAppContext c : KeystoneResourcesManager.getAppcontexts().values()) {
				String context = c.getContextName() ;
				for(ServiceSkeleton skeleton : c.getRemotingContainer().skeletons().values()) {
					Router router = Locators.extractRouter(skeleton.getApiClass()) ;
					String business = router.business(), service = router.service() ;
					ServiceLocation location = register.register(business, service, host, port, context);
					System.out.println("[KEY-STONE-REGISTER]: " + location + " register success.") ;
				}
			}
		}
	}
	
	
	
	
	/**
	 * 
	 */
	@SuppressWarnings("deprecation")
	private void stopSessionManager() {
		KeystoneSessionManager.getInstance().stop();
	}
	
	/**
	 * 
	 */
	private void stopNIOServer() {
		try
		{
			serverChannel.close();
		}catch(Exception ignore){}
	}
	
	/**
	 * 
	 */
	private void stopReactorManager() {
		KeystoneResourcesManager.getReactorManager().stop();
	}
	
	/**
	 * 
	 */
	private void stopAppContexts() {
		Collection<KeystoneAppContext> contexts = KeystoneResourcesManager.getAppcontexts().values() ;
		for(KeystoneAppContext appContext : contexts) {
			AppContextConfig acc = appContext.getContextConfig() ;
			System.out.println("[KEY-STONE-RELEASE:"+acc.getContextName()+"] begin...") ;
			KeystoneAppContextDeployManager.releaseAppContext(appContext);
			System.out.println("[KEY-STONE-RELEASE:"+appContext.getContextName()+"] success.\n") ;
		}
		KeystoneStoreManager.stop();
	}
	
	
	/**
	 * 
	 * @return
	 * @throws IOException 
	 */
	private String getServerRoot() throws IOException {
		if(serverRoot==null)
		{
			String root = System.getProperty("server.root") ;
			if(root==null) root = "." ;
			File file = new File(root) ;
			serverRoot = file.getCanonicalPath() ;
		}
		return serverRoot ;
	}
	
	
	

}
