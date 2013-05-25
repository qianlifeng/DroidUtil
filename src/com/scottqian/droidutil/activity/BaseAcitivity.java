package com.scottqian.droidutil.activity;

import com.scottqian.droidutil.app.AppManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

public class BaseAcitivity extends Activity
{
	// 是否允许全屏
	private boolean allowFullScreen = true;
	// 是否允许销毁
	private boolean allowDestroy = true;
	private View view;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		allowFullScreen = true;
		// 添加Activity到堆栈
		AppManager.getAppManager().addActivity(this);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		// 结束Activity&从堆栈中移除
		AppManager.getAppManager().finishActivity(this);
	}

	public boolean isAllowFullScreen()
	{
		return allowFullScreen;
	}

	/**
	 * 设置是否可以全屏
	 * 
	 * @param allowFullScreen
	 */
	public void setAllowFullScreen(boolean allowFullScreen)
	{
		this.allowFullScreen = allowFullScreen;
	}

	public void setAllowDestroy(boolean allowDestroy)
	{
		this.allowDestroy = allowDestroy;
	}

	public void setAllowDestroy(boolean allowDestroy, View view)
	{
		this.allowDestroy = allowDestroy;
		this.view = view;
	}
	
	protected void showToastLong(String msg)
	{
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	protected void showToastShort(String msg)
	{
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && view != null)
		{
			view.onKeyDown(keyCode, event);
			if (!allowDestroy)
			{
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
