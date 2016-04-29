package com.yo.utilities;

import android.os.Handler;

import com.yo.interfaces.UIThreadAccessInterface;

/**
 * Created by oleg on 7/31/15.
 */
public class UIAccess implements UIThreadAccessInterface{
    Handler mHandler;
    public UIAccess(Handler h)
    {
        mHandler = h;
    }
    @Override
    public void Post(Runnable r) {
        mHandler.post(r);
    }
}
