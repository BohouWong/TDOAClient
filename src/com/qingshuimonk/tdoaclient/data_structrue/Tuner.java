package com.qingshuimonk.tdoaclient.data_structrue;


/***
 * 本类用于定义接收机数据相关的变量和方法
 * @author Huang Bohao
 * @version 1.0
 * @since 2014.11.10
 * 
 * @Notice TunerIP,Pos,WorkMode,AveVoltage在客户端不能更改
 */

public class Tuner{
	private byte TunerID;						// 接收机ID
	private Position Pos;						// 接收机位置
	private byte WorkMode;						// 接收机工作模式
	private TunerWorkParameter Parameter;		// 接收机工作参数
	private double AveVoltage;					// 平均电平值
	private IQ IQqueue;						// IQ数据
	private FreqSpectrum FreqSpectrum;			// 频谱数据

	/***
	 * 构造函数		
	 * @param _TunerID			接收机ID
	 * @param _Pos				接收机位置
	 * @param _WorkMode			接收机工作模式: 1:可用	0:不可用
	 * @param AveVoltage		平均电平值
	 */
	public Tuner(byte _TunerID, Position _Pos, byte _WorkMode, double _AveVoltage){
		TunerID = _TunerID;
		Pos = _Pos;
		WorkMode = _WorkMode;
		AveVoltage = _AveVoltage;
	}
	
	// setters
	public void setTunerWorkParameter(TunerWorkParameter _Parameter){
		Parameter = _Parameter;
	}
	public void setIQData(IQ _IQqueue){
		IQqueue = _IQqueue;
	}
	public void setFreqSpectrum(FreqSpectrum _FreqSpectrum){
		FreqSpectrum = _FreqSpectrum;
	}
	
	// getters
	public byte getTunerID(){
		return TunerID;
	}
	public Position getPosition(){
		return Pos;
	}
	public byte getWorkMode(){
		return WorkMode;
	}
	public TunerWorkParameter getParameter(){
		return Parameter;
	}
	public double getAveVoltage(){
		return AveVoltage;
	}
	public IQ getIQqueue(){
		return IQqueue;
	}
	public FreqSpectrum getFreqSpectrum(){
		return FreqSpectrum;
	}

}