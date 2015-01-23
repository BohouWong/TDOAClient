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
 * 本activity用于定义相关数据的操作
 * 功能:		
 * 	1.显示相关数据；
 * @version 1.0.0
 * @since 2014.11.22
 */
/***
 * FIXME
 * @author Huang Bohao
 * 根据LocationResult改编此activity
 */ 
public class CorrelationActivity extends Activity {
	
	// 定义变量
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
		
		// 将此activity添加到SysApplication类中
		SysApplication.getInstance().addActivity(this);
		
		// 控件
		TextView RightLabel1 = (TextView)findViewById(R.id.RightLabel1);
		
		// 接收ResultActvity的数据
		Integer i = (Integer)getIntent().getSerializableExtra("correlation");
		RightLabel1.setText("接收机编号:"+i);
		
		Context context = getApplicationContext();  
		// 设置ACE的显示区域
		LinearLayout layout = (LinearLayout)findViewById(R.id.ChartView); 
		
		// 产生随机数
		final myAChartEngineLine achart = new myAChartEngineLine(series, mDataset, renderer);  
        achart.setLineData(Color.GREEN, PointStyle.CIRCLE);  // 设置图表样式
        achart.setChartSettings("This is a demo", "X", "Y", 0, XLENGTH, 0, 100, Color.WHITE,   
                Color.WHITE, true, Color.GREEN, 10, 5);  
        // 创建图表
        chart = ChartFactory.getLineChartView(context, achart.mDataset, achart.renderer);  
        // 将图表添加至layout
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
