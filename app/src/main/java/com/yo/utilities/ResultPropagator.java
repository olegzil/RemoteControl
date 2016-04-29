package com.yo.utilities;

import com.yo.discoverymanagers.DiscoveryManager;
import com.yo.interfaces.ResultNotificationInterface;
import com.yo.interfaces.UIThreadAccessInterface;

import org.json.JSONObject;

/**
 * Created by oleg on 8/5/15.
 */
public class ResultPropagator {
    public void postResultSuccess(final ResultNotificationInterface srh, final JSONObject data)
    {
        UIThreadAccessInterface uiAccess = DiscoveryManager.getInstance().getUIThreadAccess();
        uiAccess.Post(new Runnable() {
            @Override
            public void run() {srh.onSuccess(data);}
        });
    }
    public void postResultError(final ResultNotificationInterface srh, final JSONObject data)
    {
        UIThreadAccessInterface uiAccess = DiscoveryManager.getInstance().getUIThreadAccess();
        uiAccess.Post(new Runnable() {
            @Override
            public void run() {srh.onError(data);}
        });
    }
}
