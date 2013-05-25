package com.scottqian.droidutil.ad;

import android.content.Context;

public interface IAdvertisement
{
	/**
	 * 打开广告
	 */
	void showAD(Context context);
	
	/**
	 * 关闭广告
	 * @param context
	 */
	void closeAD(Context context);
}
