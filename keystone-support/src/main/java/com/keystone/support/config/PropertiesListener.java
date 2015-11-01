package com.keystone.support.config;

import java.io.File;

public class PropertiesListener implements FileListener {

	@Override
	public void onChanged(File changed) {
		if(changed.exists())
		{
			PropertiesUtils.load(changed) ;
		}
		
	}

	
}
