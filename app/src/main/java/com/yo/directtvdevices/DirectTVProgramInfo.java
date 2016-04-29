package com.yo.directtvdevices;

import com.yo.interfaces.QueryProgramDataInterface;
import com.yo.interfaces.ResultNotificationInterface;

public class DirectTVProgramInfo implements QueryProgramDataInterface{
	private DirectTVBaseController mUtils;
	private String mGetProgramData="tv/getProgInfo?major=";
	private String mGetDataCmd = "episodeTitle";

	public DirectTVProgramInfo(DirectTVBaseController utils)
	{
		mUtils = utils;
	}
	@Override
	public void GetProgramData(Long channel, ResultNotificationInterface response, int userData)
	{
		mUtils.execute(mGetProgramData+Long.toString(channel), response, null, userData);
	}
}
