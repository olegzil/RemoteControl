package com.yo.rokudevices;

import com.yo.discoverymanagers.DiscoveryManager;
import com.yo.interfaces.ConnectedDeviceInterface;
import com.yo.interfaces.ResultNotificationInterface;

import java.util.Hashtable;
public class RokuDevice implements ConnectedDeviceInterface{
	private String[] mCapabilities={"power", "channel"};
	private Hashtable<String, Object> mControllers=new Hashtable<String, Object> ();
	private Hashtable<String, String> mDeviceData;
    private Hashtable<String, String> mCommandPrefix;
	RokuBaseController mController;
	static public String POWER="power";
	static public String CHANNEL="channel";
	static public String SHOWINFO="showinfo";
	static public String COMMAND="command";

    public RokuDevice(RokuBaseController controller, Hashtable<String, String> data)
	{
		mController = controller;
        mDeviceData = data;
        mDeviceData.put("device_id", DiscoveryManager.ROKU_1);
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
