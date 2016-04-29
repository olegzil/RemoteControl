package com.yo.interfaces;

import org.json.JSONObject;

/**
 * Created by oleg on 8/5/15.
 */
public interface ResultNotificationInterface {
    void onError(JSONObject data);
    void onSuccess(JSONObject data);
    void done();
}
