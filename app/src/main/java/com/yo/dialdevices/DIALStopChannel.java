package com.yo.dialdevices;

import java.util.Hashtable;
public class DIALStopChannel{
	private DIALBaseController mUtils;

	public DIALStopChannel(DIALBaseController rbc)
	{
		mUtils = rbc;
	}
	public boolean TestLaunch() {
		String command = formCommand("apps/2DVideo");
	    Hashtable<String, String> xmlPayload=null;
	    if (mUtils.serverRequest(command))
	    	xmlPayload = (Hashtable<String, String>)mUtils.getPayloadXML();
	    
	    return xmlPayload == null ? false : true;
	}

	private String formCommand(String cmd)
	{
		return String.format("/%s/%s", "query", cmd);
	}	
}
