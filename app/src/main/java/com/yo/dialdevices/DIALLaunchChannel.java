package com.yo.dialdevices;

import com.yo.interfaces.DIALChannelServiceInterface;
public class DIALLaunchChannel implements DIALChannelServiceInterface{
	private DIALBaseController mUtils;

	public DIALLaunchChannel(DIALBaseController rbc)
	{
		mUtils = rbc;
	}

	private String formCommand(String cmd)
	{
		return String.format("/%s/%s", "query", cmd);
	}
	@Override
	public boolean Launch(String cmd) {
		mUtils.serverRequest(cmd);
		return true;
	}	
}
