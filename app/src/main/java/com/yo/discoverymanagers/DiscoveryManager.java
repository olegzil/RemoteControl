package com.yo.discoverymanagers;

import android.app.Activity;
import android.content.Intent;

import com.yo.deviceui.DirectTVNativeUI;
import com.yo.deviceui.FirestickNativeUI;
import com.yo.deviceui.RokuNativeUI;
import com.yo.interfaces.ActivityNotificationInterface;
import com.yo.interfaces.ConnectedDeviceAvailableInterface;
import com.yo.interfaces.ConnectedDeviceInterface;
import com.yo.interfaces.DeviceUIInterface;
import com.yo.interfaces.DiscoveryInterface;
import com.yo.interfaces.ResultNotificationInterface;
import com.yo.interfaces.UIThreadAccessInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

public class DiscoveryManager implements ConnectedDeviceAvailableInterface{
	static DiscoveryManager mInstance = null;
    private Activity mActivity=null;
	ArrayList<DiscoveryInterface> mDiscoveryManagers;

	Hashtable<String, ConnectedDeviceAvailableInterface> mNotifiers;
	Hashtable<Long, DeviceDescriptor> mDevices = new Hashtable<>();
    Hashtable<Integer, ActivityNotificationInterface> mActivityNotification = new Hashtable<>();

	ConnectedDeviceAvailableInterface mConnectedDeviceFoundCallback=null;
    UIThreadAccessInterface mUIThreadAccess=null;

    static public final String ROKU_1="8F0F7E40-3880-4B1B-AFC8-B8A26267B100"; //basic roku device
    static public final String ROKU_DIAL="9A174B78-87F6-46DB-B750-532DF6CBEA98"; //Roku DIAL device
    static public final String DIRECTTV="FBF992F6-AB0C-418F-A359-06E7C68BDAC5"; //basic DirectTV device
	static public final String AMAZONFILRESTICK="26AAAD3D-500A-4F2A-B243-17D0277CB880"; //basic Fire Stick device

    private class DeviceDescriptor
    {
        public DeviceDescriptor()
        {
            device = null;
            deviceUI = null;
        }
        public DeviceDescriptor(ConnectedDeviceInterface d, DeviceUIInterface u)
        {
            device = d;
            deviceUI = u;
        }
        public ConnectedDeviceInterface device;
        public DeviceUIInterface deviceUI;
        public int mDeviceIndex = 0;
    }

    private DiscoveryManager(){}
	static public DiscoveryManager getInstance()
	{
		if (mInstance == null)
		{
			mInstance = new DiscoveryManager();
			mInstance.initialize();
		}
		return mInstance;
	}
	private void initialize()
	{
		mNotifiers = new Hashtable<String, ConnectedDeviceAvailableInterface>();
		mDiscoveryManagers = new ArrayList<DiscoveryInterface>();
		mDiscoveryManagers.add(new DirectTVDiscoveryManager());
		mDiscoveryManagers.add(new RokuDiscoveryManager());
//		mDiscoveryManagers.add(new DialDiscoveryManager());
//		mDiscoveryManagers.add(new FireStickDiscoveryManager());
	}

    private DeviceUIInterface GetUIObjectForDevice(ConnectedDeviceInterface cdi, 	android.support.v4.app.FragmentActivity activity)
    {
        for (Long key : mDevices.keySet())
        {
            if (cdi.GetDeviceID().equals(mDevices.get(key).device.GetDeviceID())){
                return mDevices.get(key).deviceUI;
            }
        }
        return null;
    }
    private DeviceDescriptor createUIObject(ConnectedDeviceInterface cdi)
    {
        DeviceDescriptor retVal = null;
        if (cdi.GetDeviceID().equals(ROKU_1))
            retVal = new DeviceDescriptor(cdi, new RokuNativeUI(cdi));
        else if (cdi.GetDeviceID().equals(DIRECTTV))
            retVal = new DeviceDescriptor(cdi, new DirectTVNativeUI(cdi, mActivity));
        else if (cdi.GetDeviceID().equals(AMAZONFILRESTICK))
            retVal = new DeviceDescriptor(cdi, new FirestickNativeUI(cdi));
        return retVal;
    }

    public Long GetDeviceFromKey(ConnectedDeviceInterface cdi)
    {
        for (Long key : mDevices.keySet())
            if (mDevices.get(key).device == cdi)
                return key;
        return 0L;
    }
	public DeviceUIInterface GetUIObjectForUUID(String uuid)
	{
        for (Long key : mDevices.keySet())
            if (mDevices.get(key).device.GetDeviceID().equals(uuid))
                return mDevices.get(key).deviceUI;
		return null;
	}

    public void SetUIAccess(UIThreadAccessInterface uti)
    {
        mUIThreadAccess = uti;
    }

	public boolean AddDeviceFoundListener(String key, ConnectedDeviceAvailableInterface callback)
	{
		if (mNotifiers.containsKey(key))
			return false;
		mNotifiers.put(key, callback);
		return true;
	}
	public boolean RemoveDeviceFoundListener(String key)
	{
		if (!mNotifiers.containsKey(key))
			return false;
		mNotifiers.remove(key);
		return true;
	}
	public void Start(Activity activity, ResultNotificationInterface rni)
	{
        mDevices.clear();

        mActivity = activity;
		for (DiscoveryInterface item : mDiscoveryManagers)
			item.Start(this, mUIThreadAccess, rni);
        try {
            if (mDevices.size() > 0){
                JSONObject json = new JSONObject();
                json.put("value", mDevices.size());
                rni.onSuccess(json);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}

	public void Shutdown()
	{
		for (DiscoveryInterface item : mDiscoveryManagers)
			item.Stop();
        mDevices.clear();
	}

    public DeviceUIInterface GetDeviceUI(Long key)
    {
        if (mDevices.containsKey(key))
            return mDevices.get(key).deviceUI;
        return null;
    }
	@Override
	public synchronized Long onDeviceFound(ConnectedDeviceInterface cdi) {
        Long key = new Long(System.currentTimeMillis());
        DeviceDescriptor descriptor = createUIObject(cdi);
        descriptor.mDeviceIndex = mDevices.size();
        mDevices.put(key, descriptor);

		for (String k: mNotifiers.keySet())
			mNotifiers.get(k).onDeviceFound(cdi);
        return key;
	}
	public UIThreadAccessInterface getUIThreadAccess()
	{
		return mUIThreadAccess;
	}
	public ConnectedDeviceInterface GetDeviceReference(long key){
		ConnectedDeviceInterface retVal = null;
		if (!mDevices.containsKey(key))
			return null;
		return mDevices.get(key).device;
	}

    public boolean RegisterForActivityNotification(ActivityNotificationInterface ani, Integer requestCode)
    {
        if (mActivityNotification.containsKey(requestCode))
            return false;
        mActivityNotification.put(requestCode, ani);
        return true;
    }
    public boolean UnregisterForActivityNotification(Integer requestCode)
    {
        if (!mActivityNotification.containsKey(requestCode))
            return false;
        mActivityNotification.remove(requestCode);
        return true;
    }
    public boolean postActivityResult(final Intent data, Integer requestCode, int success)
    {
        if (!mActivityNotification.containsKey(requestCode))
            return false;
        final ActivityNotificationInterface ani = mActivityNotification.get(requestCode);

        switch (success)
        {
            case Activity.RESULT_OK:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ani.onSuccess(data);
                    }
                }).start();
                break;
            case Activity.RESULT_CANCELED:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ani.onSuccess(data);
                    }
                }).start();

        }
        return true;
    }
}
