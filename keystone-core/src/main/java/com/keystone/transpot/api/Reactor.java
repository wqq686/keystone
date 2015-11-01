package com.keystone.transpot.api;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;

/**
 * 
 * Reactor是整个NIO框架的核心, 主要是处理读、写、关闭等的IO事件.
 * 
 * 一般情况下, 大部分的NIO开源框架的Reactor都只处理 读、写、关闭等的IO事件, 例如典型的mina.
 * 由此, 引发了NIO框架的线程模式问题.典型的如下：
 * <(1)Reactor-Thread-Pool(关注读、写、关闭)> --> <(2)Filter-Thread-Pool(关注编、解码、事件通知等)> --> <Event-Thread-Pool(消息处理)>
 * 由此引发的一些问题就是：
 * 1. (1)、(2)两部分开多大的线程池合适
 * 2. 由于(2)编解码部分需要对多个Session进行操作, 那么不可避免的需要处理锁的问题
 * 3. 线程切换频繁的问题
 * 
---------------------------------------------------------------
 * 这里我们尝试从另一个角度来处理上述问题:
 * 1. 将上述线程模型简化为两个部分,
 * <(1)Reactor-Thread-Pool(关注读、写、关闭、解码)> --> <Event-Thread-Pool(事件通知、编码、消息处理)>
 * 这么做的原因是:
 * 1. 减少操作系统线程切换
 * 2. 未解码的消息, 是没有太大意义的, 没有必要触发多余的线程, 
 *    解码本身是高CPU事件, 将其并入读取数据的Ractor操作, 减少线程切换.
 *    从IO中读数据数据都是单线程操作, 由该线程进行解码, 躲避掉了编解码层的Session锁争夺的问题
 * 3. 对于写数据来说, 线程已经起来了, 那么让该线程进行编码, 而不用切换线程之后再进行编码操作, 
 *    同样也是有利于降低线程切换.
 *    与读类似, 这样也同样可以躲避掉了编解码层的Session锁争夺的问题
--------------------------------------------------------------    
 *    所以这里的Reactor从逻辑上(代码层面上)可能会有多个层次, 但是从线程池的角度, 只会有两层
 *    
 * @author wuqq
 *
 */
public interface Reactor {

	

	/**
	 * 
	 * @param name
	 */
	void 			setName(String name) ;
	
	
	/**
	 * 
	 * @param ioWorker
	 */
	Reactor 			setIoWorker(IoWorker ioWorker) ;
	
	/**
	 * 
	 */
	void			start() ;
	
	
	/**
	 * 
	 */
	void 			stop() ;
	
	/**
	 * 
	 * @return
	 */
	Selector 		getSelector() ;
	
	
	/**
	 * 
	 * @param session
	 * @param ops
	 * @throws IOException
	 */
	void 			registerSession(IoSession session, int ops) throws IOException ;
	
	/**
	 * 
	 * @param channel
	 * @param ops
	 * @param attachment
	 * @throws IOException
	 */
	void 			registerChannel(SelectableChannel channel, int ops, Object attachment) throws IOException ;
	
    /**
     * 
     * @param channel
     * @throws IOException
     */
	void 			unRegisterChannel(SelectableChannel channel) ;
	
}
