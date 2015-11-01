package com.keystone.share.message.keystone;


public class KeystoneMessage {
	/** 协议 magic number */
	public static byte KEY_STONE_PROTOCOL_MAGIC_NUMBER = (byte) 'K' ;
	
	/** 协议版本号 */
	public static byte KEY_STONE_PROTOCOL_VERSION_1_2 = (byte) 0x12;
	
	/** 同步 */
	public static byte MSG_TYPE_SYNC = 0 ;
	
	/** 异步 */
	public static byte MSG_TYPE_ASYNC = 1 ;
	
	/** 并行 */
	public static byte MSG_TYPE_FUTURE = 2 ;
	
	/** 单向 */
	public static byte MSG_TYPE_SINGLE = 3 ;
	
	/**
	 * 
	 */
	private byte version ;
	
	/**
	 * 
	 */
	private int ticket ;
	
	/**
	 * 
	 */
	private byte msgType ;
	
	/**
	 * message body
	 */
	private byte[] body ;

	
	/**
	 * 
	 * @return
	 */
	public byte getVersion() {
		return version;
	}

	
	/**
	 * 
	 * @param version
	 */
	public void setVersion(byte version) {
		this.version = version;
	}

	
	/**
	 * 
	 * @return
	 */
	public int getTicket() {
		return ticket;
	}

	
	/**
	 * 
	 * @param ticket
	 */
	public void setTicket(int ticket) {
		this.ticket = ticket;
	}

	/**
	 * 
	 * @return
	 */
	public byte getMsgType() {
		return msgType;
	}

	/**
	 * 
	 * @param msgType
	 */
	public void setMsgType(byte msgType) {
		this.msgType = msgType;
	}


	/**
	 * 
	 * @return
	 */
	public byte[] getBody() {
		return body;
	}

	
	/**
	 * 
	 * @param body
	 */
	public void setBody(byte[] body) {
		this.body = body;
	}


	@Override
	public String toString() {
		return "KeystoneMessage [version=" + version + ", ticket=" + ticket + ", msgType=" + msgType + "]";
	}
	
	
	
	
	
}
