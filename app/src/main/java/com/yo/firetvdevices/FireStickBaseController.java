package com.yo.firetvdevices;

import android.bluetooth.BluetoothSocket;

import com.yo.interfaces.ConnectedDeviceCommandInterface;
import com.yo.interfaces.ResultNotificationInterface;

import java.io.IOException;

/**
 * Created by oleg on 9/1/15.
 */
public class FireStickBaseController implements ConnectedDeviceCommandInterface {
    private BluetoothSocket mSocket;
    public FireStickBaseController(BluetoothSocket btSocket)
    {
        mSocket = btSocket;
        try {
            mSocket.connect();
        } catch (IOException e) {
            try {
                mSocket.close();
            } catch (IOException closeException) { }
        }
    }
    @Override
    public boolean execute(String command, ResultNotificationInterface response, String parameters, int userData) {
        return false;
    }
}
