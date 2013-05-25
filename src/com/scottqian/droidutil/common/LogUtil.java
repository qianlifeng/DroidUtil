package com.scottqian.droidutil.common;

public class LogUtil
{
	private static boolean showLog = true;
	private static String logTag = "DroidUtil";
	
	public static void setLogTag(String tag)
	{
		logTag = tag;
	}

	public static void DisableLog()
	{
		showLog = false;
	}
	
	public static void EnableLog()
	{
		showLog = true;
	}

	public static void log(String msg)
	{
		if (showLog)
		{
			android.util.Log.i(logTag, msg);
		}
	}
}
