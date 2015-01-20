package com.qingshuimonk.tdoaclient.data_structrue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/***
 * This is a class defines variables and functions of Variance
 * @author Huang Bohao
 * @version 1.0
 * @since 2014.11.10
 */

public class Variance{
	private ArrayBlockingQueue<Double> Var;
	private DateTime Time;
	
	/***
	 * Constructor of Variance
	 * @param _Var
	 * @param _Time
	 */
	public Variance(ArrayBlockingQueue<Double> _Var, DateTime _Time){
		Var = _Var;
		Time = _Time;
	}
	
	/***
	 * Set Var of Variance
	 * @param _Var
	 */
	public void setVar(ArrayBlockingQueue<Double> _Var){
		Var = _Var;
	}
	
	/***
	 * Get Var of Variance
	 * @return Var
	 */
	public ArrayBlockingQueue<Double> getVar(){
		return Var;
	}
	
	/***
	 * Get Time of Variance
	 * @return Time
	 */
	public DateTime getDateTime(){
		return Time;
	}

}