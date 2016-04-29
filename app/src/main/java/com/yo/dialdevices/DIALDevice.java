package com.yo.dialdevices;

import com.yo.interfaces.ConnectedDeviceInterface;
import com.yo.interfaces.ResultNotificationInterface;

import java.util.Hashtable;
public class DIALDevice implements ConnectedDeviceInterface{
	private String[] mCapabilities={"stop", "channel", "showinfo"};
	private Hashtable<String, Object> mControllers=new Hashtable<String, Object> ();
	private Hashtable<String, String> mDeviceData;
	DIALBaseController mController;
	static public String CHANNEL="channel";
	static public String SHOWINFO="showinfo";
	static public String COMMAND="command";

	public DIALDevice(DIALBaseController controller, Hashtable<String, String> data)
	{
		mDeviceData = data;
		mController = controller;
		mDeviceData.put("device_id", "9A174B78-87F6-46DB-B750-532DF6CBEA98");
		mControllers.put("launch-channel", new DIALLaunchChannel(mController));
		mControllers.put("command", new DIALCommandExecute(mController));
	}
	
	@Override
	public String[] GetCapabilities() {
		return mCapabilities;
	}

	@Override
	public String GetDeviceName() {
		return mDeviceData.get("device");
	}

	@Override
	public String GetDeviceID() {
		return mDeviceData.get("device_id");
	}

	@Override
	public Hashtable<String, String> GetDeviceData() {
		return mDeviceData;
	}

	@Override
	public void execute(String cmd, String parameters, ResultNotificationInterface rni, int userData) {

	}

}
