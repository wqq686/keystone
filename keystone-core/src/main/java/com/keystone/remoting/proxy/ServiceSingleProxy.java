package com.keystone.remoting.proxy;

import java.io.IOException;

import com.keystone.remoting.locator.RemotingProxy;
import com.keystone.share.message.keystone.KeystoneMessage;


public class ServiceSingleProxy<T> extends ServiceAbstractProxy<T> {
	
	
	public ServiceSingleProxy(RemotingProxy remoting) {
		super(remoting);
	}


	@Override
	protected void beforeRemotingInvocation(RemotingTicket ticket) throws Throwable {
		ticket.setMsgType(KeystoneMessage.MSG_TYPE_SINGLE);//只是设置消息类型, 并不进行ticket注册
	}

	
	@Override
	protected Object afterRemotingInvocation(RemotingTicket ticket) throws InterruptedException, IOException {
		return null ;
		//这里是存在一定的不严谨性的, 理论上来说, 应该是等到确认写入io了, 才返回.
		//由于我们是采用异步写的方式, 执行到这里时, 实际上是已经到写入队列了, 但实际上并一定写入了io
		//但是关系并不大吧, 因为从单向调用的角度, 本身要求的更像是一种稳定的UDP请求, 要求的只是消息传送到, 使用场景更类似于消息通知机制
		//对于消息通知机制的事件, 本身就存在着消息丢失的可能性, 同样的即使是消息传送到了, 也不一定能处理成功
		//消息机制往往需要在别的方面来进行消息处理的一致性. 所以, 暂时认为这里如此处理是ok的.
	}






	
}
