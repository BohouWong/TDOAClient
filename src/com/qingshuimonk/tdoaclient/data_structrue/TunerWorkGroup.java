package com.qingshuimonk.tdoaclient.data_structrue;

import java.util.ArrayList;

/***
 * 本类用于定义接收机工作组数据相关的变量和方法
 * @author Huang Bohao
 * @version 1.1
 * @since 2014.11.10
 * 
 * @Notice 定位结果在客户端不能被改变
 * TODO 接收机工作组同步函数需要添加
 */

public class TunerWorkGroup{
	private TunerWorkParameter Parameter;
	private ArrayList<Tuner> TunerGroup;			// Tuner的容器
	private Result Res;

	/***
	 * 构造函数
	 * @param _Parameter
	 * @param _TunerGroup
	 * @param _Res
	 */
	public TunerWorkGroup(TunerWorkParameter _Parameter, ArrayList<Tuner> _TunerGroup, Result _Res){
		Parameter = _Parameter;
		TunerGroup = _TunerGroup;
		Res = _Res;
	}
	/***
	 * 构造函数
	 * @param _Parameter
	 */
	public TunerWorkGroup(TunerWorkParameter _Parameter){
		Parameter = _Parameter;
	}
	
	// setters
	public void setTunerWorkParameter(TunerWorkParameter _Parameter){
		Parameter = _Parameter;
	}
	public void setTunerGroup(ArrayList<Tuner> _TunerGroup){
		TunerGroup = _TunerGroup;
	}
	public void setTunerGroup(ArrayList<Tuner> AvailableTunerGroup, ArrayList<Integer> ChooseResult){
		if(TunerGroup != null)TunerGroup.clear();
		else{
			TunerGroup = new ArrayList<Tuner>();
		}
		for(int TunerNum:ChooseResult){
			for(Tuner tuner:AvailableTunerGroup){
				if(TunerNum == tuner.getTunerID()){
					TunerGroup.add(tuner);
				}
			}
		}
	}
	public void setResult(Result _Res){
		Res = _Res;
	}
	
	// getters
	public TunerWorkParameter getParameter(){
		return Parameter;
	}
	public ArrayList<Tuner> getTunerGroup(){
		return TunerGroup;
	}
	public Result getResult(){
		return Res;
	}
	
	/***
	 * 同步接收机工作组中所有接收机
	 * @param _Parameter
	 * @param _TunerGroup
	 */
	public void SyncTunerGroup(TunerWorkParameter _Parameter, ArrayList<Tuner> _TunerGroup){
		
	}
	

}