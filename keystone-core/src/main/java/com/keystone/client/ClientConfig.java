package com.keystone.client;

import com.keystone.transpot.impl.DefaultNIOConfg;

public class ClientConfig extends DefaultNIOConfg {

	public ClientConfig() {
		setProtocolFactoryClassName("com.keystone.remoting.codec.KeystoneClientProtocolFactory") ;
		setMessageHandlerClassName("com.keystone.remoting.RemotingMessageHandler") ;
		setReactorNamePrefix("keystone-client-reactor") ;
		setReactorCount(2) ;
	}
}
