package com.qingshuimonk.tdoaclient.data_structrue;

import java.util.concurrent.ArrayBlockingQueue;


/***
 * 本类用于定义方差数据相关的变量和方法
 * @author Huang Bohao
 * @version 1.0
 * @since 2014.11.10
 */
public class Variance{
	private ArrayBlockingQueue<Double> Var;
	private DateTime Time;
	
	/***
	 * 构造函数
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