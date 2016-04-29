package com.yo.discoverymanagers;

import android.bluetooth.BluetoothSocket;

import com.yo.firetvdevices.FireStickBaseController;
import com.yo.firetvdevices.FireTVDevice;
import com.yo.interfaces.BluetoothDeviceFactoryInterface;
import com.yo.interfaces.ConnectedDeviceAvailableInterface;
import com.yo.interfaces.ConnectedDeviceInterface;
import com.yo.interfaces.DeviceIdentificationInterface;
import com.yo.interfaces.DiscoveryInterface;
import com.yo.interfaces.ResultNotificationInterface;
import com.yo.interfaces.UIThreadAccessInterface;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by oleg on 8/20/15.
 */
public class FireStickDiscoveryManager implements DiscoveryInterface,
                                                  DeviceIdentificationInterface,
                                                  BluetoothDeviceFactoryInterface
{
    private final static String TARGET = "amazonfirestick";
    private DiscoveryAssistBluetooth mDiscover;
    private ArrayList<ConnectedDeviceInterface> mBluetoothDevices;
    BluetoothSocket mSocket = null;
    Hashtable<String, ConnectedDeviceInterface> mDevices = new Hashtable<String, ConnectedDeviceInterface>();

    public FireStickDiscoveryManager()
    {
        mDiscover = new DiscoveryAssistBluetooth(this);
    }

    @Override
    public boolean Start(ConnectedDeviceAvailableInterface callback, UIThreadAccessInterface uiaccess, ResultNotificationInterface rni) {
        return mDiscover.Start(callback, uiaccess, rni);
    }

    @Override
    public boolean Stop() {
        return mDiscover.Stop();
    }

    @Override
    public ConnectedDeviceInterface onCreate(BluetoothSocket btSocket) {
        FireStickBaseController base=new FireStickBaseController(btSocket);
        ConnectedDeviceInterface device = new FireTVDevice(base);
        String key = btSocket.getRemoteDevice().getName();
        if (mDevices.containsKey(key))
            return null;
        mDevices.put(key, device);
        return device;
    }

    @Override
    public Hashtable<String, String> onDeviceIdentify(String data) {
        return null;
    }
}
