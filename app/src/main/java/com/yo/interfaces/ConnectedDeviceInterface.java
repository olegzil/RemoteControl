package com.yo.interfaces;

import java.util.Hashtable;

public interface ConnectedDeviceInterface {
	public String[] GetCapabilities();
	public String GetDeviceName();
	public String GetDeviceID();
	public Hashtable<String, String> GetDeviceData();
	public void execute(String cmd, String parameters, ResultNotificationInterface rni, int userData );
}
