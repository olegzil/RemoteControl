package com.yo.rokudevices;

import com.yo.interfaces.QueryInstalledAppInterface;
import com.yo.interfaces.ResultNotificationInterface;

public class RokuGetAppInfo implements QueryInstalledAppInterface{
	private RokuBaseController mUtils;
	private ResultNotificationInterface mCallback = null;
	public RokuGetAppInfo(RokuBaseController rbc)
	{
		mUtils = rbc;
	}
	@Override
	public void GetInstalledApplicationList( ResultNotificationInterface response) {
//		String prefix = mUtils.getCommandPrefix(RokuBaseController.QUERY);
//        if (prefix != null)
//            mUtils.execute(prefix + "/" + "apps", response, "GET");
	}
}
