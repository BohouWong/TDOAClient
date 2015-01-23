package com.qingshuimonk.tdoaclient.data_structrue;

import java.util.concurrent.BlockingQueue;

/***
 * �������ڶ���Ƶ��������صı����ͷ���
 * @author Huang Bohao
 * @version 1.0
 * @since 2014.11.10
 */

public class FreqSpectrum{
	private BlockingQueue<Integer> Freqqueue;
	private DateTime Time;
	
	/***
	 * ���캯��
	 * @param _Freqqueue
	 * @param _Time
	 */
	public FreqSpectrum(BlockingQueue<Integer> _Freqqueue, DateTime _Time){
		Freqqueue = _Freqqueue;
		Time = _Time;
	}
	
	// setters
	public void setFreqSpectrum(BlockingQueue<Integer> _Freqqueue){
		Freqqueue = _Freqqueue;
	}
	
	// getters
	public BlockingQueue<Integer> getFreqSpectrum(){
		return Freqqueue;
	}
	public DateTime getTime(){
		return Time;
	}

}