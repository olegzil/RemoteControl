package com.yo.deviceui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.yo.directtvdevices.DirectTVBaseController;
import com.yo.discoverymanagers.DiscoveryManager;
import com.yo.interfaces.ConnectedDeviceInterface;
import com.yo.interfaces.ResultNotificationInterface;
import com.yo.remotecontrol.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by oleg on 8/18/15.
 */
public class DirectTVChannelDataDialog extends android.app.DialogFragment {
    private ArrayList<String> mChannelData = new ArrayList<>();
    ConnectedDeviceInterface mDevice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo);
        if (mDevice == null) {
            Bundle b = this.getArguments();
            initDeviceReference(b);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mDevice == null) {
            Bundle b = this.getArguments();
            initDeviceReference(b);
        }

        //setup the text view that will display the data associated with this channel
        View v = inflater.inflate(R.layout.channel_data, container, false);
        TextView tv = (TextView) v.findViewById(R.id.text_channel_data);
        tv.setMovementMethod(new ScrollingMovementMethod());

        //setup the text view that accepts a channel number as user input
        tv = (TextView) v.findViewById(R.id.text_channel_number);
        tv.setInputType(InputType.TYPE_CLASS_NUMBER);
        tv.setText(null);

        //setup keyboard input access for the above.
        final TextView info = (TextView)v.findViewById(R.id.text_channel_data);
        tv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    final String channel = v.getText().toString();
                    mDevice.execute(DirectTVBaseController.PROGRAMINFO, channel, new ResultNotificationInterface() {
                        @Override
                        public void onError(JSONObject data) {
                            info.setText(channel + "  " + "Channel not available");
                        }

                        @Override
                        public void onSuccess(JSONObject data) {
                            StringBuffer sb = new StringBuffer();
                            String item=new String();
                            for (Iterator<String> iter=data.keys(); iter.hasNext();) {
                                String key = iter.next();
                                if (key.compareTo("user-data") == 0)
                                    continue;
                                try {
                                    item = String.format("%s  ---> %s\n", key, data.getString(key));
                                    sb.append(item);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            info.setText(sb.toString());
                        }

                        @Override
                        public void done() {}

                    }, 0);
                }
                return false;
            }
        });
        return v;
    }
    private void initDeviceReference(Bundle b)
    {
        Long key = b.getLong("device"); //get the device key
        mDevice = DiscoveryManager.getInstance().GetDeviceReference(key);
    }

}
