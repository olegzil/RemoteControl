package com.yo.deviceui;

import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yo.remotecontrol.MainActivity;
import com.yo.directtvdevices.DirectTVServerQuery;
import com.yo.discoverymanagers.DiscoveryManager;
import com.yo.interfaces.ConnectedDeviceInterface;
import com.yo.interfaces.DeviceUIInterface;
import com.yo.interfaces.ResultNotificationInterface;
import com.yo.interfaces.ServerCallInterface;
import com.yo.remotecontrol.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by oleg on 8/7/15.
 */
public class DirectTVInfoAndChannelsUI implements DeviceUIInterface {
    private static final int mResourceID = R.layout.meta_data;
    private ConnectedDeviceInterface mDevice;
    private interface UIToggleInterface
    {
        void execute(boolean on_off);
    }

    public DirectTVInfoAndChannelsUI(ConnectedDeviceInterface cdi)
    {
        mDevice = cdi;
    }
    private class ChannelUp implements ServerCallInterface{
        private TextView mTargetView, mErrorView;
        private UIToggleInterface mToggle;
        public ChannelUp(TextView targetView, TextView errorView, UIToggleInterface uiToggle)
        {
            mTargetView = targetView;
            mErrorView = errorView;
            mToggle = uiToggle;
        }
        @Override
        public void execute()
        {
            DirectTVServerQuery query = new DirectTVServerQuery(mDevice);
            final TextView eView=mErrorView, tView=mTargetView;
            mToggle.execute(false);
            query.nextChannel(new ResultNotificationInterface() {
                @Override
                public void onError(JSONObject data) {
                    mToggle.execute(true);
                    try {
                        eView.setText(data.getString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSuccess(JSONObject data) {
                    mToggle.execute(true);
                    String channel = "0";
                    try {
                        channel = data.getString("value");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    tView.setText(channel);
                    eView.setText(" ");
                }

                @Override
                public void done() {

                }
            });
        }
    }

    private class ChannelDown implements ServerCallInterface{
        private TextView mTargetView, mErrorView;
        private UIToggleInterface mToggle;
        public ChannelDown(TextView targetView, TextView errorView, UIToggleInterface uiToggle)
        {
            mTargetView = targetView;
            mErrorView = errorView;
            mToggle = uiToggle;
        }
        @Override
        public void execute()
        {
            DirectTVServerQuery query = new DirectTVServerQuery(mDevice);
            final TextView eView=mErrorView, tView=mTargetView;
            mToggle.execute(false);
            query.prevChannel(new ResultNotificationInterface() {
                @Override
                public void onError(JSONObject data) {
                    mToggle.execute(true);
                    try {
                        eView.setText(data.getString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSuccess(JSONObject data) {
                    mToggle.execute(true);
                    try {
                        String channel = data.getString("value");
                        tView.setText(channel);
                        eView.setText(" ");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void done() {

                }
            });
        }
    }

    private class CurrentChannelStatus implements ServerCallInterface
    {
        private TextView mView;
        public CurrentChannelStatus(TextView view)
        {
            mView = view;
        }

        @Override
        public void execute() {
            DirectTVServerQuery query = new DirectTVServerQuery(mDevice);
            final TextView view = mView;
            query.getCurrentProgramInfo(new ResultNotificationInterface() {
                @Override
                public void onError(JSONObject data) {
                    try {
                        view.setText(data.getString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSuccess(JSONObject data) {
                    try {
                        String text = String.format("%s %s", data.getString("major"), data.getString("title"));
                        view.setText(text);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void done() {

                }
            });
        }
    }

    private class ChannelData implements ServerCallInterface
    {
        private TextView mView;
        public ChannelData(TextView view)
        {
            mView = view;
        }

        @Override
        public void execute() {
            DirectTVChannelDataDialog dlg = new DirectTVChannelDataDialog();
            Long key = DiscoveryManager.getInstance().GetDeviceFromKey(mDevice);
            Bundle args = new Bundle();
            args.putLong("device", key);
            dlg.setArguments(args);
            FragmentManager fm = MainActivity.mThisActivity.getFragmentManager();
            dlg.show(fm, "dlg");
        }
    }
    private class ClientList implements ServerCallInterface
    {
        TextView mView;

        public ClientList(TextView view)
        {
            mView = view;
        }
        @Override
        public void execute() {
            DirectTVServerQuery query = new DirectTVServerQuery(mDevice);
            final TextView view=mView;
            query.getConnectedClientList(new ResultNotificationInterface() {
                @Override
                public void onError(JSONObject data) {
                    try {
                        view.setText(data.getString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSuccess(JSONObject data) {
                    try {
                        view.setText(data.getString("value"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void done() {

                }
            });
        }
    }

    private class SerialNumber implements ServerCallInterface
    {
        private TextView mView;
        public  SerialNumber(TextView v)
        {
            mView = v;
        }
        @Override
        public void execute() {
            DirectTVServerQuery query = new DirectTVServerQuery(mDevice);
            final TextView view=mView;
            query.getSerialNumber(new ResultNotificationInterface() {
                @Override
                public void onError(JSONObject data) {
                    try {
                        view.setText(data.getString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSuccess(JSONObject data) {
                    try {
                        view.setText(data.getString("value"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void done() {

                }
            });
        }
    }

    private class DeviceVersion implements ServerCallInterface
    {
        private TextView mView;
        public  DeviceVersion(TextView v)
        {
            mView = v;
        }
        @Override
        public void execute() {
            DirectTVServerQuery query = new DirectTVServerQuery(mDevice);
            final TextView view=mView;
            query.getDeviceVersion(new ResultNotificationInterface() {
                @Override
                public void onError(JSONObject data) {
                    try {
                        view.setText(data.getString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSuccess(JSONObject data) {
                    try {
                        view.setText(data.getString("value"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void done() {

                }
            });
        }
    }

    void connectButton(final View parentView, int resource, final ServerCallInterface serverCall)
    {
        Button button = (Button) parentView.findViewById(resource);
        if (button == null)
        {
            Log.i(this.getClass().getCanonicalName(), "Resouce not found");
            return;
        }
        button.setOnClickListener(new View.OnClickListener()
                                  {
                                      @Override
                                      public void onClick(View view) {serverCall.execute();}
                                  }
        );
    }

    @Override
    public void Show(View view) {
        TextView infoView = (TextView)view.findViewById(R.id.text_telemetry);
        TextView channelView = (TextView)view.findViewById(R.id.text_channel_value);
        CurrentChannelStatus currentChannel = new CurrentChannelStatus(channelView);
        currentChannel.execute();
        final View v=view;
        UIToggleInterface uiCallback = new UIToggleInterface(){
            @Override
            public void execute(boolean on_off) {
                ((Button) v.findViewById(R.id.btn_chandown)).setEnabled(on_off);
                ((Button) v.findViewById(R.id.btn_chanup)).setEnabled(on_off);
                ((Button) v.findViewById(R.id.btn_show_serial_number)).setEnabled(on_off);
                ((Button) v.findViewById(R.id.btn_show_version)).setEnabled(on_off);
                ((Button) v.findViewById(R.id.btn_show_clients)).setEnabled(on_off);
                ((Button) v.findViewById(R.id.btn_program_data)).setEnabled(on_off);
                ((Button) v.findViewById(R.id.btn_program_time)).setEnabled(on_off);
            }
        };
        connectButton(view, R.id.btn_chanup,                new ChannelUp(channelView, infoView, uiCallback));
        connectButton(view, R.id.btn_chandown,              new ChannelDown(channelView, infoView, uiCallback));
        connectButton(view, R.id.btn_show_serial_number,    new SerialNumber(infoView));
        connectButton(view, R.id.btn_show_version,          new DeviceVersion(infoView));
        connectButton(view, R.id.btn_show_clients,          new ClientList(infoView));
        connectButton(view, R.id.btn_program_data,          new CurrentChannelStatus(infoView));
        connectButton(view, R.id.btn_program_time,          new ChannelData(infoView));
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
}
