package com.qingshuimonk.tdoaclient.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/***
 * ���ļ������������ڵ�ͼ����ʾ���ջ�λ�õĸ�����view������ת��Ϊbitmap����ʾ�ڵ�ͼ��
 * ��һ������ͼƬ�����ֹ��ɣ����������������ջ��ı��
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