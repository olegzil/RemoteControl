package com.yo.deviceui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.yo.remotecontrol.MainActivity;
import com.yo.discoverymanagers.DiscoveryManager;
import com.yo.interfaces.ConnectedDeviceInterface;
import com.yo.interfaces.ResultNotificationInterface;
import com.yo.remotecontrol.R;
import com.yo.rokudevices.RokuBaseController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by oleg on 8/19/15.
 */
public class RokuAppSelectionDialog  extends android.app.DialogFragment {
    private ArrayList<CustomListViewAdapter.ListModel> mChannelData = new ArrayList<>();
    ConnectedDeviceInterface mDevice;
    CustomListViewAdapter mListViewAdapter;
    final static private String TAG="ROKU";

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

        View view = (View) inflater.inflate(R.layout.roku_app_list, container, false);
        ListView list = (ListView) view.findViewById(R.id.list_app_list);
        mListViewAdapter = new CustomListViewAdapter(MainActivity.mThisActivity, mChannelData);
        list.setAdapter(mListViewAdapter);
        createAndPopulateListViewAdater();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CustomListViewAdapter.ListModel lm = (CustomListViewAdapter.ListModel) mListViewAdapter.getItem(i);
                activateApp(lm.getDescription(), lm.getId());
            }
        });
        return view;
    }

    private void activateApp(String appName, String appID)
    {
        mDevice.execute(RokuBaseController.LAUNCH, appID, new ResultNotificationInterface() {
            @Override
            public void onError(JSONObject data) {
                Log.e(TAG, "unable to start app");
            }

            @Override
            public void onSuccess(JSONObject data) {
                Log.i(TAG, "app started successfuly");
            }

            @Override
            public void done() {

            }
        }, 0);
    }
    private void createAndPopulateListViewAdater()
    {
        mDevice.execute(RokuBaseController.INFO, null, new ResultNotificationInterface() {
            @Override
            public void onError(JSONObject data) {
                StringBuffer sb = new StringBuffer();
                try {
                    for (Iterator<String> iter = data.keys(); iter.hasNext(); ) {
                        String key = iter.next();
                        String item = null;
                        item = String.format("%s ---> %s\n", key, data.get(key));
                        sb.append(item);
                    }
                    Toast.makeText(MainActivity.mThisActivity, sb.toString(), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(JSONObject data) {
                populateAdapter(data);
                mListViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void done() {

            }
        }, 0);
    }

    private void populateAdapter(JSONObject data)
    {
        try {
            for (Iterator<String>iter=data.keys(); iter.hasNext();)
            {
                String key = iter.next();
                final CustomListViewAdapter.ListModel detail = new CustomListViewAdapter.ListModel();

                /******* Firstly take data in model object ******/
                detail.setDescription(data.getString(key));
                detail.setId(key);
                mChannelData.add(detail);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void initDeviceReference(Bundle b) {
        Long key = b.getLong("device"); //get the device key
        mDevice = DiscoveryManager.getInstance().GetDeviceReference(key);
    }
}