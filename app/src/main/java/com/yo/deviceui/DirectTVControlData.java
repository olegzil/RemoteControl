package com.yo.deviceui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yo.discoverymanagers.DiscoveryManager;
import com.yo.interfaces.ConnectedDeviceInterface;
import com.yo.remotecontrol.R;

/**
 * Created by oleg on 8/11/15.
 */
public class DirectTVControlData extends android.app.DialogFragment {
    ConnectedDeviceInterface mDevice;
    com.yo.deviceui.DirectTVInfoAndChannelsUI mController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo);
        Bundle b = this.getArguments();
        initDeviceReference(b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mController = new com.yo.deviceui.DirectTVInfoAndChannelsUI(mDevice);
        View v = inflater.inflate(mController.GetLayoutID(), container, false);
        TextView tv = (TextView) v.findViewById(R.id.text_telemetry);
        tv.setMovementMethod(new ScrollingMovementMethod());

        if (mDevice == null) {
            Bundle b = this.getArguments();
            initDeviceReference(b);
        }

        mController.Show(v);
        return v;
    }
    private void initDeviceReference(Bundle b)
    {
        Long key = b.getLong("device"); //get the device key
        mDevice = DiscoveryManager.getInstance().GetDeviceReference(key);
    }
}