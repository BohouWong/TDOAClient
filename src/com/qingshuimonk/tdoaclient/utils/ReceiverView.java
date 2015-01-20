package com.qingshuimonk.tdoaclient.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/***
 * 本文件定义了用于在地图中显示接收机位置的覆盖物view，便于转化为bitmap后显示在地图上
 * 由一个背景图片和文字构成，文字用于描述接收机的编号
 * @author Huang Boaho
 * @version 1.0
 * @since 01/19/2015
 */
public class ReceiverView{
	private static Context context;
	private static int bg_resid;
	
	public ReceiverView(Context _context, int resid){
		context = _context;
		bg_resid = resid;
	}
	
	public Bitmap getBitmapFromView(int fontColor, int fontSize, String text) {
		TextView textView = new TextView(context);  
        textView.setGravity(Gravity.CENTER);  
        textView.setBackgroundResource(bg_resid);  
        textView.setTextColor(fontColor);  
        textView.setText(text);
        textView.setTextSize(fontSize);
		
        textView.destroyDrawingCache();  
        textView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),  
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));  
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());  
        textView.setDrawingCacheEnabled(true);  
        Bitmap bitmap = textView.getDrawingCache(true);  
        return bitmap;  
    }
}