package com.qingshuimonk.tdoaclient;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import com.qingshuimonk.tdoaclient.utils.SysApplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/***
 * ��activity���ڶ���������ݵĲ���
 * ����:		
 * 	1.��ʾ������ݣ�
 * @version 1.0.0
 * @since 2014.11.22
 */
/***
 * FIXME
 * @author Huang Bohao
 * ����LocationResult�ı��activity
 */ 
public class CorrelationActivity extends Activity {
	
	// �������
	private static final int XLENGTH = 100;
	private static final int YLENGTH = 100;
	private XYSeries series;
	private XYMultipleSeriesDataset mDataset;
	private GraphicalView chart;
	private XYMultipleSeriesRenderer renderer;
	double addY;
	 int[] xv = new int[XLENGTH];
	 double[] yv = new double[YLENGTH];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_correlation);
		
		// ����activity��ӵ�SysApplication����
		SysApplication.getInstance().addActivity(this);
		
		// �ؼ�
		TextView RightLabel1 = (TextView)findViewById(R.id.RightLabel1);
		
		// ����ResultActvity������
		Integer i = (Integer)getIntent().getSerializableExtra("correlation");
		RightLabel1.setText("���ջ����:"+i);
		
		Context context = getApplicationContext();  
		// ����ACE����ʾ����
		LinearLayout layout = (LinearLayout)findViewById(R.id.ChartView); 
		
		// ���������
		final myAChartEngineLine achart = new myAChartEngineLine(series, mDataset, renderer);  
        achart.setLineData(Color.GREEN, PointStyle.CIRCLE);  // ����ͼ����ʽ
        achart.setChartSettings("This is a demo", "X", "Y", 0, XLENGTH, 0, 100, Color.WHITE,   
                Color.WHITE, true, Color.GREEN, 10, 5);  
        // ����ͼ��
        chart = ChartFactory.getLineChartView(context, achart.mDataset, achart.renderer);  
        // ��ͼ�������layout
        layout.addView(chart, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));  
        
        achart.mDataset.removeSeries(achart.series);
        for(int index = 0; index < XLENGTH; index++){
			achart.series.add(index, (Math.random() * 90));
		}
        achart.mDataset.addSeries(achart.series);
        chart.invalidate();  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.correlation, menu);
		return true;
	}

}
