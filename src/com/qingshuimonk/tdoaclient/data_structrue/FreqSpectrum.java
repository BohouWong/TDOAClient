package com.qingshuimonk.tdoaclient.data_structrue;

import java.util.concurrent.BlockingQueue;

/***
 * This is a class defines variables and functions of FreqSpectrum
 * @author Huang Bohao
 * @version 1.0
 * @since 2014.11.10
 * 
 * @problem Functions of frequency spectrum needs to be added
 */

public class FreqSpectrum{
	private BlockingQueue<Integer> Freqqueue;
	private DateTime Time;
	
	/***
	 * Constructor of FreqSpectrum
	 * @param _Freqqueue
	 * @param _Time
	 */
	public FreqSpectrum(BlockingQueue<Integer> _Freqqueue, DateTime _Time){
		Freqqueue = _Freqqueue;
		Time = _Time;
	}
	
	/***
	 * Set Freqqueue of FreqSpectrum
	 * @param _Freqqueue
	 */
	public void setFreqSpectrum(BlockingQueue<Integer> _Freqqueue){
		Freqqueue = _Freqqueue;
	}
	
	/***
	 * Get Freqqueue of FreqSpectrum
	 * @return Freqqueue
	 */
	public BlockingQueue<Integer> getFreqSpectrum(){
		return Freqqueue;
	}
	
	/***
	 * Get Time of FreqSpectrum
	 * @return Time
	 */
	public DateTime getTime(){
		return Time;
	}

}