package com.yo.interfaces;

import android.content.Intent;

/**
 * Created by oleg on 8/21/15.
 */
public interface ActivityNotificationInterface {
    void onSuccess(Intent intent);
    void onFailure(Intent intent);
}
