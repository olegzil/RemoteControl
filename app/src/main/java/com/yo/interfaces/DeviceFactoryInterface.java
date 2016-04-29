package com.yo.interfaces;

import java.util.Hashtable;

public interface DeviceFactoryInterface {
	ConnectedDeviceInterface onCreate(Hashtable<String, String> data);

}
