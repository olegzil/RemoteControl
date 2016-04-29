package com.yo.interfaces;

/**
 * Created by oleg on 8/3/15.
 */
public interface ConnectedDeviceCommandInterface {
    boolean execute(String command, ResultNotificationInterface response, String parameters, int userData);
}
