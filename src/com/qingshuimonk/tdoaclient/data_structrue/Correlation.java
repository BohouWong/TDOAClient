package com.qingshuimonk.tdoaclient.data_structrue;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

/***
 * 本类用于定义相关数据相关的变量和方法
 * @author Huang Bohao
 * @version 2.0
 * @since 2014.11.22
 */

public class Correlation implements Serializable{
	
	private static final long serialVersionUID = -478341915603189981L;
	private byte RefTunerID;
	private byte OtherTunerID;
	private BlockingQueue<Integer> Cor;
	private DateTime Time;
	
	/***
	 * 构造函数
	 * @param _RefTunerID
	 * @param _OtherTunerID
	 * @param _Cor
	 * @param _Time
	 */
	public Correlation(byte _RefTunerID, byte _OtherTunerID, BlockingQueue<Integer> _Cor, DateTime _Time){
		RefTunerID = _RefTunerID;
		OtherTunerID = _OtherTunerID;
		Cor = _Cor;
		Time = _Time;
	}
	
	// setters
	public void setCor(BlockingQueue<Integer> _Cor){
		Cor = _Cor;
	}	
	public void setDateTime(DateTime _Time){
		Time = _Time;
	}
	
	// getters
	public byte getRefTunerID(){
		return RefTunerID;
	}
	public byte getOtherTunerID(){
		return OtherTunerID;
	}
	public BlockingQueue<Integer> getCor(){
		return Cor;
	}
	public DateTime getTime(){
		return Time;
	}

}