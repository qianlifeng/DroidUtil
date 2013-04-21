package com.scottqian.droidutil.activity;

import com.scottqian.droidutil.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends Activity
{
	private TextView txtTitlebar;
	protected ImageView imgTitlebarSetting;

	/**
	 * 是否需要标题栏
	 */
	public Boolean NoTitleBar = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (NoTitleBar)
		{
			// 不需要标题栏
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		else
		{
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
			setTheme(R.style.DroidUtil_CustomTitleBar);
		}
	}

	@Override
	public void setContentView(int layoutResID)
	{
		super.setContentView(layoutResID);
		AfterSetContentView();
	}

	@Override
	public void setContentView(View view, LayoutParams params)
	{
		super.setContentView(view, params);
		AfterSetContentView();
	}

	@Override
	public void setContentView(View view)
	{
		super.setContentView(view);
		AfterSetContentView();
	}

	private void AfterSetContentView()
	{
		// titlebar为自己标题栏的布局，这个一定要放在setcontentview之后才起作用
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.droiduitl_titlebar);
		txtTitlebar = (TextView) findViewById(R.id.txtTitlebar);
		imgTitlebarSetting = (ImageView) findViewById(R.id.imgTitlebarSetting);
	}

	protected void ShowMessageLong(String msg)
	{
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	protected void ShowMessageShort(String msg)
	{
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	protected void ShowMessage(String msg, int timeDuration)
	{
		Toast.makeText(this, msg, timeDuration).show();
	}

	/**
	 * 设置标题
	 * 
	 * @param title
	 */
	@Override
	public void setTitle(CharSequence title)
	{
		if (txtTitlebar != null)
		{
			txtTitlebar.setText(title);
		}
	}

	/**
	 * 退出程序
	 */
	protected void ExitApp()
	{
		finish();
		System.exit(0);
	}
}
