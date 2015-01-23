package com.qingshuimonk.tdoaclient.data_structrue;

/***
 * 本类用于定义接收机工作组工作参数数据相关的变量和方法
 * @author Huang Bohao
 * @version 1.0
 * @since 2014.11.10
 */

public class TunerWorkParameter{
	private long CenterFreq;			// 中心频率
	private int BandWidth; 			// 带宽
	private byte TrigMode;				// 触发方式
	private DateTime TrigTime; 		// 触发时间
	private short TrigPower;			// 触发能量
	private byte IQNum;				// 每包IQ数据，单位kB
	private byte MGC;					// 手动增益控制
	private LocationRegion Region;		// 定位区域
	
	/***
	 * 构造
	 * @param _CenterFreq
	 * @param _BandWidth
	 * @param _TrigMode: 	0:时间触发模式;	1:能量触发模式
	 * @param _TrigTime
	 * @param _TrigPower
	 * @param _IQNum
	 * @param _MGC
	 * @param _Region
	 */
	public TunerWorkParameter(long _CenterFreq, int _BandWidth, byte _TrigMode, DateTime _TrigTime, 
			short _TrigPower, byte _IQNum, byte _MGC, LocationRegion _Region){
		CenterFreq = _CenterFreq;
		BandWidth = _BandWidth;
		TrigMode = _TrigMode;
		TrigTime = _TrigTime;
		TrigPower = _TrigPower;
		IQNum = _IQNum;
		MGC = _MGC;
		Region = _Region;
	}
	/***
	 * 构造函数
	 * @param _Region
	 */
	public TunerWorkParameter(LocationRegion _Region){
		Region = _Region;
	}
	
	// setters
	public void setCenterFreq(long _CenterFreq){
		CenterFreq = _CenterFreq;
	}
	public void setBandWidth(int _BandWidth){
		BandWidth = _BandWidth;
	}
	public void setTrigMode(byte _TrigMode){
		TrigMode = _TrigMode;
	}
	public void setTrigTime(DateTime _TrigTime){
		TrigTime = _TrigTime;
	}
	public void setTrigPower(short _TrigPower){
		TrigPower = _TrigPower;
	}
	public void setIQNum(byte _IQNum){
		IQNum = _IQNum;
	}
	public void setMGC(byte _MGC){
		MGC = _MGC;
	}
	public void setLocationRegion(LocationRegion _Region){
		Region = _Region;
	}
	
	// getters
	public long getCenterFreq(){
		return CenterFreq;
	}
	public int getBandWidth(){
		return BandWidth;
	}
	public byte getTrigMode(){
		return TrigMode;
	}
	public DateTime getTrigTime(){
		return TrigTime;
	}
	public short getTrigPower(){
		return TrigPower;
	}
	public byte getIQNum(){
		return IQNum;
	}
	public byte getMGC(){
		return MGC;
	}
	public LocationRegion getLocationRegion(){
		return Region;
	}

}