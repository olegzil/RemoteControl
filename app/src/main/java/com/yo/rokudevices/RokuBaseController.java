package com.yo.rokudevices;

import com.yo.interfaces.ConnectedDeviceCommandInterface;
import com.yo.interfaces.ResultNotificationInterface;
import com.yo.utilities.ResultPropagator;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;


public class RokuBaseController implements ConnectedDeviceCommandInterface {
	private String mCommandPrefix=null;
	String mResultFromserver = null;
	private JSONObject mParser=null;
	private String mErrorMessage=null;
    public static String ENTER="Enter";
    public static String HOME="Home";
    public static String REVERS="Rev";
    public static String FORWARD="Fwd";
    public static String PLAY="Play";
    public static String SELECT="Select";
    public static String LEFT="Left";
    public static String RIGHT="Right";
    public static String DOWN="Down";
    public static String UP="Up";
    public static String BACK="Back";
    public static String REPLAY="InstantReplay";
    public static String INFO="Info";
    public static String BACKSPACE="Backspace";
    public static String SEARCH="Search";
    public static String ALPHA_PREFIX="Lit_=";
    public static String APPS="apps";
    public static String CHANNEL="channel";
    public static String ICON="icons";
    public static String LAUNCH="launch";
    public class commandDescriptor
    {
        public String mPrefix, mRequestType, mCommand;
        public commandDescriptor(String prefix, String requestType, String command)
        {
            mPrefix = prefix;
            mRequestType = requestType;
            mCommand = command;
        }
    }
    private Hashtable<String, commandDescriptor> mPrefix = new Hashtable<String, commandDescriptor>()
    {{
            put(ENTER, new commandDescriptor("keypress", "POST", ENTER));
            put(HOME,new commandDescriptor("keypress", "POST", HOME));
            put(REVERS,new commandDescriptor("keypress", "POST", REVERS));
            put(FORWARD,new commandDescriptor("keypress", "POST", FORWARD));
            put(PLAY,new commandDescriptor("keypress", "POST", PLAY));
            put(SELECT,new commandDescriptor("keypress", "POST", SELECT));
            put(LEFT,new commandDescriptor("keypress", "POST", LEFT));
            put(RIGHT,new commandDescriptor("keypress", "POST", RIGHT));
            put(DOWN,new commandDescriptor("keypress", "POST", DOWN));
            put(UP,new commandDescriptor("keypress", "POST", UP));
            put(BACK,new commandDescriptor("keypress", "POST", BACK));
            put(REPLAY,new commandDescriptor("keypress", "POST", REPLAY));
            put(INFO, new commandDescriptor("query", "GET", APPS));
            put(BACKSPACE,new commandDescriptor("keypress", "POST", BACKSPACE));
            put(SEARCH, new commandDescriptor("keypress", "POST", SEARCH));
            put(CHANNEL,new commandDescriptor("query", "GET", CHANNEL));
            put(ICON,new commandDescriptor("query", "GET", ICON));
            put(APPS,new commandDescriptor("query", "GET", APPS));
            put(LAUNCH,new commandDescriptor("launch", "POST", ""));
    }};

    @Override
    public boolean execute(String command, ResultNotificationInterface response, String parameters, int userData) {
        commandDescriptor descriptor = getCommandPrefix(command);
        String cmd;
        if (parameters == null)
            cmd = String.format("%s/%s", descriptor.mPrefix, descriptor.mCommand);
        else
            cmd = String.format("%s/%s", descriptor.mPrefix, parameters);
        return serverRequest(cmd, response, descriptor.mRequestType, userData);
    }

	public RokuBaseController(String ip, String portNumber)
	{
		mCommandPrefix = String.format("%s:%s/", ip, portNumber);
	}
    private JSONObject toJSON(JSONObject result, String key, String value)
    {
        try {
            result.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
	public boolean serverRequest(String cmd, final ResultNotificationInterface srh, String requestType, final int userData)
	{
		final String command = mCommandPrefix + cmd;
		boolean retVal=true;
        mErrorMessage = null;

        final String request = requestType;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn;
                URL url;
                BufferedReader rd;
                String line;
                StringBuffer data=null;
                Socket socket = null;
                try {
                    url = new URL(command);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod(request);
                    data = new StringBuffer();
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = rd.readLine()) != null) {
                        data.append(line);
                    }
                    mResultFromserver = data.toString();
                    rd.close();
                    if (srh != null) {
                        JSONObject json=new JSONObject();
                        json.put("user-data", userData);
                        (new ResultPropagator()).postResultSuccess(srh, getPayloadAsJSON(mResultFromserver));
                    }
                } catch (IOException e) {
                    mErrorMessage = e.getMessage();
                    if (srh != null) {
                        JSONObject json = new JSONObject();
                        json = toJSON(json, "error", mErrorMessage);
                        json = toJSON(json, "user-data", Integer.toString(userData));
                        (new ResultPropagator()).postResultError(srh, json);
                    }
                }
                catch (Exception e)
                {
                    if (srh != null) {
                        JSONObject json = new JSONObject();
                        json = toJSON(json, "user-data", Integer.toString(userData));
                        (new ResultPropagator()).postResultError(srh, toJSON(json, "error", mErrorMessage));
                    }
                }
                finally
                {
                    if (socket != null && socket.isClosed() == false)
                        try {
                            socket.close();
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                }
            }
        }).start();
		return true;
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
	public JSONObject getPayloadAsJSON(String xmlString)
	{
		Document doc = Jsoup.parse(xmlString);
		Elements elements = doc.select("app");
		JSONObject data = new JSONObject();
        try {
            for (Element e : elements)
            {
                String key = e.attr("id");
                    data.put(key, e.text());
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
		return data;
	}
    public commandDescriptor getCommandPrefix(String cmd)
    {
        return mPrefix.get(cmd);
    }
}
