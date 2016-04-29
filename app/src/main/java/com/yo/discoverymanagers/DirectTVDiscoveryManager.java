package com.yo.discoverymanagers;

import android.util.Log;

import com.yo.directtvdevices.DirectTVBaseController;
import com.yo.directtvdevices.DirectTVDevice;
import com.yo.interfaces.ConnectedDeviceAvailableInterface;
import com.yo.interfaces.ConnectedDeviceInterface;
import com.yo.interfaces.DeviceFactoryInterface;
import com.yo.interfaces.DeviceIdentificationInterface;
import com.yo.interfaces.DiscoveryInterface;
import com.yo.interfaces.ResultNotificationInterface;
import com.yo.interfaces.UIThreadAccessInterface;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Hashtable;

public class DirectTVDiscoveryManager implements DiscoveryInterface, 
												 DeviceFactoryInterface, 
												 DeviceIdentificationInterface{
	private DiscoveryAssistNetwork mDiscover;
	private Hashtable<String, ConnectedDeviceInterface> mDevices = new Hashtable<>();
    private Hashtable<String, String> mDeviceIP = new Hashtable<>();

	public DirectTVDiscoveryManager()
	{
		StringBuffer discoveryRequest = new StringBuffer();
		discoveryRequest.append("M-SEARCH * HTTP/1.1\r\n");
		discoveryRequest.append("HOST: 239.255.255.250:1900\r\n");
		discoveryRequest.append("MAN: \"ssdp:discover\"\r\n");
		discoveryRequest.append("MX: 3\r\n");
		discoveryRequest.append("ST: urn:schemas-upnp-org:device:MediaRenderer:1\r\n\r\n");
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
	public ConnectedDeviceInterface onCreate(Hashtable<String, String> data) {
		DirectTVBaseController base=new DirectTVBaseController(data.get("ip"), data.get("port"));
		ConnectedDeviceInterface device = new DirectTVDevice(base, data);
		String key = data.get("ip");
		if (mDevices.containsKey(key))
			return null;
		mDevices.put(key, device);
		return device;
	}
	@Override
	public Hashtable<String, String> onDeviceIdentify(String data) {
        if (!data.toLowerCase().contains("200 ok"))
            return null;
		if (!data.toLowerCase().contains("directv"))
			return null;


		Hashtable<String, String> retVal = new Hashtable<String, String>();
    	String[] parts = data.split("\n");
    	Log.i("DirectTV=-=-=-=-=", data);
    	String ip = extractIP(parts);
		if (ip == null)
			return null;
    	if (mDeviceIP.containsKey(ip))
    		return null; //don't store the device more then once. There will be only one device per IP.
        mDeviceIP.put(ip, ip);
    	String descriptor = findDeviceURL(parts);
		if (descriptor == null)
			return null;

    	retVal.put("result", parts[0].split(" ")[1]);
    	retVal.put("descriptor", descriptor);
    	retVal.put("ip", ip);
    	String xmlData = readContent(retVal.get("descriptor"));
    	Document doc = Jsoup.parse(xmlData);
    	retVal.put("device", doc.select("manufacturer").text());
    	retVal.put("description", doc.select("modelDescription").text());
    	retVal.put("model", doc.select("modelNumber").text());
    	retVal.put("port", "8080");
		return retVal;
	}

	private String findDeviceURL(String[] parts)
	{
		for (int i = 0; i<parts.length; ++i)
			if (parts[i].toLowerCase().contains("description.xml"))
			{
				int index = parts[i].toLowerCase().indexOf(':');
				if (index<0)
					return null;

				String retVal = parts[i].toLowerCase().substring(index+1);
				return retVal;
			}
		return null;
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
	private String readContent(String url){
		URL source;
		String inputLine=null;
		String data="";
		BufferedReader in;
		try {
			source = new URL(url);
			in = new BufferedReader(new InputStreamReader(source.openStream()));
			while ((inputLine = in.readLine()) != null)
				data += inputLine + "\n";
			System.out.println(data);
			in.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return data;
	}
	
}
