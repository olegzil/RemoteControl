package com.yo.interfaces;

import android.bluetooth.BluetoothSocket;

/**
 * Created by oleg on 8/24/15.
 */
public interface BluetoothDeviceFactoryInterface {
    ConnectedDeviceInterface onCreate(BluetoothSocket device);
}
