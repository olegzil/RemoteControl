package com.yo.deviceui;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.yo.remotecontrol.MainActivity;
import com.yo.directtvdevices.DirectTVBaseController;
import com.yo.discoverymanagers.DiscoveryManager;
import com.yo.interfaces.ConnectedDeviceInterface;
import com.yo.interfaces.DeviceUIInterface;
import com.yo.interfaces.ResultNotificationInterface;
import com.yo.remotecontrol.R;

import org.json.JSONObject;

/**
 * Created by oleg on 8/7/15.
 **/
public class DirectTVNativeUI implements DeviceUIInterface {
    private final static int mResourceID= R.layout.directtv_direct;
    private ConnectedDeviceInterface mDevice;
    private Activity mActivity;
    private View mMasterView;
    public DirectTVNativeUI(ConnectedDeviceInterface cdi, 	Activity activity) {
        mDevice = cdi;
        mActivity = activity;
    }
    void connectButton(View view, int resource, final String cmd, final String parameters)
    {
        Button button = (Button) view.findViewById(resource);
        if (button == null)
        {
            Log.i("DirectTVNativeUI", "Attempt to use an non existant button resource");
            return;
        }
        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          mDevice.execute(cmd, parameters, new ResultNotificationInterface() {
                                              @Override
                                              public void onError(JSONObject data) {
                                              }

                                              @Override
                                              public void onSuccess(JSONObject data) {
                                              }

                                              @Override
                                              public void done() {

                                              }
                                          }, 0);
                                      }
                                  }
        );
    }
    @Override
    public void Show(final View rootView) {
        connectButton(rootView, R.id.btn_power, DirectTVBaseController.POWER, null);
        connectButton(rootView, R.id.btn_poweron, DirectTVBaseController.POWERON, null);
        connectButton(rootView, R.id.btn_poweroff, DirectTVBaseController.POWEROFF, null);
        connectButton(rootView, R.id.btn_format, DirectTVBaseController.FORMAT, null);
        connectButton(rootView, R.id.btn_pause, DirectTVBaseController.PAUSE, null);
        connectButton(rootView, R.id.btn_rew, DirectTVBaseController.REVIEW, null);
        connectButton(rootView, R.id.btn_replay, DirectTVBaseController.REPLAY, null);
        connectButton(rootView, R.id.btn_stop, DirectTVBaseController.STOP, null);
        connectButton(rootView, R.id.btn_advance, DirectTVBaseController.ADVANCE, null);
        connectButton(rootView, R.id.btn_forward, DirectTVBaseController.FASTFWD, null);
        connectButton(rootView, R.id.btn_record, DirectTVBaseController.RECORD, null);
        connectButton(rootView, R.id.btn_play, DirectTVBaseController.PLAY, null);
        connectButton(rootView, R.id.btn_guide, DirectTVBaseController.GUIDE, null);
        connectButton(rootView, R.id.btn_active, DirectTVBaseController.ACTIVE, null);
        connectButton(rootView, R.id.btn_list, DirectTVBaseController.LIST, null);
        connectButton(rootView, R.id.btn_exit, DirectTVBaseController.EXIT, null);
        connectButton(rootView, R.id.btn_back, DirectTVBaseController.BACK, null);
        connectButton(rootView, R.id.btn_menu, DirectTVBaseController.MENU, null);
        connectButton(rootView, R.id.btn_info, DirectTVBaseController.INFO, null);
        connectButton(rootView, R.id.btn_up, DirectTVBaseController.ARROWUP, null);
        connectButton(rootView, R.id.btn_down, DirectTVBaseController.ARROWDOWN, null);
        connectButton(rootView, R.id.btn_left, DirectTVBaseController.ARROWLEFT, null);
        connectButton(rootView, R.id.btn_right, DirectTVBaseController.ARROWRIGHT, null);
        connectButton(rootView, R.id.btn_select,  DirectTVBaseController.SELECT, null);
        connectButton(rootView, R.id.btn_red, DirectTVBaseController.RED, null);
        connectButton(rootView, R.id.btn_green, DirectTVBaseController.GREEN, null);
        connectButton(rootView, R.id.btn_yellow,  DirectTVBaseController.YELLOW, null);
        connectButton(rootView, R.id.btn_blue, DirectTVBaseController.BLUE, null);
        connectButton(rootView, R.id.btn_prev, DirectTVBaseController.PREV, null);
        connectButton(rootView, R.id.btn_enter, DirectTVBaseController.ENTER, null);
        connectDialog(rootView, R.id.btn_gesture, new GestureDialog());

        Button otherUI = (Button) rootView.findViewById(R.id.btn_metadata);
        otherUI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DirectTVControlData dlg = new DirectTVControlData();
                Long key = DiscoveryManager.getInstance().GetDeviceFromKey(mDevice);
                Bundle args = new Bundle();
                args.putLong("device", key);
                dlg.setArguments(args);
                FragmentManager fm = MainActivity.mThisActivity.getFragmentManager();
                dlg.show(fm, "dlg");
            }
        });
    }
    @Override
    public String GetDeviceDescription()
    {
        return mDevice.GetDeviceName() + " " + mDevice.GetDeviceData().get("ip");
    }

    @Override
    public int GetLayoutID() {
        return mResourceID;
    }

    void connectDialog(View view, int resource, final DialogFragment dlg)
    {
        Button tv = (Button) view.findViewById(resource);
        if (tv == null)
        {
            Log.e("RokuNativeUI", "Attempt to user a non-existant button resource");
            return;
        }
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long key = DiscoveryManager.getInstance().GetDeviceFromKey(mDevice);
                Bundle args = new Bundle();
                args.putLong("device", key);
                args.putString("left", DirectTVBaseController.ARROWLEFT);
                args.putString("right", DirectTVBaseController.ARROWRIGHT);
                args.putString("up", DirectTVBaseController.ARROWUP);
                args.putString("down", DirectTVBaseController.ARROWDOWN);
                args.putString("enter", DirectTVBaseController.SELECT);
                args.putString("home", DirectTVBaseController.GUIDE);
                dlg.setArguments(args);
                FragmentManager fm = MainActivity.mThisActivity.getFragmentManager();
                dlg.show(fm, "dlg");
            }
        });
    }
}
