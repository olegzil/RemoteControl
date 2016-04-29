package com.yo.rokudevices;

import com.yo.interfaces.ConnectedDeviceCommandInterface;
import com.yo.interfaces.ResultNotificationInterface;

/**
 * Created by oleg on 8/4/15.
 */
public class RokuCommandExecute  implements ConnectedDeviceCommandInterface {
    private RokuBaseController mUtils;
    public RokuCommandExecute(RokuBaseController controller)
    {
        mUtils = controller;
    }

    @Override
    public boolean execute(String command, ResultNotificationInterface response, String parameters, int userData) {
        String prefix = mUtils.getCommandPrefix(command).mPrefix;
        if (prefix == null)
            return false;

        String message = prefix + "/" + command;
        mUtils.execute(message, response, parameters, 0);
        return true;
    }
}
