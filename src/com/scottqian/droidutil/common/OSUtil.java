	package com.scottqian.droidutil.common;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class OSUtil
{
	/**
	 * 获得屏幕宽度，单位：dp
	 * @return
	 */
	public static float GetScreenWidthDp(Context context)
	{
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return ConvertPixelsToDp(metrics.widthPixels,context.getResources());
	}
	
	/**
	 * 获得屏幕高度，单位：dp
	 * @return
	 */
	public static float GetScreenHeightDp(Context context)
	{
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return ConvertPixelsToDp(metrics.heightPixels,context.getResources());
	}
	
	public static float GetScreenWidthPixel(Context context)
	{
		return context.getResources().getDisplayMetrics().widthPixels;
	}
	
	public static float GetScreenHeightPixel(Context context)
	{
		return context.getResources().getDisplayMetrics().heightPixels;
	}
	
	/**
	 * This method convets dp unit to equivalent device specific value in pixels. 
	 * 
	 * @param dp A value in dp(Device independent pixels) unit. Which we need to convert into pixels
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent Pixels equivalent to dp according to device
	 */
	public static float ConvertDpToPixel(float dp,Resources resources){
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float px = dp * (metrics.densityDpi/160f);
	    return px;
	}
	/**
	 * This method converts device specific pixels to device independent pixels.
	 * 
	 * @param px A value in px (pixels) unit. Which we need to convert into db
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent db equivalent to px value
	 */
	public static float ConvertPixelsToDp(float px,Resources resources){
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float dp = px / (metrics.densityDpi / 160f);
	    return dp;
	}
}
