package com.qingshuimonk.tdoaclient.data_structrue;

import java.util.ArrayList;

/***
 * �������ڶ�����������صı����ͷ���
 * @author Huang Bohao
 * @version 1.0
 * @since 2014.11.10
 * 
 * TODO ��Ҫ���ͬ������
 */

public class Result{
	private ArrayList<Correlation> CorList;
	private Position Pos;
	private Variance Var;
	private DateTime Time;

	/***
	 * ���캯��
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
	
	// setters
	public void setCorrelation(ArrayList<Correlation> _CorList){
		CorList = _CorList;
	}
	public void setPosition(Position _Pos){
		Pos = _Pos;
	}
	public void setVariance(Variance _Var){
		Var = _Var;
	}
	public void setDateTime(DateTime _Time){
		Time = _Time;
	}
	
	// getters
	public ArrayList<Correlation> getCorrelation(){
		return CorList;
	}
	public Position getPosition(){
		return Pos;
	}
	public Variance getVariance(){
		return Var;
	}
	public DateTime getTime(){
		return Time;
	}
	
	/***
	 * ͬ�������ջ�������
	 * @param _Cor
	 * @param _Pos
	 * @param _Var
	 * @param _Time
	 */
	public void SyncTime(Correlation _Cor, Position _Pos, Variance _Var, DateTime _Time){
		
	}
}