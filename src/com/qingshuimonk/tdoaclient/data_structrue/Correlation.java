package com.qingshuimonk.tdoaclient.data_structrue;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

/***
 * This is a class defines variables and functions of Correlation
 * Serializable is implemented for transfer between activities
 * @author Huang Bohao
 * @version 2.0
 * @since 2014.11.22
 * 
 * @problem Operations corresponding to Queue needs to be verified
 */

public class Correlation implements Serializable{
	
	private static final long serialVersionUID = -478341915603189981L;
	private byte RefTunerID;
	private byte OtherTunerID;
	private BlockingQueue<Integer> Cor;
	private DateTime Time;
	
	/***
	 * Constructor of Correlation
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
	
	/***
	 * Set Cor of Correlation
	 * @param _Cor
	 */
	public void setCor(BlockingQueue<Integer> _Cor){
		Cor = _Cor;
	}
	
	/***
	 * Set Time of Correlation
	 * @param _Time
	 * It represents to the time when sever completes location request
	 */
	public void setDateTime(DateTime _Time){
		Time = _Time;
	}
	
	/***
	 * Get RefTunerID of Correlation
	 * @return RefTunerID
	 */
	public byte getRefTunerID(){
		return RefTunerID;
	}
	
	/***
	 * Get OtherTunerID of Correlation
	 * @return OtherTunerID
	 */
	public byte getOtherTunerID(){
		return OtherTunerID;
	}
	
	/***
	 * Get Cor of Correlation
	 * @return Cor
	 */
	public BlockingQueue<Integer> getCor(){
		return Cor;
	}
	
	/***
	 * Get Time of Correlation
	 * @return Time
	 */
	public DateTime getTime(){
		return Time;
	}

}