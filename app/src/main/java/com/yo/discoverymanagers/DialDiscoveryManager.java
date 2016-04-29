package com.yo.discoverymanagers;

import com.yo.dialdevices.DIALBaseController;
import com.yo.dialdevices.DIALDevice;
import com.yo.interfaces.ConnectedDeviceAvailableInterface;
import com.yo.interfaces.ConnectedDeviceInterface;
import com.yo.interfaces.DeviceFactoryInterface;
import com.yo.interfaces.DeviceIdentificationInterface;
import com.yo.interfaces.DiscoveryInterface;
import com.yo.interfaces.ResultNotificationInterface;
import com.yo.interfaces.UIThreadAccessInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;

public class DialDiscoveryManager  implements DiscoveryInterface,
											  DeviceFactoryInterface,
											  DeviceIdentificationInterface{
	
	private String mTarget = "urn:dial-multiscreen-org:service:dial:1";
	private String[] mExtra={"USER-AGENT: OS/version product/version"};
	private DiscoveryAssistNetwork mDiscover;
	private Hashtable<String, ConnectedDeviceInterface> mDevices = new Hashtable<String, ConnectedDeviceInterface>();

	public DialDiscoveryManager()
	{
		mDiscover = new DiscoveryAssistNetwork(mTarget, this, this);
	}
	private String parseHeader(String key, String separator, String[] source)
	{
		for (String token : source)
			if (token.toLowerCase().contains(key.toLowerCase()))
			{
				String[] parts = token.split(separator);
				String retVal="";
				for (int i=1; i<parts.length; ++i)
					retVal += parts[i]+separator;
				return retVal;
			}
		return null;
	}
	@Override
	public Hashtable<String, String> onDeviceIdentify(String response)
	{
		if (!response.toLowerCase().contains("dial-multtiscreen-org"))
			return null;
		Hashtable<String, String> retVal = new Hashtable<String, String>();
    	String[] parts = response.split("\n");
    	retVal.put("result", parseHeader("HTTP/1.1", " ", parts));
    	retVal.put("cache-control", parseHeader("Cache-Control:", " ", parts));
    	retVal.put("st", parseHeader("ST:", " ", parts));
    	retVal.put("usn", parseHeader("usn:", " ", parts));
    	retVal.put("server", parseHeader("server:", " ", parts));
    	retVal.put("endpoint", parseHeader("location:", " ", parts));
		String temp = retVal.get("endpoint");
		int end = nthIndexOf(temp, ':', 2);
		retVal.put("ip", temp.substring(0, end));
		retVal.put("port", temp.substring(end+1));
		retVal.put("device", "DIAL -- " + retVal.get("server"));
		retVal.put("description", "DIAL -- " + retVal.get("usn"));
		return retVal;
	}

	public void sendChannelInfoRequest(String url, String command) {
        try {
            URL obj = new URL(url + "/2DVideo");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

	@Override
	public ConnectedDeviceInterface onCreate(Hashtable<String, String> data) {
        String key = data.get("ip");
        if (mDevices.containsKey(key)) //if this is a duplicate device, don't create it again.
            return null;

		DIALBaseController base=new DIALBaseController(data.get("endpoint"));
		ConnectedDeviceInterface device = new DIALDevice(base, data);

		mDevices.put(key, device);
		return device;
	}

	@Override
	public boolean Start(ConnectedDeviceAvailableInterface callback, UIThreadAccessInterface uiaccess, final ResultNotificationInterface rni) {
		return mDiscover.Start(callback, uiaccess, rni);
	}

	@Override
	public boolean Stop() {
		return mDiscover.Stop();
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
	
	private int nthIndexOf(String haystack, char needle, int n)
	{
		if (n <= 0)
			return -1;
		
		int tally = 0;
		for (int i=0; i<haystack.length(); ++i)
		{
			if (haystack.charAt(i) == needle){
				tally++;
				if (tally == n)
					return i;
			}
		}
		return -1;
	}
}
