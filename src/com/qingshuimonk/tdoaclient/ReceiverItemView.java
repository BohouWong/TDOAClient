package com.qingshuimonk.tdoaclient;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;
import com.qingshuimonk.tdoaclient.R;

/***
 * �������ڶ���ReceiverChooseActivity��ListView��ʾ
 * ����:		
 * 1.����ListView��ÿһ����Ŀ����ʾ��
 * ע��: 
 * 1.��Ҫ�޸����color.xml��dimens.xml�����UI��ʾ 
 * @author Huang Bohao
 * @version 1.0
 * @since 2014.11.18
 */
public class ReceiverItemView extends TextView{
	
	private Paint marginPaint;
	private Paint linePaint;
	private int paperColor;
	private float margin;
	
	//ReceiverItemView�Ĺ��캯��
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
		
		//������ˢ
		marginPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		marginPaint.setColor(myResources.getColor(R.color.margin));
		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setColor(myResources.getColor(R.color.lines));
		
		//���ҳ�汳��ɫ�ͱ�Ե���
		paperColor = myResources.getColor(R.color.paper);
		margin = myResources.getDimension(R.dimen.margin);
	}
	
	@Override
	public void onDraw(Canvas canvas){
		//����ҳ����ɫ
		canvas.drawColor(paperColor);
		
		//���Ʊ�Ե
		//canvas.drawLine(0, 0, 0, getMeasuredHeight(), linePaint);
		canvas.drawLine(0, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight(), linePaint);
		canvas.drawLine(margin, 0, margin, getMeasuredHeight(), marginPaint);
		canvas.drawLine(margin, margin-5, getMeasuredWidth(), margin-5, marginPaint);
		
		//�ƶ��ı�
		canvas.save();
		canvas.translate(margin, 0);
		
		//��Ⱦ�ı�
		super.onDraw(canvas);
		canvas.restore();
	}
	
}
