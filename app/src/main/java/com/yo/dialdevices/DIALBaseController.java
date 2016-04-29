package com.yo.dialdevices;

//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

public class DIALBaseController {
	private String mEndpoint=null;
	private String mResultFromserver = null;
	private JSONObject mParser= null;
	private String mErrorMessage=null;
	static public final String QUERY="query";
	static public final String LAUNCH="launch";
	static public final String STOP="stop";

	public DIALBaseController(String endpoint)
	{
		mEndpoint = endpoint;
	}
	public boolean serverRequest(String cmd)
	{
		String command = mEndpoint + cmd;
		boolean retVal=true;
		URL url;
	    HttpURLConnection conn;
	    BufferedReader rd;
	    String line;
	    StringBuffer data=null;	   	    
        Socket socket = null;
        mErrorMessage = null;
        try {
			url = new URL(command);
	        conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");
	        String urlParameters = mEndpoint;
	        conn.setDoOutput(true);
	        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
	        wr.writeBytes(urlParameters);
	        wr.flush();
	        wr.close();
	        
	        data = new StringBuffer();
	        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        while ((line = rd.readLine()) != null) {
	        	data.append(line);
	        }
	        this.mResultFromserver = data.toString();
	        rd.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			mErrorMessage = e.getMessage();
			retVal = false;
		} 
		finally
		{
			if (socket != null && socket.isClosed() == false)
				try {
					socket.close();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
            System.out.println(data);
		}
		return retVal;		
	}
	public String getErrorMessage()
	{
		return mErrorMessage;
	}

	private Map<?,?> convertToMap(JSONObject o)
	{
		Hashtable<String, String> map=new Hashtable<String, String>();
		while (o.keys().hasNext())
		{
			try {
				String key = o.keys().next();
				map.put(key, o.getString(key));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	public Map<?, ?> getPayloadJSON()
	{
        try {
			mParser = new JSONObject(mResultFromserver);
			return convertToMap(mParser);
		} catch (JSONException e) {
			System.out.println(e.getMessage());
			mErrorMessage = e.getMessage();
		}
		return null;
	}
	public Map<?, ?> getPayloadXML()
	{
		Document doc = Jsoup.parse(mResultFromserver);
		Elements elements = doc.select("app");
		Hashtable<String, String> data = new Hashtable<String, String>();
		for (Element e : elements)
		{
			String key = e.attr("id");
			data.put(key, e.text());
		}
		return data;
	}
}
