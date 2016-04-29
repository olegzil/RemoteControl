package com.yo.discoverymanagers;

import android.util.Log;

import com.yo.interfaces.ConnectedDeviceAvailableInterface;
import com.yo.interfaces.ConnectedDeviceInterface;
import com.yo.interfaces.DeviceFactoryInterface;
import com.yo.interfaces.DeviceIdentificationInterface;
import com.yo.interfaces.DiscoveryInterface;
import com.yo.interfaces.ResultNotificationInterface;
import com.yo.interfaces.UIThreadAccessInterface;
import com.yo.rokudevices.RokuBaseController;
import com.yo.rokudevices.RokuDevice;

import java.util.ArrayList;
import java.util.Hashtable;

public class RokuDiscoveryManager  implements DiscoveryInterface, 
											  DeviceIdentificationInterface,
											  DeviceFactoryInterface
{
	private DiscoveryAssistNetwork mDiscover;
	private ArrayList<ConnectedDeviceInterface> mRokuDevices;
	Hashtable<String, ConnectedDeviceInterface> mDevices = new Hashtable<String, ConnectedDeviceInterface>();
	
	public RokuDiscoveryManager()
	{
		StringBuffer discoveryRequest = new StringBuffer();
		discoveryRequest.append("M-SEARCH * HTTP/1.1\n");
		discoveryRequest.append("Host: 239.255.255.250:1900\n");
		discoveryRequest.append("Man: \"ssdp:discover\"\n");
		discoveryRequest.append("ST: roku:ecp\n");
		mDiscover = new DiscoveryAssistNetwork(discoveryRequest.toString(), this, this);
	}
	@Override
	public boolean Start(ConnectedDeviceAvailableInterface callback, UIThreadAccessInterface uiaccess, ResultNotificationInterface rni) {
		return mDiscover.Start(callback, uiaccess, rni);
	}

	@Override
	public boolean Stop() {
		return mDiscover.Stop();
	}

	@Override
	public Hashtable<String, String> onDeviceIdentify(String response)
	{
		if (!response.contains("200"))
			return null;
		if (!response.toLowerCase().contains("roku"))
			return null;

		Hashtable<String, String> retVal = new Hashtable<String, String>();
		String[] deviceData = null;
    	String[] parts = response.split("\n");
    	Log.i("Roku =-=-=-=-=-=", response);
    	String ip = extractIP(parts);
		if (ip == null)
			return null;
    	String port = extractPort(parts);
    	retVal.put("result", parts[0].split(" ")[1]);
    	retVal.put("descriptor", parts[3].split(":", 2)[1]);
    	retVal.put("ip", ip);
    	retVal.put("port", port);
		if (parts.length >=6)
			deviceData = parts[5].split(" ");

		if (deviceData.length > 0)
			retVal.put("device", deviceData[1]);
		if (deviceData.length > 3)
			retVal.put("description", deviceData[3]);

		return retVal;
	}

	@Override
	public ConnectedDeviceInterface onCreate(Hashtable<String, String> data)
	{
		RokuBaseController base=new RokuBaseController(data.get("ip"), data.get("port"));
		ConnectedDeviceInterface device = new RokuDevice(base, data);
		String key = data.get("ip");
		if (mDevices.containsKey(key))
			return null;
		mDevices.put(key, device);
		return device;
	}
	
	private String extractIP(String[] source)
	{
		for (int i=0; i<source.length; ++i)
			if(source[i].toUpperCase().contains("LOCATION"))
			{
				String[] parts = source[i].split(" ");
				parts = parts[1].split(":");
				return parts[0]+":"+parts[1];
			}
		return null;
	}
	private String extractPort(String[] source)
	{
		for (int i=0; i<source.length; ++i)
			if(source[i].toUpperCase().contains("LOCATION"))
			{
				String[] parts = source[i].split(" ");
				parts = parts[1].split(":");
				return parts[2].substring(0,  parts[2].length()-2);
			}
		return null;
	}		
}
