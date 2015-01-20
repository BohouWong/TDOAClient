package com.qingshuimonk.tdoaclient;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/***
 * This is an android activity file:
 * function:	1.Show user details of Correlation data;
 * @author Huang Bohao
 * @version 1.0.0
 * @since 2014.11.22
 *
 */
public class CorrelationActivity extends Activity {
	
	// Definition of variables
	private static final int XLENGTH = 100;
	private static final int YLENGTH = 100;
	private XYSeries series;
	private XYMultipleSeriesDataset mDataset;
	private GraphicalView chart;
	private XYMultipleSeriesRenderer renderer;
	private int addX = -1;
	double addY;
	 int[] xv = new int[XLENGTH];
	 double[] yv = new double[YLENGTH];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_correlation);
		
		// add activity to list
		SysApplication.getInstance().addActivity(this);
		
		// get ID of widgets
		TextView RightLabel1 = (TextView)findViewById(R.id.RightLabel1);
		
		// get data transferred from ResultActivity
		Integer i = (Integer)getIntent().getSerializableExtra("correlation");
		RightLabel1.setText("½ÓÊÕ»ú±àºÅ:"+i);
		
		Context context = getApplicationContext();  
		// get layout in view, the chart will be set into the layout
		LinearLayout layout = (LinearLayout)findViewById(R.id.ChartView); 
		
		// generate random dots
		final myAChartEngineLine achart = new myAChartEngineLine(series, mDataset, renderer);  
        achart.setLineData(Color.GREEN, PointStyle.CIRCLE);  // set the style of the chart
        achart.setChartSettings("This is a demo", "X", "Y", 0, XLENGTH, 0, 100, Color.WHITE,   
                Color.WHITE, true, Color.GREEN, 10, 5);  
        // generate the chart
        chart = ChartFactory.getLineChartView(context, achart.mDataset, achart.renderer);  
        // add the chart into the layout
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
