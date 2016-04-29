package com.yo.deviceui;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.yo.interfaces.ConnectedDeviceInterface;
import com.yo.interfaces.DeviceUIInterface;
import com.yo.interfaces.ResultNotificationInterface;
import com.yo.interfaces.ServerCallInterface;
import com.yo.remotecontrol.R;
import com.yo.rokudevices.RokuBaseController;

import org.json.JSONObject;

/**
 * Created by oleg on 8/7/15.
 */
public class FirestickNativeUI implements DeviceUIInterface {
    ConnectedDeviceInterface mDevice;
    private static final int mLayoutID = R.layout.fire_tv_direct;
    public FirestickNativeUI(ConnectedDeviceInterface cdi){
        mDevice = cdi;
    }

    private class QueryAvailablePrograms implements ServerCallInterface
    {
        @Override
        public void execute()
        {
//            FirestickDeviceSelectionDialog dlg = new FirestickDeviceSelectionDialog();
//            Long key = DiscoveryManager.getInstance().GetDeviceFromKey(mDevice);
//            Bundle args = new Bundle();
//            args.putLong("device", key);
//            dlg.setArguments(args);
//            android.support.v4.app.FragmentManager fm = com.yo.appmain.MainActivity.mThisActivity.getSupportFragmentManager();
//            dlg.show(fm, "dlg");
        }
    }

    void connectButton_2(View view, int resource, final String cmd, final String parameters, final ServerCallInterface action)
    {
        Button button = (Button) view.findViewById(resource);
        if (button == null)
        {
            Log.e("FirestickNativeUI", "Attempt to use a non-existant button resource");
            return;
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action.execute();
            }
        });
    }
    void connectButton(View view, int resource, final String cmd, final String parameters)
    {
        boolean debugFlag = false;

        Button button = (Button) view.findViewById(resource);
        if (button == null)
        {
            Log.i("FirestickNativeUI", "Attempt to use an non existant button resource");
            return;
        }
        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          mDevice.execute(cmd, parameters, new ResultNotificationInterface() {
                                              @Override
                                              public void onError(JSONObject json) {
                                              }

                                              @Override
                                              public void onSuccess(JSONObject json) {
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
    public void Show(View view) {
        connectButton(view, R.id.btn_home ,RokuBaseController.HOME, null);
        connectButton(view, R.id.btn_up ,RokuBaseController.UP, null);
        connectButton(view, R.id.btn_select ,RokuBaseController.SELECT, null);
        connectButton(view, R.id.btn_back ,RokuBaseController.BACK, null);
        connectButton(view, R.id.btn_replay ,RokuBaseController.REPLAY, null);
        connectButton(view, R.id.btn_backspace ,RokuBaseController.BACKSPACE, null);
        connectButton(view, R.id.btn_enter ,RokuBaseController.ENTER, null);
        connectButton(view, R.id.btn_down ,RokuBaseController.DOWN, null);
        connectButton(view, R.id.btn_left ,RokuBaseController.LEFT, null);
        connectButton(view, R.id.btn_right ,RokuBaseController.RIGHT, null);
        connectButton(view, R.id.btn_play ,RokuBaseController.PLAY, null);
        connectButton(view, R.id.btn_reverse ,RokuBaseController.REVERS, null);
        connectButton(view, R.id.btn_forward ,RokuBaseController.FORWARD, null);
        connectButton(view, R.id.btn_search ,RokuBaseController.SEARCH, null);
        connectButton_2(view, R.id.btn_info, RokuBaseController.INFO, null, new QueryAvailablePrograms());
    }

    @Override
    public String GetDeviceDescription() {
        return mDevice.GetDeviceName() + " " + mDevice.GetDeviceData().get("ip");
    }

    @Override
    public int GetLayoutID() {
        return mLayoutID;

    }
}
