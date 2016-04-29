package com.yo.discoverymanagers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.yo.interfaces.ActivityNotificationInterface;
import com.yo.interfaces.BluetoothDeviceFactoryInterface;
import com.yo.interfaces.ConnectedDeviceAvailableInterface;
import com.yo.interfaces.ConnectedDeviceInterface;
import com.yo.interfaces.DiscoveryInterface;
import com.yo.interfaces.ResultNotificationInterface;
import com.yo.interfaces.UIThreadAccessInterface;
import com.yo.remotecontrol.MainActivity;
import com.yo.utilities.ResultPropagator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by oleg on 8/20/15.
 */
public class DiscoveryAssistBluetooth implements DiscoveryInterface,
        ActivityNotificationInterface {
    private BluetoothDeviceFactoryInterface mCreate;
    private BluetoothAdapter mBluetoothAdapter = null;
    private ArrayList<BluetoothDevice> mDebugDeviceList = new ArrayList<>();
    BroadcastReceiver mReceiver=null;
    ResultNotificationInterface mErrorCallback;

    public static Integer REQUEST_ENABLE_BT = new Integer((int)System.currentTimeMillis());

    private class BluetoothDeviceCreator extends Thread {
        private  BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final java.util.UUID MY_UUID=java.util.UUID.randomUUID();
        private BluetoothDeviceFactoryInterface mCreate;
        ResultNotificationInterface mErrorCallback;
        private ConnectedDeviceAvailableInterface mCallback;

        public BluetoothDeviceCreator(BluetoothDevice device, BluetoothDeviceFactoryInterface create, ConnectedDeviceAvailableInterface m, ResultNotificationInterface callback) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            mErrorCallback = callback;
            mmDevice = device;
            mCreate = create;
            mCallback = m;
        }

        public void run() {
            try
            {
                BluetoothSocket tmp = null;
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                // Get a BluetoothSocket to connect with the given BluetoothDevice
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
                mmSocket = tmp;
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            ;
            ConnectedDeviceInterface device = mCreate.onCreate(mmSocket);
            if (device != null) {
                ResultPropagator rp = new ResultPropagator();
                Long key = mCallback.onDeviceFound(device);
                JSONObject json = new JSONObject();
                try {
                    json.put("key", Long.toString(key));
                    rp.postResultSuccess(mErrorCallback, json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }


    public  DiscoveryAssistBluetooth(BluetoothDeviceFactoryInterface devInst)
    {
        mCreate = devInst;
    }
    @Override
    public boolean Start(ConnectedDeviceAvailableInterface device, UIThreadAccessInterface uiaccess, ResultNotificationInterface rni) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mErrorCallback = rni;
        try {
            if (mBluetoothAdapter == null) {
                JSONObject json = new JSONObject();
                json.put("error", "device does not support bluetooth");
                rni.onError(json);
                return false;
            }
            if (!mBluetoothAdapter.isEnabled()) {
                while (DiscoveryManager.getInstance().RegisterForActivityNotification(this, REQUEST_ENABLE_BT) == false)
                    REQUEST_ENABLE_BT = new Integer((int)System.currentTimeMillis()); //keep trying until you succeed.

                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                MainActivity.mThisActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else
                collectBluetoothDevices(device);

        } catch (JSONException e) {

        }

        return true;
    }

    @Override
    public boolean Stop() {
        if (mBluetoothAdapter != null)
            mBluetoothAdapter.cancelDiscovery();
        if (mReceiver != null)
            MainActivity.mThisActivity.unregisterReceiver(mReceiver);
        return true;
    }

    @Override
    public void onSuccess(Intent intent) {
    }

    @Override
    public void onFailure(Intent intent) {
        DiscoveryManager.getInstance().getUIThreadAccess().Post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.mThisActivity, "Device discovery declined", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void collectBluetoothDevices(final ConnectedDeviceAvailableInterface connectedDevice)
    {
// Create a BroadcastReceiver for ACTION_FOUND
            mReceiver = new BroadcastReceiver()
            {
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    String name=null;
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    name = device.getName();
                    if (name != null && name.toLowerCase().equals("dtvbluetooth")) {
                        mBluetoothAdapter.cancelDiscovery();
                        BluetoothDeviceCreator btc = new BluetoothDeviceCreator(device, mCreate, connectedDevice, mErrorCallback);
                        btc.start();
                    }
                }
                else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
                {
                    queryConnectedDevices();
                    StringBuffer sb = new StringBuffer();
                    for (BluetoothDevice device : mDebugDeviceList)
                    {
                        if (device.getName() != null && device.getName().toLowerCase().equals("dtvbluetooth")) {
                            mBluetoothAdapter.cancelDiscovery();
                            BluetoothDeviceCreator btc = new BluetoothDeviceCreator(device, mCreate, connectedDevice, mErrorCallback);
                            btc.start();
                            break;
                        }
                    }

                    MainActivity.mThisActivity.unregisterReceiver(mReceiver); // Don't forget to unregister during onDestroy
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        MainActivity.mThisActivity.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        mBluetoothAdapter.startDiscovery();
    }

    private void queryConnectedDevices()
    {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() <= 0)
            return;

        StringBuffer sb = new StringBuffer();

        // Loop through paired devices
        for (BluetoothDevice device : pairedDevices) {
            String item = String.format("name %s  address %s  type %d\n", device.getName(), device.getAddress(), device.getType());
            sb.append(item);
        }
        UIThreadAccessInterface uiAccess = DiscoveryManager.getInstance().getUIThreadAccess();
        final String message = sb.toString();
        uiAccess.Post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.mThisActivity, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
