package com.yo.directtvdevices;

import com.yo.interfaces.PowerControlInterface;
import com.yo.interfaces.ResultNotificationInterface;

import org.json.JSONObject;

public class DirectTVPowerControl implements PowerControlInterface {
	DirectTVBaseController mUtils;
	String mCmdPrefix = "remote/processKey?key=";
	String mCmdSuffix = "&hold=keyPress";
	String mPowerOn = "poweron";
	String mPowerOff = "poweroff";
	ResultNotificationInterface mCallback = new ResultNotificationInterface() {
		@Override
		public void onError(JSONObject data) {

		}

		@Override
		public void onSuccess(JSONObject data) {

		}

		@Override
		public void done() {

		}
	};
	public DirectTVPowerControl(DirectTVBaseController utils)
	{
		mUtils = utils;
	}
	@Override
	public boolean PowerOn() {
	    String command =formCommand(mPowerOn);
	    mUtils.execute(command, mCallback, null, 0);
		return true;
	}

	@Override
	public boolean PowerOff() {
	    String command =formCommand(mPowerOn);
	    return mUtils.execute(command, mCallback, null, 0);
	}
	private String formCommand(String cmd)
	{
		return mCmdPrefix + cmd + mCmdSuffix;
	}
}
