package com.yo.remotecontrol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yo.discoverymanagers.DiscoveryManager;
import com.yo.interfaces.ActivityNotificationInterface;
import com.yo.interfaces.ResultNotificationInterface;
import com.yo.interfaces.UIThreadAccessInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private ArrayList<ActivityNotificationInterface> mClients = new ArrayList<>();
    private boolean mError = false;
    private com.yo.remotecontrol.DevicePager mPagerAdapter = null;
    private UIControlInterface mUIControl;
    public static Activity mThisActivity=null;

    private interface UIControlInterface
    {
        void update(String name);
    }
    private class InitializePager implements  UIControlInterface
    {

        @Override
        public void update(String key) {
            setContentView(R.layout.view_pager);
            ViewPager vp = (ViewPager) findViewById(R.id.pager);
            mPagerAdapter = new com.yo.remotecontrol.DevicePager(getApplicationContext());
            mPagerAdapter.addNewPage(Long.valueOf(key));
            mPagerAdapter.notifyDataSetChanged();
            vp.setAdapter(mPagerAdapter);
            mUIControl = new UpdatePager();
        }
    }

    private class UpdatePager implements  UIControlInterface
    {

        @Override
        public void update(String key) {
            mPagerAdapter.addNewPage(Long.valueOf(key));
            mPagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mThisActivity = this;
        setContentView(R.layout.activity_device_control_main);
        final TextView tv = (TextView)findViewById(R.id.text_discovery_message);
        tv.setEnabled(false);
        final Handler h = new Handler(getMainLooper());
        mUIControl = new InitializePager();

        final Button btn = (Button) findViewById(R.id.btn_start);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn.setEnabled(false);
                DiscoveryManager.getInstance().SetUIAccess(new UIThreadAccessInterface() {
                    @Override
                    public void Post(Runnable r) {
                        h.post(r);
                    }
                });
                DiscoveryManager.getInstance().Start(mThisActivity, new ResultNotificationInterface() {
                    @Override
                    public void onError(JSONObject data) {
                        btn.setEnabled(true);
                        Toast.makeText(mThisActivity, "no devices available", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onSuccess(JSONObject data) {
                        try {
                            mUIControl.update(data.getString("device"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void done() {

                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        DiscoveryManager.getInstance().Shutdown();
        mThisActivity = null;
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        DiscoveryManager.getInstance().postActivityResult(data, requestCode, resultCode);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
