package com.qingshuimonk.tdoaclient.data_structrue;

import java.util.ArrayList;
import java.util.Iterator;

/***
 * This is a class defines variables and functions of TunerWorkGroup
 * @author Huang Bohao
 * @version 1.1
 * @since 2014.11.10
 * 
 * @Notice Result cannot be changed in client
 * @Problem Function SyncTunerGroup need to be finished
 */

public class TunerWorkGroup{
	private TunerWorkParameter Parameter;
	private ArrayList<Tuner> TunerGroup;			// Container of Tuner
	private Result Res;

	/***
	 * Constructor of TunerWorkGroup
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
	 * Constructor of TunerWorkGroup
	 * @param _Parameter
	 */
	public TunerWorkGroup(TunerWorkParameter _Parameter){
		Parameter = _Parameter;
	}
	
	/***
	 * Set Parameter
	 * @param _Parameter
	 */
	public void setTunerWorkParameter(TunerWorkParameter _Parameter){
		Parameter = _Parameter;
	}
	
	/***
	 * Set TunerGroup
	 * @param _TunerGroup
	 */
	public void setTunerGroup(ArrayList<Tuner> _TunerGroup){
		TunerGroup = _TunerGroup;
	}
	/***
	 * Set TunerGroup by providing an available tuner list and choose result
	 * @param AvailableTunerGroup
	 * @param ChooseResultit
	 */
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
	
	/***
	 * Set Result
	 * @param _Res
	 */
	public void setResult(Result _Res){
		Res = _Res;
	}
	
	/***
	 * Get Parameter
	 * @return Parameter
	 */
	public TunerWorkParameter getParameter(){
		return Parameter;
	}
	
	/***
	 * Get TunerGroup
	 * @return TunerGroup
	 */
	public ArrayList<Tuner> getTunerGroup(){
		return TunerGroup;
	}
	
	/***
	 * Get Result
	 * @return Result
	 */
	public Result getResult(){
		return Res;
	}
	
	/***
	 * Synchronize all the tuner to make them share the same TunerWorkParameter
	 * @param _Parameter
	 * @param _TunerGroup
	 */
	public void SyncTunerGroup(TunerWorkParameter _Parameter, ArrayList<Tuner> _TunerGroup){
		
	}
	

}