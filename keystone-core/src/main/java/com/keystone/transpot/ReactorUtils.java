package com.keystone.transpot;

public class ReactorUtils {

	
	
	/**
	 * 根据CPU Processor可用个数计算Reactor个数
	 * @return
	 */
	public static int calRactorCount() {	
		int processors = Runtime.getRuntime().availableProcessors();
		return processors > 8 ? 4 + (processors * 5 / 8) : processors + 1;
	}
}
