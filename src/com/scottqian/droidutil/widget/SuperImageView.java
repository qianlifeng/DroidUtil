package com.scottqian.droidutil.widget;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 带有放大缩小功能的ImageView
 * 
 */
public class SuperImageView extends ImageView
{

	/**
	 * 最大缩放倍数
	 */
	static final float MAX_SCALE = 2.0f;
	float imageW;
	float imageH;
	float rotatedImageW;
	float rotatedImageH;
	float viewW;
	float viewH;
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	static final int NONE = 0;// 初始状态
	static final int DRAG = 1;// 拖动
	static final int ZOOM = 2;// 缩放
	static final int ROTATE = 3;// 旋转
	static final int ZOOM_OR_ROTATE = 4; // 缩放或旋转
	int mode = NONE;

	PointF pA = new PointF();
	PointF pB = new PointF();
	PointF mid = new PointF();
	PointF lastClickPos = new PointF();
	long lastClickTime = 0;
	double rotation = 0.0;
	float dist = 1f;

	private Timer signalClickTimer = new Timer();
	Handler uiHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case 0:
					if (touchEventListener != null)
					{
						touchEventListener.onClick();
					}
					break;
			}
			super.handleMessage(msg);
		}
	};

	private ITouchEventListener touchEventListener;

	public ITouchEventListener getTouchEventListener()
	{
		return touchEventListener;
	}

	public void setTouchEventListener(ITouchEventListener touchEventListener)
	{
		this.touchEventListener = touchEventListener;
	}

	/**
	 * 对外的事件接口
	 * 
	 */
	public interface ITouchEventListener
	{
		void onZoom();

		void onClick();

		void onZoomToOriginal();
	}

	public SuperImageView(Context context)
	{
		super(context);
		init();
	}

	public SuperImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public SuperImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	private void init()
	{
		setScaleType(ImageView.ScaleType.MATRIX);
	}

	public void setImageBitmap(Bitmap bm)
	{
		super.setImageBitmap(bm);
		setImageWidthHeight();
	}

	public void setImageDrawable(Drawable drawable)
	{
		super.setImageDrawable(drawable);
		setImageWidthHeight();
	}

	public void setImageResource(int resId)
	{
		super.setImageResource(resId);
		setImageWidthHeight();
	}

	private void setImageWidthHeight()
	{
		Drawable d = getDrawable();
		if (d == null)
		{
			return;
		}
		imageW = rotatedImageW = d.getIntrinsicWidth();
		imageH = rotatedImageH = d.getIntrinsicHeight();
		initImage();
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		viewW = w;
		viewH = h;
		if (oldw == 0)
		{
			initImage();
		}
		else
		{
			fixScale();
			fixTranslation();
			setImageMatrix(matrix);
		}
	}

	private void initImage()
	{
		if (viewW <= 0 || viewH <= 0 || imageW <= 0 || imageH <= 0)
		{
			return;
		}
		mode = NONE;
		matrix.setScale(0, 0);
		fixScale();
		fixTranslation();
		setImageMatrix(matrix);
	}

	private void fixScale()
	{
		float p[] = new float[9];
		matrix.getValues(p);
		float curScale = Math.abs(p[0]) + Math.abs(p[1]);

		float minScale = Math.min((float) viewW / (float) rotatedImageW, (float) viewH / (float) rotatedImageH);
		if (curScale < minScale)
		{
			if (curScale > 0)
			{
				double scale = minScale / curScale;
				p[0] = (float) (p[0] * scale);
				p[1] = (float) (p[1] * scale);
				p[3] = (float) (p[3] * scale);
				p[4] = (float) (p[4] * scale);
				matrix.setValues(p);
			}
			else
			{
				matrix.setScale(minScale, minScale);
			}
		}
	}

	private float maxPostScale()
	{
		float p[] = new float[9];
		matrix.getValues(p);
		float curScale = Math.abs(p[0]) + Math.abs(p[1]);

		float minScale = Math.min((float) viewW / (float) rotatedImageW, (float) viewH / (float) rotatedImageH);
		float maxScale = Math.max(minScale, MAX_SCALE);
		return maxScale / curScale;
	}

	private void fixTranslation()
	{
		RectF rect = new RectF(0, 0, imageW, imageH);
		matrix.mapRect(rect);

		float height = rect.height();
		float width = rect.width();

		float deltaX = 0, deltaY = 0;

		if (width < viewW)
		{
			deltaX = (viewW - width) / 2 - rect.left;
		}
		else if (rect.left > 0)
		{
			deltaX = -rect.left;
		}
		else if (rect.right < viewW)
		{
			deltaX = viewW - rect.right;
		}

		if (height < viewH)
		{
			deltaY = (viewH - height) / 2 - rect.top;
		}
		else if (rect.top > 0)
		{
			deltaY = -rect.top;
		}
		else if (rect.bottom < viewH)
		{
			deltaY = viewH - rect.bottom;
		}
		matrix.postTranslate(deltaX, deltaY);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		switch (event.getAction() & MotionEvent.ACTION_MASK)
		{
		// 主点按下
			case MotionEvent.ACTION_DOWN:
				savedMatrix.set(matrix);
				pA.set(event.getX(), event.getY());
				pB.set(event.getX(), event.getY());
				mode = DRAG;
				break;
			// 副点按下
			case MotionEvent.ACTION_POINTER_DOWN:
				if (event.getActionIndex() > 1)
					break;
				dist = spacing(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
				// 如果连续两点距离大于10，则判定为多点模式
				if (dist > 10f)
				{
					savedMatrix.set(matrix);
					pA.set(event.getX(0), event.getY(0));
					pB.set(event.getX(1), event.getY(1));
					mid.set((event.getX(0) + event.getX(1)) / 2, (event.getY(0) + event.getY(1)) / 2);
					mode = ZOOM_OR_ROTATE;
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				if (mode == DRAG)
				{
					if (spacing(pA.x, pA.y, pB.x, pB.y) < 50)
					{
						long now = System.currentTimeMillis();
						if (now - lastClickTime < 500 && spacing(pA.x, pA.y, lastClickPos.x, lastClickPos.y) < 50)
						{
							doubleClick(pA.x, pA.y);
							now = 0;
							signalClickTimer.cancel(); //cancel后不能再次schedule
							signalClickTimer = new Timer();
						}
						else if (spacing(pA.x, pA.y, event.getX(), event.getY()) == 0)
						{
							// 开始单击定时器，如果是双击则取消定时器
						  TimerTask task=	new TimerTask()
							{
								public void run()
								{
									 Message message = new Message();   
						             message.what =0;               
						             uiHandler.sendMessage(message);   
								}
							};
							signalClickTimer.schedule(task, 600);
						}
						lastClickPos.set(pA);
						lastClickTime = now;
					}
				}
				if (touchEventListener != null)
				{
					float p[] = new float[9];
					matrix.getValues(p);
					float curScale = Math.abs(p[0]) + Math.abs(p[1]);
					float minScale = Math.min((float) viewW / (float) rotatedImageW, (float) viewH / (float) rotatedImageH);
					if (curScale <= minScale + 0.01)
						touchEventListener.onZoomToOriginal();
					else
						touchEventListener.onZoom();
				}
				mode = NONE;
				break;

			case MotionEvent.ACTION_MOVE:

				if (mode == ZOOM_OR_ROTATE)
				{
					PointF pC = new PointF(event.getX(1) - event.getX(0) + pA.x, event.getY(1) - event.getY(0) + pA.y);
					double a = spacing(pB.x, pB.y, pC.x, pC.y);
					double b = spacing(pA.x, pA.y, pC.x, pC.y);
					double c = spacing(pA.x, pA.y, pB.x, pB.y);
					if (a >= 10)
					{
						double cosB = (a * a + c * c - b * b) / (2 * a * c);
						double angleB = Math.acos(cosB);
						double PID4 = Math.PI / 4;
						if (angleB > PID4 && angleB < 3 * PID4)
						{
							mode = ROTATE;
							rotation = 0;
						}
						else
						{
							mode = ZOOM;
						}
					}
				}

				if (mode == DRAG)
				{
					matrix.set(savedMatrix);
					pB.set(event.getX(), event.getY());
					matrix.postTranslate(event.getX() - pA.x, event.getY() - pA.y);
					fixTranslation();
					setImageMatrix(matrix);
				}
				else if (mode == ZOOM)
				{
					float newDist = spacing(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
					if (newDist > 10f)
					{
						matrix.set(savedMatrix);
						float tScale = Math.min(newDist / dist, maxPostScale());
						matrix.postScale(tScale, tScale, mid.x, mid.y);
						fixScale();
						fixTranslation();
						setImageMatrix(matrix);
					}
				}
				break;
		}
		return true;
	}

	/**
	 * 两点的距离
	 */
	private float spacing(float x1, float y1, float x2, float y2)
	{
		float x = x1 - x2;
		float y = y1 - y2;
		return (float) Math.sqrt(x * x + y * y);
	}

	private void doubleClick(float x, float y)
	{
		float p[] = new float[9];
		matrix.getValues(p);
		float curScale = Math.abs(p[0]) + Math.abs(p[1]);

		float minScale = Math.min((float) viewW / (float) rotatedImageW, (float) viewH / (float) rotatedImageH);
		if (curScale <= minScale + 0.01)
		{ // 放大
			float toScale = Math.max(minScale, MAX_SCALE) / curScale;
			matrix.postScale(toScale, toScale, x, y);
		}
		else
		{ // 缩小
			float toScale = minScale / curScale;
			matrix.postScale(toScale, toScale, x, y);
			fixTranslation();
		}
		setImageMatrix(matrix);
	}

}
