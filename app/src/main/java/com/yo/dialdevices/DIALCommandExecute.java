package com.yo.dialdevices;

import com.yo.interfaces.ConnectedDeviceCommandInterface;
import com.yo.interfaces.ResultNotificationInterface;

/**
 * Created by oleg on 8/7/15.
 */
public class DIALCommandExecute  implements ConnectedDeviceCommandInterface {
    private DIALBaseController mUtils;
    public DIALCommandExecute(DIALBaseController controller)
    {
        mUtils = controller;
    }

    @Override
    public boolean execute(String command, ResultNotificationInterface response, String extra, int userData) {
        //TODO:OZ implement DIAL protocol per device type
        return true;
    }
}
