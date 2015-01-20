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
	// ��Ա����
	public XYSeries series;
	public XYMultipleSeriesDataset mDataset;
	public XYMultipleSeriesRenderer renderer;
	
	// ���캯��
	public myAChartEngineLine(XYSeries _series, XYMultipleSeriesDataset _mDataset, XYMultipleSeriesRenderer _renderer){
		series = _series;
		mDataset = _mDataset;
		renderer = _renderer;
	}
	
	// ������������
	/**
	 * ���������ߵ����ݼ��Լ���ʽ
	 * @param color		�ߵ���ɫ
	 * @param style		�ߵ���ʽ
	 */
	public void setLineData(int color, PointStyle style){
		//������������������ϵ����е㣬��һ����ļ��ϣ�������Щ�㻭������
        series = new XYSeries("title");
        //����һ�����ݼ���ʵ����������ݼ�������������ͼ��
        mDataset = new XYMultipleSeriesDataset();
        //���㼯��ӵ�������ݼ���
        mDataset.addSeries(series);
        
        //���¶������ߵ���ʽ�����Եȵȵ����ã�renderer�൱��һ��������ͼ������Ⱦ�ľ��
        renderer = buildRenderer(color, style, true);
	}
	
	protected XYMultipleSeriesRenderer buildRenderer(int color, PointStyle style, boolean fill) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        
        //����ͼ�������߱������ʽ��������ɫ����Ĵ�С�Լ��ߵĴ�ϸ��
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(color);
        r.setPointStyle(style);
        r.setFillPoints(fill);
        r.setLineWidth(3);
        renderer.addSeriesRenderer(r);
        
        return renderer;
       }
	
	/**
	 * �����������������ʽ
	 * @param title			ͼ���ͷ
	 * @param xTitle		X�����
	 * @param yTitle		Y�����
	 * @param xMin			X����Сֵ
	 * @param xMax			X�����ֵ
	 * @param yMin			Y����Сֵ
	 * @param yMax			Y�����ֵ
	 * @param axesColor		��������ɫ
	 * @param labelsColor	ͼ���ͷ���������ͷ��ɫ
	 * @param showGrid		�Ƿ���ʾ����
	 * @param gridColor		������ɫ
	 * @param XLabels		X��̶���(����=(xMax-xMin)/XLabels)
	 * @param YLabels		Y��̶���(����=(yMax-yMin)/YLabels)
	 */
	protected void setChartSettings(String title, String xTitle, String yTitle, double xMin, 
			double xMax, double yMin, double yMax, int axesColor, int labelsColor, boolean showGrid,
			int gridColor, int XLabels, int YLabels) {
    	     //�йض�ͼ�����Ⱦ�ɲο�api�ĵ�
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
		
		// ���ԭ��maxseries
        mDataset.removeSeries(1);
        mDataset.removeSeries(1);
        maxseries1.clear();
        maxseries2.clear();
		
		// Ѱ�����ֵ
		for(int i = 0; i < XLENGTH; i++){       	
        	if(series.getY(i) >= tempmax){
        		tempmax = series.getY(i);
        		maxindex = i;
        	}
        }
		
		// ����series
	    maxseries1.add(maxindex, tempmax);
        maxseries1.add(maxindex-1, tempmax+2);
        maxseries1.add(maxindex+1, tempmax+2);
        maxseries2.add(maxindex-1, tempmax+2);
        maxseries2.add(maxindex+1, tempmax+2);
        
        // �����ݼ�������µĵ㼯
        mDataset.addSeries(maxseries1);
        mDataset.addSeries(maxseries2);
        
        // ˢ����ʾ
        //chart.invalidate();
	}
	
}

