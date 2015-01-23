package com.qingshuimonk.tdoaclient.data_structrue;

import java.util.concurrent.ArrayBlockingQueue;


/***
 * �������ڶ��巽��������صı����ͷ���
 * @author Huang Bohao
 * @version 1.0
 * @since 2014.11.10
 */
public class Variance{
	private ArrayBlockingQueue<Double> Var;
	private DateTime Time;
	
	/***
	 * ���캯��
	 * @param _Var
	 * @param _Time
	 */
	public Variance(ArrayBlockingQueue<Double> _Var, DateTime _Time){
		Var = _Var;
		Time = _Time;
	}
	
	// setters
	public void setVar(ArrayBlockingQueue<Double> _Var){
		Var = _Var;
	}
	
	// getters
	public ArrayBlockingQueue<Double> getVar(){
		return Var;
	}
	public DateTime getDateTime(){
		return Time;
	}

}