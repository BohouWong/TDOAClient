package com.qingshuimonk.tdoaclient.data_structrue;

/***
 * 本类用于定义定位区域相关的变量和方法
 * @author Huang Bohao
 * @version 1.0
 * @since 2014.11.10
 */

public class LocationRegion{
	private byte RegionMode;			// 定位区域类型
	private double RegionValue1;
	private double RegionValue2;
	private double RegionValue3;
	private double RegionValue4;
	
	/***
	 * 构造函数
	 * @param _RegionMode		1:圆形定位区域;			0:矩形定位区域；
	 * @param _RegionValue1		1:圆心经度;				0:左上点经度；
	 * @param _RegionValue2		1:圆心纬度;				0:左上点纬度；
	 * @param _RegionValue3		1:圆半径;				0:右下点经度；
	 * @param _RegionValue4		1:缺省;					0:右下点纬度；
	 */
	public LocationRegion(byte _RegionMode, double _RegionValue1, double _RegionValue2, double _RegionValue3, double _RegionValue4){
		RegionMode = _RegionMode;
		RegionValue1 = _RegionValue1;
		RegionValue2 = _RegionValue2;
		RegionValue3 = _RegionValue3;
		RegionValue4 = _RegionValue4;
	}
	
	// setters 
	public void setRegionMode(byte _RegionMode){
		RegionMode = _RegionMode;
	}
	public void setRegionValue1(byte _RegionValue1){
		RegionValue1 = _RegionValue1;
	}
	public void setRegionValue2(byte _RegionValue2){
		RegionValue2 = _RegionValue2;
	}
	public void setRegionValue3(byte _RegionValue3){
		RegionValue3 = _RegionValue3;
	}
	public void setRegionValue4(byte _RegionValue4){
		RegionValue4 = _RegionValue4;
	}
	
	// getters
	public byte getRegionMode(){
		return RegionMode;
	}
	public double getRegionValue1(){
		return RegionValue1;
	}
	public double getRegionValue2(){
		return RegionValue2;
	}
	public double getRegionValue3(){
		return RegionValue3;
	}
	public double getRegionValue4(){
		return RegionValue4;
	}

}