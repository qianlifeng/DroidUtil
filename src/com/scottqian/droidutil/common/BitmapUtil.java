package com.scottqian.droidutil.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class BitmapUtil
{
	/**
	 * 根据URL获取网络图片，并转换成Bitmap
	 * 
	 * @param url
	 *            图片资源的URL
	 * @return Bitmap 返回的图片资源位图
	 * */
	public static Bitmap GetBitmapByUrl(String url)
	{
		URL myUrl = null;
		Bitmap myBitmap = null;
		// 字符串转化成URL
		try
		{
			myUrl = new URL(url);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		// 从网络流中读取图片资源
		try
		{
			HttpURLConnection con = (HttpURLConnection) myUrl.openConnection();
			con.setDoInput(true);// 以后就可以使用conn.getInputStream().read() ;
			con.connect();
			InputStream is = con.getInputStream();
			myBitmap = BitmapFactory.decodeStream(is);
			is.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return myBitmap;
	}

	/**
	 * 将Drawable转换成Bitmap
	 * 
	 * @param drawable
	 *            图片资源Drawable
	 * @return Bitmap 返回的图片资源位图
	 * */
	public static Bitmap DrawableToBitmap(Drawable drawable)
	{
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;// 取得drawable颜色
		Bitmap bitmap = Bitmap.createBitmap(width, height, config);// 新建对应的Bitmap
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * 根据传入的Bitmap对象构建带有倒影的Bitmap
	 * 
	 * @param originalBitmap
	 *            图片位图
	 * @return Bitmap 返回带有倒影的位图Bitmap
	 * */
	public static Bitmap CreateWithReflectedImage(Bitmap originalBitmap)
	{
		if (originalBitmap == null)
		{
			return originalBitmap;
		}
		int reflectionGap = 4;// 图片与倒影之间的距离间隔
		int width = originalBitmap.getWidth();
		int height = originalBitmap.getHeight();
		// 变换所需的Matrix,完成 图片旋转，缩放等控制
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		// 获取倒影Bitmap
		Bitmap reflectionBitmap = Bitmap.createBitmap(originalBitmap, 0, height / 2, width, height / 2, matrix, false);
		/*
		 * Bitmap reflectionBitmap = readBitMap(bitmap2IS(originalBitmap),
		 * width, height);
		 */
		// 获取带倒影的Bitmap.即整体的效果图位图对象
		Bitmap withReflectionBitmap = Bitmap.createBitmap(width, height + height / 2, Config.ARGB_8888);

		/** Bitmap的显示还需要画布Canvas来完成 */
		// 由该位图对象创建初始画布(规定了画布的宽高)
		Canvas canvas = new Canvas(withReflectionBitmap);
		canvas.drawBitmap(originalBitmap, 0, 0, null);
		// 绘制出原图与倒影之间的间隔，用矩形来描绘
		Paint paint1 = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, paint1);
		// 绘制出倒影的Bitmap
		canvas.drawBitmap(reflectionBitmap, 0, height + reflectionGap, paint1);
		// 绘制线性渐变对象
		Paint paint2 = new Paint();
		LinearGradient shader = new LinearGradient(0, originalBitmap.getHeight(), 0, withReflectionBitmap.getHeight() + reflectionGap,
				0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		// 把渐变效果应用在画笔上
		paint2.setShader(shader);
		// 设置倒影的阴影度，使其与原来的图像颜色区别开来，此处显示灰度，会被染上下面的底部的原图片的倒影颜色，实现倒影的修饰
		paint2.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_IN));
		// 用设置好的paint2绘制此倒影
		canvas.drawRect(0, height, width, withReflectionBitmap.getHeight() + reflectionGap, paint2);
		return withReflectionBitmap;
	}

	/**
	 * Bitmap转换成InputStream
	 * */
	public static InputStream Bitmap2IS(Bitmap bm)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		InputStream sbs = new ByteArrayInputStream(baos.toByteArray());
		return sbs;
	}

	/**
	 * 以最省内存的方式读取本地资源的图片
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap ReadBitMap(InputStream in, int width, int height)
	{
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		opt.inSampleSize = 4;
		// 获取资源图片
		return BitmapFactory.decodeStream(in, new Rect(0, height / 2, width + 0, height), opt);
	}

	// 通过传入位图,新的宽.高比进行位图的缩放操作
	public static Drawable ResizeImage(Bitmap bitmap, int w, int h)
	{

		// load the origial Bitmap
		Bitmap BitmapOrg = bitmap;

		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		int newWidth = w;
		int newHeight = h;

		// calculate the scale
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the Bitmap
		matrix.postScale(scaleWidth, scaleHeight);
		// if you want to rotate the Bitmap
		// matrix.postRotate(45);

		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);

		// make a Drawable from Bitmap to allow to set the Bitmap
		// to the ImageView, ImageButton or what ever
		return new BitmapDrawable(resizedBitmap);

	}

	/**
	 * 获得圆角图片的方法
	 */
	public static Bitmap GetRoundedCornerBitmap(Bitmap bitmap, float roundPx)
	{

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * create the bitmap from a byte array 生成水印图片
	 * 
	 * @param src
	 *            the bitmap object you want proecss
	 * @param watermark
	 *            the water mark above the src
	 * @return return a bitmap object ,if paramter's length is 0,return null
	 */
	public static Bitmap CreateBitmap(Bitmap src, Bitmap watermark)
	{
		if (src == null)
		{
			return null;
		}

		int w = src.getWidth();
		int h = src.getHeight();
		int ww = watermark.getWidth();
		int wh = watermark.getHeight();
		// create the new blank bitmap
		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas(newb);
		// draw src into
		cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
		// draw watermark into
		cv.drawBitmap(watermark, w - ww + 5, h - wh + 5, null);// 在src的右下角画入水印
		// save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		// store
		cv.restore();// 存储
		return newb;
	}

	/**
	 * @param 将图片内容解析成字节数组
	 * @param inStream
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] readStream(InputStream inStream) throws Exception
	{
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1)
		{
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;

	}

}
