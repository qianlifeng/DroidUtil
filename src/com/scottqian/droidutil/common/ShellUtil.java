package com.scottqian.droidutil.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * shell命令帮助类，用于执行shell命令
 * 
 * @author scott
 */
public class ShellUtil
{
	/**
	 * 需要执行的命令
	 * 
	 * @author scott
	 * 
	 */
	public static class ShellCMD
	{
		/**
		 * 请求Root权限
		 */
		public static final ShellCMD REQUEST_SU = new ShellCMD(new String[] { "su" });

		String[] command;

		ShellCMD(String[] command)
		{
			this.command = command;
		}
	}

	public static Boolean ExecuteCommand(ShellCMD shellCmd)
	{
		try
		{
			Runtime.getRuntime().exec(shellCmd.command);
			return true;
		}
		catch (Exception e)
		{
			return false;
			// e.printStackTrace();
		}
	}
}
