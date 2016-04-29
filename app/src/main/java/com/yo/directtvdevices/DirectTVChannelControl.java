package com.yo.directtvdevices;

import com.yo.interfaces.ChannelControlInterface;
import com.yo.interfaces.ResultNotificationInterface;

import org.json.JSONObject;

public class DirectTVChannelControl implements ChannelControlInterface{
	private DirectTVBaseController mUtils;
	private String mGetCurrentChannel="tv/getTuned";
	private String mChangeChannel="tv/tune?major=";
	private String mChannelCommand = "major";
	private Long mPreviousChannel=0L;
	ResultNotificationInterface callback = new ResultNotificationInterface() {
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

	public DirectTVChannelControl(DirectTVBaseController utils)
	{
		mUtils = utils;
	}
	@Override
	public boolean ChannelUp() {
		if (!mUtils.execute(mGetCurrentChannel, callback, null, 0))
			return false;
		
		Long channel = (Long) mUtils.getPayload().get(mChannelCommand);
		if (channel == 9999)
			channel = 0L;
		channel++;
		mUtils.execute(mChangeChannel + Long.toString(channel), callback, null, 0);
		return true;
	}

	@Override
	public boolean ChannelDown() {
		if (!mUtils.execute(mGetCurrentChannel, callback, null, 0))
			return false;
		
		Long channel = (Long) mUtils.getPayload().get(mChannelCommand);
		if (channel == 1)
			channel = 1000L;
		--channel;
		return mUtils.execute(mChangeChannel + channel.toString(), callback,null, 0);
	}

	@Override
	public boolean ChannelSet(Long value) {
		if (value <= 0 && value >= 9999)
			return false;
		
		return mUtils.execute(mChangeChannel + Long.toString(value), callback, null, 0);
	}

	@Override
	public boolean CannelPrevious() {
		if (this.mPreviousChannel == 0)
		{
			Long channel = (Long) mUtils.getPayload().get(mChannelCommand);
			this.mPreviousChannel = channel;
			return true;
		}		
		return ChannelSet(this.mPreviousChannel);
	}
	@Override
	public Long GetCurrentChannel() {
		if (!mUtils.execute(mGetCurrentChannel, callback, null,0))
			return -1L;
		
		return (Long) mUtils.getPayload().get(mChannelCommand);
	}
}
