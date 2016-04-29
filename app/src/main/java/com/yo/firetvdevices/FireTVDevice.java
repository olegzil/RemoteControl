package com.yo.firetvdevices;

import com.yo.discoverymanagers.DiscoveryManager;
import com.yo.interfaces.ConnectedDeviceInterface;
import com.yo.interfaces.ResultNotificationInterface;

import java.util.Hashtable;

/**
 * Created by oleg on 8/24/15.
 */
public class FireTVDevice implements ConnectedDeviceInterface {
    private String[] mCapabilities={"power", "channel"};
    private Hashtable<String, Object> mControllers=new Hashtable<String, Object> ();
    private Hashtable<String, String> mDeviceData;
    FireStickBaseController mController;
    public FireTVDevice(FireStickBaseController controller)
    {
        mController = controller;
        mDeviceData.put("device_id", DiscoveryManager.AMAZONFILRESTICK);
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
