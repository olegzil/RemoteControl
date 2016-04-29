package com.yo.deviceui;

import android.app.Activity;
import android.view.View;

import com.yo.interfaces.ConnectedDeviceInterface;
import com.yo.interfaces.DeviceUIInterface;

/**
 * Created by oleg on 8/7/15.
 */
public class ROKU_DIALNativeUI implements DeviceUIInterface {
    public ROKU_DIALNativeUI(ConnectedDeviceInterface cdi, Activity activity){

    }

    @Override
    public void Show(View view) {

    }

    @Override
    public String GetDeviceDescription() {
        return null;
    }

    @Override
    public int GetLayoutID() {
        return 0;
    }
}
