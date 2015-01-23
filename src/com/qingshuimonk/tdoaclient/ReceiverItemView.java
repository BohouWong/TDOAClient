package com.qingshuimonk.tdoaclient;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;
import com.qingshuimonk.tdoaclient.R;

/***
 * 本类用于定义ReceiverChooseActivity中ListView显示
 * 功能:		
 * 1.定义ListView中每一项条目的显示；
 * 注意: 
 * 1.需要修改添加color.xml和dimens.xml以完成UI显示 
 * @author Huang Bohao
 * @version 1.0
 * @since 2014.11.18
 */
public class ReceiverItemView extends TextView{
	
	private Paint marginPaint;
	private Paint linePaint;
	private int paperColor;
	private float margin;
	
	//ReceiverItemView的构造函数
	public ReceiverItemView(Context context, AttributeSet ats, int ds){
		super(context, ats, ds);
		init();
	}
	
	public ReceiverItemView(Context context){
		super(context);
		init();
	}
	
	public ReceiverItemView(Context context, AttributeSet ats){
		super(context, ats);
		init();
	}
	
	private void init(){
		Resources myResources = getResources();
		
		//创建画刷
		marginPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		marginPaint.setColor(myResources.getColor(R.color.margin));
		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setColor(myResources.getColor(R.color.lines));
		
		//获得页面背景色和边缘宽度
		paperColor = myResources.getColor(R.color.paper);
		margin = myResources.getDimension(R.dimen.margin);
	}
	
	@Override
	public void onDraw(Canvas canvas){
		//绘制页面颜色
		canvas.drawColor(paperColor);
		
		//绘制边缘
		//canvas.drawLine(0, 0, 0, getMeasuredHeight(), linePaint);
		canvas.drawLine(0, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight(), linePaint);
		canvas.drawLine(margin, 0, margin, getMeasuredHeight(), marginPaint);
		canvas.drawLine(margin, margin-5, getMeasuredWidth(), margin-5, marginPaint);
		
		//移动文本
		canvas.save();
		canvas.translate(margin, 0);
		
		//渲染文本
		super.onDraw(canvas);
		canvas.restore();
	}
	
}
