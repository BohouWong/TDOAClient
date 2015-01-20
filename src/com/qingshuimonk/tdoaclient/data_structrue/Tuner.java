package com.qingshuimonk.tdoaclient.data_structrue;

import java.util.concurrent.BlockingQueue;

/***
 * This is a class defines variables and functions of Tuner
 * @author Huang Bohao
 * @version 1.0
 * @since 2014.11.10
 * 
 * @Notice TunerIP,Pos,WorkMode,AveVoltage cannot be changed in client
 */

public class Tuner{
	private byte TunerID;						// Number of receiver
	private Position Pos;						// Position of Receiver
	private byte WorkMode;						// Work mode: 1:available	0:unavailable
	private TunerWorkParameter Parameter;		// Work parameter
	private double AveVoltage;					// Average voltage
	private IQ IQqueue;							// IQ data
	private FreqSpectrum FreqSpectrum;			// Frequency Spectrum

	/***
	 * Constructor of Tuner		
	 * @param _TunerID			Number of receiver
	 * @param _Pos				Position of Receiver
	 * @param _WorkMode			Work mode: 1:available	0:unavailable
	 * @param AveVoltage		Average voltage
	 */
	public Tuner(byte _TunerID, Position _Pos, byte _WorkMode, double _AveVoltage){
		TunerID = _TunerID;
		Pos = _Pos;
		WorkMode = _WorkMode;
		AveVoltage = _AveVoltage;
	}
	
	/***
	 * Set TunerWorkParameter
	 * @param _Parameter
	 */
	public void setTunerWorkParameter(TunerWorkParameter _Parameter){
		Parameter = _Parameter;
	}
	
	/***
	 * Set IQData
	 * @param _IQData
	 */
	public void setIQData(IQ _IQqueue){
		IQqueue = _IQqueue;
	}
	
	/***
	 * Set FreqSpectrum
	 * @param _FreqSpectrum
	 */
	public void setFreqSpectrum(FreqSpectrum _FreqSpectrum){
		FreqSpectrum = _FreqSpectrum;
	}
	
	/***
	 * Get TunerID
	 * @return TunerID
	 */
	public byte getTunerID(){
		return TunerID;
	}
	
	/***
	 * Get Pos
	 * @return Pos
	 */
	public Position getPosition(){
		return Pos;
	}
	
	/***
	 * Get WorkMode
	 * @return WorkMode
	 */
	public byte getWorkMode(){
		return WorkMode;
	}
	
	/***
	 * Get Parameter
	 * @return Parameter
	 */
	public TunerWorkParameter getParameter(){
		return Parameter;
	}
	
	/***
	 * Get AveVoltage
	 * @return AveVoltage
	 */
	public double getAveVoltage(){
		return AveVoltage;
	}
	
	/***
	 * Get IQ
	 * @return IQqueue
	 */
	public IQ getIQqueue(){
		return IQqueue;
	}
	
	/***
	 * Get FreqSpectrum
	 * @return FreqSpectrum
	 */
	public FreqSpectrum getFreqSpectrum(){
		return FreqSpectrum;
	}

}