package com.scottqian.droidutil.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.scottqian.droidutil.R;

@SuppressLint("Registered")
public class CustomMenuActivity extends BaseAcitivity
{
	private TextView txtTitlebar;
	private  ImageView imgTitlebarSetting;
	private Boolean enableTitleBar = true;
	
	public ImageView getTitlebarSettingImageView()
	{
		return imgTitlebarSetting;
	}

	public Boolean getTitleBarEnabled()
	{
		return enableTitleBar == true;
	}
	



	public void setTitleBarEnabled()
	{
		enableTitleBar = true;
	}
	
	public void setTitleBarDisabled()
	{
		enableTitleBar = false;
	}

	public void setTitleBarContent(CharSequence title)
	{
		if (txtTitlebar != null)
		{
			txtTitlebar.setText(title);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		if (!enableTitleBar)
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
		// droiduitl_titlebar为自己标题栏的布局
		//这个一定要放在setcontentview之后才起作用
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.droiduitl_titlebar);
		txtTitlebar = (TextView) findViewById(R.id.txtTitlebar);
		imgTitlebarSetting = (ImageView) findViewById(R.id.imgTitlebarSetting);
	}
}
