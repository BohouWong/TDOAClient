package com.qingshuimonk.tdoaclient.data_structrue;

import java.util.ArrayList;


/***
 * This is a class defines variables and functions of Result
 * @author Huang Bohao
 * @version 1.0
 * @since 2014.11.10
 * 
 * @problem The SyncTime function is needed to be completed
 */

public class Result{
	private ArrayList<Correlation> CorList;
	private Position Pos;
	private Variance Var;
	private DateTime Time;

	/***
	 * Constructor of Result
	 * @param _Cor
	 * @param _Pos
	 * @param _Var
	 * @param _Time
	 */
	public Result(ArrayList<Correlation> _CorList, Position _Pos, Variance _Var, DateTime _Time){
		CorList = _CorList;
		Pos = _Pos;
		Var = _Var;
		Time = _Time;
		
	}
	
	/***
	 * Set Correlation of Result
	 * @param _CorList
	 */
	public void setCorrelation(ArrayList<Correlation> _CorList){
		CorList = _CorList;
	}
	
	/***
	 * Set Pos of Result
	 * @param _Pos
	 */
	public void setPosition(Position _Pos){
		Pos = _Pos;
	}
	
	/***
	 * Set Var of Result
	 * @param _Var
	 */
	public void setVariance(Variance _Var){
		Var = _Var;
	}
	
	/***
	 * Set Time of Result
	 * @param _Time
	 */
	public void setDateTime(DateTime _Time){
		Time = _Time;
	}
	
	/***
	 * Get CorList of Result
	 * @return CorList
	 */
	public ArrayList<Correlation> getCorrelation(){
		return CorList;
	}
	
	/***
	 * Get Pos of Result
	 * @return Pos
	 */
	public Position getPosition(){
		return Pos;
	}
	
	/***
	 * Get Variance of Result
	 * @return Var
	 */
	public Variance getVariance(){
		return Var;
	}
	
	/***
	 * Get Time of Result
	 * @return Time
	 */
	public DateTime getTime(){
		return Time;
	}
	
	/***
	 * Synchronization the Time of Correlation, Position and Variance
	 * @param _Cor
	 * @param _Pos
	 * @param _Var
	 * @param _Time
	 */
	public void SyncTime(Correlation _Cor, Position _Pos, Variance _Var, DateTime _Time){
		
	}
}