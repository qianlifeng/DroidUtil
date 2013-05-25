package com.scottqian.droidutil.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * @author hljdrl@gmail.com 网络状态工具累
 */
public class NetworkUtil
{

	/**
	 * 检测手机是否开启GPRS网络,需要调用ConnectivityManager,TelephonyManager 服务.
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean IsGprsNetwork(Context context)
	{
		boolean has = false;
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		int netType = info.getType();
		int netSubtype = info.getSubtype();
		if (netType == ConnectivityManager.TYPE_MOBILE && netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
				&& !mTelephony.isNetworkRoaming())
		{
			has = info.isConnected();
		}
		return has;

	}

	/**
	 * 检测手机是否开启WIFI网络,需要调用ConnectivityManager服务.
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean IskWifiNetwork(Context context)
	{
		boolean has = false;
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		int netType = info.getType();
		if (netType == ConnectivityManager.TYPE_WIFI)
		{
			has = info.isConnected();
		}
		return has;
	}

	/**
	 * 检测当前手机是否联网
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean IsNetworkAvailable(Context context)
	{
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null)
		{
			return false;
		}
		else
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
			{
				for (int i = 0; i < info.length; i++)
				{
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 手机是否处在漫游
	 * 
	 * @param mCm
	 * @return boolean
	 */
	public static boolean IsNetworkRoaming(Context mCm)
	{
		ConnectivityManager connectivity = (ConnectivityManager) mCm.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null)
		{
			return false;
		}
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		boolean isMobile = (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE);
		TelephonyManager mTm = (TelephonyManager) mCm.getSystemService(Context.TELEPHONY_SERVICE);
		boolean isRoaming = isMobile && mTm.isNetworkRoaming();
		return isRoaming;
	}

}