package com.scottqian.droidutil.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * 文件操作工具包
 */
public class FileUtil
{
	/**
	 * 写文本文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
	 * 
	 * @param context
	 * @param msg
	 */
	public static void Write(Context context, String fileName, String content)
	{
		if (content == null)
			content = "";

		try
		{
			FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			fos.write(content.getBytes());

			fos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 读取文本文件
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static String Read(Context context, String fileName)
	{
		try
		{
			FileInputStream in = context.openFileInput(fileName);
			return readInStream(in);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}

	private static String readInStream(FileInputStream inStream)
	{
		try
		{
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[512];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1)
			{
				outStream.write(buffer, 0, length);
			}

			outStream.close();
			inStream.close();
			return outStream.toString();
		}
		catch (IOException e)
		{
			Log.i("FileTest", e.getMessage());
		}
		return null;
	}

	public static File createFile(String folderPath, String fileName)
	{
		File destDir = new File(folderPath);
		if (!destDir.exists())
		{
			destDir.mkdirs();
		}
		return new File(folderPath, fileName + fileName);
	}

	/**
	 * 向手机写字节（比如说图片）
	 * 
	 * @param buffer
	 * @param folder
	 * @param fileName
	 * @return
	 */
	public static boolean WriteFile(byte[] buffer, String folder, String fileName)
	{
		boolean writeSucc = false;

		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

		String folderPath = "";
		if (sdCardExist)
		{
			folderPath = Environment.getExternalStorageDirectory() + File.separator + folder + File.separator;
		}
		else
		{
			writeSucc = false;
		}

		File fileDir = new File(folderPath);
		if (!fileDir.exists())
		{
			fileDir.mkdirs();
		}

		File file = new File(folderPath + fileName);
		FileOutputStream out = null;
		try
		{
			out = new FileOutputStream(file);
			out.write(buffer);
			writeSucc = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				out.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		return writeSucc;
	}

	/**
	 * 根据文件绝对路径获取文件名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileName(String filePath)
	{
		if (StringUtil.isEmpty(filePath))
			return "";
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
	}

	/**
	 * 根据文件的绝对路径获取文件名但不包含扩展名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileNameNoFormat(String filePath)
	{
		if (StringUtil.isEmpty(filePath))
		{
			return "";
		}
		int point = filePath.lastIndexOf('.');
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1, point);
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileFormat(String fileName)
	{
		if (StringUtil.isEmpty(fileName))
			return "";

		int point = fileName.lastIndexOf('.');
		return fileName.substring(point + 1);
	}

	/**
	 * 获取文件大小
	 * 
	 * @param filePath
	 * @return
	 */
	public static long getFileSize(String filePath)
	{
		long size = 0;

		File file = new File(filePath);
		if (file != null && file.exists())
		{
			size = file.length();
		}
		return size;
	}

	/**
	 * 获取文件大小
	 * 
	 * @param size
	 *            字节
	 * @return
	 */
	public static String getFileSize(long size)
	{
		if (size <= 0)
			return "0";
		java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
		float temp = (float) size / 1024;
		if (temp >= 1024)
		{
			return df.format(temp / 1024) + "M";
		}
		else
		{
			return df.format(temp) + "K";
		}
	}

	/**
	 * 转换文件大小
	 * 
	 * @param fileS
	 * @return B/KB/MB/GB
	 */
	public static String formatFileSize(long fileS)
	{
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024)
		{
			fileSizeString = df.format((double) fileS) + "B";
		}
		else if (fileS < 1048576)
		{
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		}
		else if (fileS < 1073741824)
		{
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		}
		else
		{
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 获取目录文件大小
	 * 
	 * @param dir
	 * @return
	 */
	public static long getDirSize(File dir)
	{
		if (dir == null)
		{
			return 0;
		}
		if (!dir.isDirectory())
		{
			return 0;
		}
		long dirSize = 0;
		File[] files = dir.listFiles();
		for (File file : files)
		{
			if (file.isFile())
			{
				dirSize += file.length();
			}
			else if (file.isDirectory())
			{
				dirSize += file.length();
				dirSize += getDirSize(file); // 递归调用继续统计
			}
		}
		return dirSize;
	}

	/**
	 * 获取目录文件个数
	 * 
	 * @param f
	 * @return
	 */
	public long getFileList(File dir)
	{
		long count = 0;
		File[] files = dir.listFiles();
		count = files.length;
		for (File file : files)
		{
			if (file.isDirectory())
			{
				count = count + getFileList(file);// 递归
				count--;
			}
		}
		return count;
	}

	public static byte[] toBytes(InputStream in) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int ch;
		while ((ch = in.read()) != -1)
		{
			out.write(ch);
		}
		byte buffer[] = out.toByteArray();
		out.close();
		return buffer;
	}

	/**
	 * 检查文件是否存在
	 * 
	 * @param path 手机内存存储： /system/XXX | 外部存储：/sdcard/XXX
	 * @return 是否存在
	 */
	public static boolean IsFileExists(String path)
	{
		boolean status;
		if (!path.equals(""))
		{
			File newPath = new File(path);
			status = newPath.exists();
		}
		else
		{
			status = false;
		}
		return status;
	}
	
	public static boolean IsFolderExists(String path)
	{
		boolean status;
		if (!path.equals(""))
		{
			File newPath = new File(path);
			status = newPath.exists()  && newPath.isDirectory();
		}
		else
		{
			status = false;
		}
		return status;
	}

	public static boolean deleteDir(String path)
	{
		boolean success = true;
		File file = new File(path);
		if (file.exists())
		{
			File[] list = file.listFiles();
			if (list != null)
			{
				int len = list.length;
				for (int i = 0; i < len; ++i)
				{
					if (list[i].isDirectory())
					{
						deleteDir(list[i].getPath());
					}
					else
					{
						boolean ret = list[i].delete();
						if (!ret)
						{
							success = false;
						}
					}
				}
			}
		}
		else
		{
			success = false;
		}
		if (success)
		{
			file.delete();
		}
		return success;
	}

	public static void CopyFile(File src, File dst) throws IOException {
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
	
	/**
	 * 计算SD卡的剩余空间
	 * 
	 * @return 返回-1，说明没有安装sd卡
	 */
	public static long getFreeDiskSpace()
	{
		String status = Environment.getExternalStorageState();
		long freeSpace = 0;
		if (status.equals(Environment.MEDIA_MOUNTED))
		{
			try
			{
				File path = Environment.getExternalStorageDirectory();
				StatFs stat = new StatFs(path.getPath());
				long blockSize = stat.getBlockSize();
				long availableBlocks = stat.getAvailableBlocks();
				freeSpace = availableBlocks * blockSize / 1024;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			return -1;
		}
		return (freeSpace);
	}

	/**
	 * 新建目录
	 * 
	 * @param directoryName
	 * @return
	 */
	public static boolean createDirectory(String directoryName)
	{
		boolean status;
		if (!directoryName.equals(""))
		{
			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + directoryName);
			status = newPath.mkdir();
			status = true;
		}
		else
			status = false;
		return status;
	}

	/**
	 * 检查是否安装SD卡
	 * 
	 * @return
	 */
	public static boolean checkSaveLocationExists()
	{
		String sDCardStatus = Environment.getExternalStorageState();
		boolean status;
		if (sDCardStatus.equals(Environment.MEDIA_MOUNTED))
		{
			status = true;
		}
		else
			status = false;
		return status;
	}

	/**
	 * 删除目录(包括：目录里的所有文件)
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean DeleteDirectory(String fileName)
	{
		boolean status;
		SecurityManager checker = new SecurityManager();

		if (!fileName.equals(""))
		{

			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + fileName);
			checker.checkDelete(newPath.toString());
			if (newPath.isDirectory())
			{
				String[] listfile = newPath.list();
				// delete all files within the specified directory and then
				// delete the directory
				try
				{
					for (int i = 0; i < listfile.length; i++)
					{
						File deletedFile = new File(newPath.toString() + "/" + listfile[i].toString());
						deletedFile.delete();
					}
					newPath.delete();
					Log.i("DirectoryManager deleteDirectory", fileName);
					status = true;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					status = false;
				}

			}
			else
				status = false;
		}
		else
			status = false;
		return status;
	}

	/**
	 * 删除文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean deleteFile(String fileName)
	{
		boolean status;
		SecurityManager checker = new SecurityManager();

		if (!fileName.equals(""))
		{

			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + fileName);
			checker.checkDelete(newPath.toString());
			if (newPath.isFile())
			{
				try
				{
					Log.i("DirectoryManager deleteFile", fileName);
					newPath.delete();
					status = true;
				}
				catch (SecurityException se)
				{
					se.printStackTrace();
					status = false;
				}
			}
			else
				status = false;
		}
		else
			status = false;
		return status;
	}

	/**
	 * 解压缩功能. 将zipFile文件解压到folderPath目录下.
	 * 
	 * @throws Exception
	 */
	public static int unZipFile(File zipFile, String folderPath) throws ZipException, IOException
	{

		ZipFile zfile = new ZipFile(zipFile);
		Enumeration<? extends ZipEntry> zList = zfile.entries();

		ZipEntry ze = null;
		byte[] buf = new byte[1024];
		while (zList.hasMoreElements())
		{
			ze = (ZipEntry) zList.nextElement();
			File tempFile = new File(folderPath + "/" + ze.getName());
			/*
			 * if(ze.isDirectory()){ Log.d("upZipFile",
			 * "ze.getName() = "+ze.getName()); String dirstr = folderPath +
			 * ze.getName(); //dirstr.trim(); // dirstr = new
			 * String(dirstr.getBytes("8859_1"), "GB2312"); Log.d("upZipFile",
			 * "str = "+dirstr); File f=new File(dirstr); f.mkdir(); continue; }
			 */
			if (ze.isDirectory())
			{
				tempFile.mkdirs();
				continue;
			}
			else if (!tempFile.getParentFile().exists())
			{
				tempFile.getParentFile().mkdirs();
			}

			Log.d("upZipFile", "ze.getName() = " + ze.getName());
			OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(folderPath, ze.getName())));
			InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
			int readLen = 0;
			while ((readLen = is.read(buf, 0, 1024)) != -1)
			{
				os.write(buf, 0, readLen);
			}
			is.close();
			os.close();
		}
		zfile.close();
		Log.d("upZipFile", "finish");
		return 0;
	}

	public static void downFile(String url, String path, String fileName) throws IOException
	{
		String localfile = fileName;
		if (fileName == null || fileName == "")
			localfile = url.substring(url.lastIndexOf("/") + 1);

		URL Url = new URL(url);
		URLConnection conn = Url.openConnection();
		conn.connect();
		InputStream is = conn.getInputStream();
		try
		{
			int fileSize = conn.getContentLength();// 根据响应获取文件大小
			if (fileSize <= 0)
			{
				// 获取内容长度为0
				throw new RuntimeException("无法获知文件大小 ");
			}
			if (is == null)
			{

				throw new RuntimeException("无法获取文件");
			}
			FileOutputStream FOS = new FileOutputStream(path + File.separator + localfile);
			// 创建写入文件内存流，通过此流向目标写文件
			byte buf[] = new byte[1024];

			int numread;
			while ((numread = is.read(buf)) != -1)
			{
				FOS.write(buf, 0, numread);
			}
			FOS.close();
		}
		finally
		{
			is.close();
		}

	}

	public static String getFileNameWithoutExtension(String filePath)
	{
		String path = getFileName(filePath);
		return path.substring(0, path.lastIndexOf("."));
	}
}