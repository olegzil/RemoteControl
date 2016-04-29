package com.yo.directtvdevices;

import com.yo.interfaces.ConnectedDeviceCommandInterface;
import com.yo.interfaces.ResultNotificationInterface;

/**
 * Created by oleg on 8/5/15.
 */
public class DirectTVCommandExecute  implements ConnectedDeviceCommandInterface {
    private DirectTVBaseController mUtils;
    public DirectTVCommandExecute(DirectTVBaseController controller)
    {
        mUtils = controller;
    }

    @Override
    public boolean execute(String command, ResultNotificationInterface response, String extra, int userData) {
        String prefix = "remote/processKey/";
        if (prefix == null)
            return false;

        mUtils.execute(prefix + command, response, extra, userData);
        return true;
    }
}
