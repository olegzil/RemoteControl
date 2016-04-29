package com.yo.directtvdevices;

import com.yo.interfaces.ConnectedDeviceCommandInterface;
import com.yo.interfaces.ResultNotificationInterface;
import com.yo.utilities.ResultPropagator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

public class DirectTVBaseController implements ConnectedDeviceCommandInterface {
	private String mCommandPrefix=null;
	private Map<?,?> mPayload;
	private JSONObject mParser=null;
	private String mErrorMessage=null;
	private class commandDescriptor
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
			put(CHANNELUP, new commandDescriptor("remote/processKey?key", "GET", CHANNELUP));
			put(CHANNELDOWN,new commandDescriptor("remote/processKey?key", "GET", CHANNELDOWN));
			put(POWER,new commandDescriptor("remote/processKey?key", "GET", POWER));
			put(POWERON,new commandDescriptor("remote/processKey?key", "GET", POWERON));
			put(PLAY,new commandDescriptor("remote/processKey?key", "GET", PLAY));
			put(SELECT,new commandDescriptor("remote/processKey?key", "GET", SELECT));
			put(POWEROFF,new commandDescriptor("remote/processKey?key", "GET", POWEROFF));
			put(FORMAT,new commandDescriptor("remote/processKey?key", "GET", FORMAT));
			put(PAUSE,new commandDescriptor("remote/processKey?key", "GET", PAUSE));
			put(REVIEW,new commandDescriptor("remote/processKey?key", "GET", REVIEW));
			put(REPLAY,new commandDescriptor("remote/processKey?key", "GET", REPLAY));
			put(STOP,new commandDescriptor("remote/processKey?key", "GET", STOP));
			put(ADVANCE,new commandDescriptor("remote/processKey?key", "GET", ADVANCE));
			put(FASTFWD,new commandDescriptor("remote/processKey?key", "GET", FASTFWD));
			put(RECORD, new commandDescriptor("remote/processKey?key", "GET", RECORD));
			put(PLAY,new commandDescriptor("remote/processKey?key", "GET", PLAY));
			put(GUIDE,new commandDescriptor("remote/processKey?key", "GET", GUIDE));
			put(ACTIVE,new commandDescriptor("remote/processKey?key", "GET", ACTIVE));
			put(LIST,new commandDescriptor("remote/processKey?key", "GET", LIST));
			put(EXIT,new commandDescriptor("remote/processKey?key", "GET", EXIT));
			put(BACK,new commandDescriptor("remote/processKey?key", "GET", BACK));
			put(MENU,new commandDescriptor("remote/processKey?key", "GET", MENU));
			put(INFO,new commandDescriptor("remote/processKey?key", "GET", INFO));
			put(ARROWUP,new commandDescriptor("remote/processKey?key", "GET", ARROWUP));
			put(ARROWDOWN,new commandDescriptor("remote/processKey?key", "GET", ARROWDOWN));
			put(ARROWLEFT,new commandDescriptor("remote/processKey?key", "GET", ARROWLEFT));
			put(ARROWRIGHT,new commandDescriptor("remote/processKey?key", "GET", ARROWRIGHT));
			put(SELECT,new commandDescriptor("remote/processKey?key", "GET", SELECT));
			put(RED,new commandDescriptor("remote/processKey?key", "GET", RED));
			put(GREEN,new commandDescriptor("remote/processKey?key", "GET", GREEN));
			put(YELLOW,new commandDescriptor("remote/processKey?key", "GET", YELLOW));
			put(BLUE,new commandDescriptor("remote/processKey?key", "GET", BLUE));
			put(PREV,new commandDescriptor("remote/processKey?key", "GET", PREV));
			put(DASH,new commandDescriptor("remote/processKey?key", "GET", DASH));
			put(ENTER,new commandDescriptor("remote/processKey?key", "GET", ENTER));
		}};


    private Hashtable<String, commandDescriptor> mInfo = new Hashtable<String, commandDescriptor>()
    {{
        put(TUNE,new commandDescriptor("tv/tune?major=", "GET", null));
        put(PROGRAMINFO,new commandDescriptor("tv/getProgInfo?major=", "GET", null));
        put(SERIALNUMBER, new commandDescriptor("info/getSerialNum", "GET", null));
        put(GETTUNED, new commandDescriptor("tv/getTuned", "GET", null));
        put(GETVERSION, new commandDescriptor("info/getVersion", "GET", null));
        put(MODE, new commandDescriptor("info/mode", "GET", null));
        put(GETLOCATION, new commandDescriptor("info/getLocations", "GET", null));
    }};

	public static String CHANNELUP="chanup";
	public static String CHANNELDOWN="chandown";
	public static String POWER="power";
	public static String POWERON="poweron";
	public static String POWEROFF="poweroff";
	public static String FORMAT="format";
	public static String PAUSE="pause";
	public static String REVIEW="rew";
	public static String REPLAY="replay";
	public static String STOP="stop";
	public static String ADVANCE="advance";
	public static String FASTFWD="ffwd";
	public static String RECORD="record";
	public static String PLAY="play";
	public static String GUIDE="guide";
	public static String ACTIVE="active";
	public static String LIST="list";
	public static String EXIT="exit";
	public static String BACK="back";
	public static String MENU="menu";
	public static String INFO="info";
	public static String ARROWUP="up";
	public static String ARROWDOWN="down";
	public static String ARROWLEFT="left";
	public static String ARROWRIGHT="right";
	public static String SELECT="select";
	public static String RED="red";
	public static String GREEN="green";
	public static String YELLOW="yellow";
	public static String BLUE="blue";
	public static String PREV="prev";
	public static String DASH="dash";
	public static String ENTER="enter";

    public static String PROGRAMINFO="programinfo";
    public static String TUNE="tune";
    public static String GETTUNED="gettuned";
    public static String SERIALNUMBER="serialnum";
    public static String GETVERSION="info/getVersion";
    public static String MODE="info/mode";
    public static String GETLOCATION="info/getLocations";

	private JSONObject formatJSON(String key, String data, int userData)
	{
        JSONObject json = new JSONObject();
        try {
            json.put(key, data);
            json.put("user-data", userData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

	public DirectTVBaseController(String ip, String portNumber)
	{
		mCommandPrefix = String.format("%s:%s/", ip, portNumber);
	}

	public boolean serverRequest(String cmd, final ResultNotificationInterface srh, String requestType, final int userData)
	{
		final String command = mCommandPrefix + cmd;
		boolean retVal=true;
        mErrorMessage = null;
        mPayload = null;
        final String request = requestType;

        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                HttpURLConnection conn;
                BufferedReader rd;
                String line;
                StringBuffer data=null;
                Socket socket = null;
                boolean error=true;
                try {
                    url = new URL(command);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod(request);
                    data = new StringBuffer();
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = rd.readLine()) != null) {
                        data.append(line);
                    }
                    mParser = new JSONObject(data.toString());
                    JSONObject status = mParser.getJSONObject("status");
                    if (status.getInt("code") == 200)
                    {
                        mParser.remove("status");
                        mParser.put("user-data", userData);
                        if (srh != null)
                            (new ResultPropagator()).postResultSuccess(srh, mParser);
                    }
                    else
                    {
                        status.put("user-data", userData);
                        status.put("error", status.getString("msg")); // copy the value of msg to error for consistency
                        if (srh != null)
                            (new ResultPropagator()).postResultError(srh, status);
                    }
                    rd.close();
                    error = false;

                }
                catch (IOException e) {
                    String exceptionName = e.getClass().getName();
                    error = false;
                    System.out.println(e.getMessage());
                    mErrorMessage = exceptionName + "\n" +e.getMessage();
                    if (srh != null)
						(new ResultPropagator()).postResultError(srh, formatJSON("error", mErrorMessage, userData));


                } catch (JSONException e) {
                    error = false;
                    System.out.println(e.getMessage());
                    String exceptionName = e.getClass().getName();
                    mErrorMessage = exceptionName + "\n" +e.getMessage();
					if (srh != null) {
                        (new ResultPropagator()).postResultError(srh, formatJSON("error", mErrorMessage, userData));
                    }
                }
                catch (Exception e){
                    error = false;
                    String exceptionName = e.getClass().getName();
                    mErrorMessage = exceptionName + "\n" +e.getMessage();
                    if (srh != null)
                        (new ResultPropagator()).postResultError(srh, formatJSON("error", mErrorMessage, userData));
                }
                finally {
                    if (error)
                        (new ResultPropagator()).postResultError(srh, formatJSON("error", mErrorMessage, userData));

                    if (socket != null && socket.isClosed() == false)
                        try {
                            if (socket != null)
                                socket.close();
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    System.out.println(data);
                }
            }
        }).start();
		return true;
	}
	public String getErrorMessage()
	{
		return mErrorMessage;
	}
	
	public Map<?, ?> getPayload()
	{
		return mPayload;
	}

	@Override
	public boolean execute(String command, ResultNotificationInterface response, String parameters, int userData) {
		commandDescriptor descriptor = getCommandPrefix(command);
		String cmd;
		cmd = descriptor.mCommand != null ? descriptor.mPrefix + "=" + descriptor.mCommand : descriptor.mPrefix;
		if (parameters != null)
			cmd += parameters;
		return serverRequest(cmd, response, descriptor.mRequestType, userData);
	}

	private commandDescriptor getCommandPrefix(String cmd)
	{
        if (mInfo.containsKey(cmd))
            return mInfo.get(cmd);
		return mPrefix.get(cmd);
	}
}
