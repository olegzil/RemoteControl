package com.yo.directtvdevices;

import com.yo.interfaces.ChannelControlInterface;
import com.yo.interfaces.ConnectedDeviceColorKeysInterface;
import com.yo.interfaces.ConnectedDeviceDVRInterface;
import com.yo.interfaces.ConnectedDeviceMenuInterface;
import com.yo.interfaces.ConnectedDeviceNavigationKeysInterface;
import com.yo.interfaces.ConnectedDeviceNumericInterface;
import com.yo.interfaces.ResultNotificationInterface;

import org.json.JSONObject;

public class DirectTVRemoteControl implements ChannelControlInterface, 
											  ConnectedDeviceColorKeysInterface,
											  ConnectedDeviceDVRInterface,
											  ConnectedDeviceMenuInterface,
											  ConnectedDeviceNavigationKeysInterface,
											  ConnectedDeviceNumericInterface
											  {
	private DirectTVBaseController mUtils;
	private String mCmdPrefix = "remote/processKey?key=";
	private String mCmdSuffix = "&hold=keyPress";

	private ResultNotificationInterface callback = new ResultNotificationInterface() {
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


												  public DirectTVRemoteControl(DirectTVBaseController utils)
	{
		mUtils = utils;
	}

	//****** ChannelControlInterface ******
	@Override
	public boolean ChannelUp() {
	    String command = formCommand(DirectTVBaseController.CHANNELUP);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean ChannelDown() {
	    String command = formCommand(DirectTVBaseController.CHANNELDOWN);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean ChannelSet(Long value) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean CannelPrevious() {
	    String command = formCommand(DirectTVBaseController.CHANNELUP);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public Long GetCurrentChannel() {
		// TODO Auto-generated method stub
		return null;
	}

	//***** ConnectedDeviceColorKeysInterface *****
	@Override
	public boolean Red() {
	    String command = formCommand(DirectTVBaseController.RED);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean Green() {
	    String command = formCommand(DirectTVBaseController.GREEN);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean Yellow() {
	    String command = formCommand(DirectTVBaseController.YELLOW);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean Blue() {
	    String command = formCommand(DirectTVBaseController.BLUE);
	    return mUtils.execute(command, callback, null, 0);
	}

	//***** ConnectedDeviceDVRInterface *****
	@Override
	public boolean Pause() {
	    String command = formCommand(DirectTVBaseController.PAUSE);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean Review() {
	    String command = formCommand(DirectTVBaseController.REVIEW);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean Replay() {
	    String command = formCommand(DirectTVBaseController.REPLAY);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean Stop() {
	    String command = formCommand(DirectTVBaseController.STOP);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean Advance() {
	    String command = formCommand(DirectTVBaseController.ADVANCE);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean FastForward() {
	    String command = formCommand(DirectTVBaseController.FASTFWD);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean Record() {
	    String command = formCommand(DirectTVBaseController.RECORD);
	    return mUtils.execute(command, callback, null, 0);
	}

	//***** ConnectedDeviceMenuInterface *****
	@Override
	public boolean Play() {
	    String command = formCommand(DirectTVBaseController.PLAY);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean Guide() {
	    String command = formCommand(DirectTVBaseController.GUIDE);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean Active() {
	    String command = formCommand(DirectTVBaseController.ACTIVE);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean List() {
	    String command = formCommand(DirectTVBaseController.LIST);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean Exit() {
	    String command = formCommand(DirectTVBaseController.EXIT);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean Back() {
	    String command = formCommand(DirectTVBaseController.BACK);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean Menu() {
	    String command = formCommand(DirectTVBaseController.MENU);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean Info() {
	    String command = formCommand(DirectTVBaseController.INFO);
	    return mUtils.execute(command, callback, null, 0);
	}

	//***** ConnectedDeviceNavigationKeysInterface *****
	@Override
	public boolean KeyLeft() {
	    String command = formCommand(DirectTVBaseController.ARROWLEFT);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean KeyRight() {
	    String command = formCommand(DirectTVBaseController.ARROWRIGHT);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean KeyUp() {
	    String command = formCommand(DirectTVBaseController.ARROWUP);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean KeyDown() {
	    String command = formCommand(DirectTVBaseController.ARROWDOWN);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean KeySelect() {
	    String command = formCommand(DirectTVBaseController.SELECT);
	    return mUtils.execute(command, callback, null, 0);
	}
	
	//***** ConnectedDeviceNumericInterface *****
	@Override
	public boolean Number(int value) {
	    String command = formCommand(Integer.toString(value));
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean Dash() {
	    String command = formCommand(DirectTVBaseController.DASH);
	    return mUtils.execute(command, callback, null, 0);
	}

	@Override
	public boolean Enter() {
	    String command = formCommand(DirectTVBaseController.ENTER);
	    return mUtils.execute(command, callback, null, 0);
	}
	
	private String formCommand(String cmd)
	{
		return mCmdPrefix + cmd + mCmdSuffix;
	}

}
