package com.qingshuimonk.tdoaclient;

import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.graphics.Paint.Align;
import android.widget.LinearLayout;


public class myAChartEngineLine{
	// 成员变量
	public XYSeries series;
	public XYMultipleSeriesDataset mDataset;
	public XYMultipleSeriesRenderer renderer;
	
	// 构造函数
	public myAChartEngineLine(XYSeries _series, XYMultipleSeriesDataset _mDataset, XYMultipleSeriesRenderer _renderer){
		series = _series;
		mDataset = _mDataset;
		renderer = _renderer;
	}
	
	// 设置折线数据
	/**
	 * 用于设置线的数据集以及样式
	 * @param color		线的颜色
	 * @param style		线的样式
	 */
	public void setLineData(int color, PointStyle style){
		//这个类用来放置曲线上的所有点，是一个点的集合，根据这些点画出曲线
        series = new XYSeries("title");
        //创建一个数据集的实例，这个数据集将被用来创建图表
        mDataset = new XYMultipleSeriesDataset();
        //将点集添加到这个数据集中
        mDataset.addSeries(series);
        
        //以下都是曲线的样式和属性等等的设置，renderer相当于一个用来给图表做渲染的句柄
        renderer = buildRenderer(color, style, true);
	}
	
	protected XYMultipleSeriesRenderer buildRenderer(int color, PointStyle style, boolean fill) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        
        //设置图表中曲线本身的样式，包括颜色、点的大小以及线的粗细等
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(color);
        r.setPointStyle(style);
        r.setFillPoints(fill);
        r.setLineWidth(3);
        renderer.addSeriesRenderer(r);
        
        return renderer;
       }
	
	/**
	 * 用于设置坐标轴的样式
	 * @param title			图表表头
	 * @param xTitle		X轴标题
	 * @param yTitle		Y轴标题
	 * @param xMin			X轴最小值
	 * @param xMax			X轴最大值
	 * @param yMin			Y轴最小值
	 * @param yMax			Y轴最大值
	 * @param axesColor		坐标轴颜色
	 * @param labelsColor	图表表头，坐标轴表头颜色
	 * @param showGrid		是否显示网格
	 * @param gridColor		网格颜色
	 * @param XLabels		X轴刻度数(步长=(xMax-xMin)/XLabels)
	 * @param YLabels		Y轴刻度数(步长=(yMax-yMin)/YLabels)
	 */
	protected void setChartSettings(String title, String xTitle, String yTitle, double xMin, 
			double xMax, double yMin, double yMax, int axesColor, int labelsColor, boolean showGrid,
			int gridColor, int XLabels, int YLabels) {
    	     //有关对图表的渲染可参看api文档
    	     renderer.setChartTitle(title);
    	     renderer.setXTitle(xTitle);
    	     renderer.setYTitle(yTitle);
    	     renderer.setXAxisMin(xMin);
    	     renderer.setXAxisMax(xMax);
    	     renderer.setYAxisMin(yMin);
    	     renderer.setYAxisMax(yMax);
    	     renderer.setAxesColor(axesColor);
    	     renderer.setLabelsColor(labelsColor);
    	     renderer.setShowGrid(showGrid);
    	     renderer.setGridColor(gridColor);
    	     renderer.setXLabels(XLabels);
    	     renderer.setYLabels(YLabels);
    	     //renderer.setXTitle(XTitle);
    	     //renderer.setYTitle(YTitle);
    	     renderer.setYLabelsAlign(Align.RIGHT);
    	     renderer.setPointSize((float) 2);
    	     renderer.setShowLegend(false);
    	    }
	
	public void labelMax(XYSeries series, int XLENGTH, GraphicalView chart){
		XYSeries maxseries1 = new XYSeries("max1");
	    XYSeries maxseries2 = new XYSeries("max2");
		double tempmax = 0;
		int maxindex = 0;
		
		// 清空原有maxseries
        mDataset.removeSeries(1);
        mDataset.removeSeries(1);
        maxseries1.clear();
        maxseries2.clear();
		
		// 寻找最大值
		for(int i = 0; i < XLENGTH; i++){       	
        	if(series.getY(i) >= tempmax){
        		tempmax = series.getY(i);
        		maxindex = i;
        	}
        }
		
		// 配置series
	    maxseries1.add(maxindex, tempmax);
        maxseries1.add(maxindex-1, tempmax+2);
        maxseries1.add(maxindex+1, tempmax+2);
        maxseries2.add(maxindex-1, tempmax+2);
        maxseries2.add(maxindex+1, tempmax+2);
        
        // 在数据集中添加新的点集
        mDataset.addSeries(maxseries1);
        mDataset.addSeries(maxseries2);
        
        // 刷新显示
        //chart.invalidate();
	}
	
}

