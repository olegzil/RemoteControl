package com.yo.directtvdevices;

import com.yo.interfaces.ConnectedDeviceInterface;
import com.yo.interfaces.ResultNotificationInterface;

import java.util.Hashtable;

public class DirectTVDevice implements ConnectedDeviceInterface{
	private String[] mCapabilities={"power", "channel"};
	private Hashtable<String, Object> mControllers=new Hashtable<String, Object> ();
	private Hashtable<String, String> mDeviceData;
	DirectTVBaseController mController;
	public DirectTVDevice(DirectTVBaseController controller, Hashtable<String, String> data)
	{
		mController = controller;
		mDeviceData = data;
		mDeviceData.put("device_id", "FBF992F6-AB0C-418F-A359-06E7C68BDAC5");
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
		mController.execute(cmd, rni, parameters, userData);
	}

}
