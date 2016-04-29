package com.yo.remotecontrol;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yo.discoverymanagers.DiscoveryManager;
import com.yo.interfaces.DeviceUIInterface;

import java.util.Hashtable;

/**
 * Created by oleg on 8/24/15.
 */
public class DevicePager extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    int mPageCount = 0;
    Hashtable<Integer, Long> mPostionToKeyMap =new Hashtable<>();

    private class PageView
    {
    }

    public DevicePager(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addNewPage(Long key)
    {
        int last = mPostionToKeyMap.size();
        mPostionToKeyMap.put(last, key);
    }
    @Override
    public int getCount() {
        return mPostionToKeyMap.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Long key = mPostionToKeyMap.get(position);
        DeviceUIInterface device = DiscoveryManager.getInstance().GetDeviceUI(key);
        View itemView = mLayoutInflater.inflate(device.GetLayoutID(), container, false);
        container.addView(itemView);
        device.Show(itemView);
        return itemView;
    }

    @Override
    public CharSequence getPageTitle (int position)
    {
        Long key = mPostionToKeyMap.get(position);
        DeviceUIInterface device = DiscoveryManager.getInstance().GetDeviceUI(key);
        return device.GetDeviceDescription();
    }

    @Override
    public void destroyItem (ViewGroup container, int position, Object object){
        mPostionToKeyMap.remove(position);
    }
}
