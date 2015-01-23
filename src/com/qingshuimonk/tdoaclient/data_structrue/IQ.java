package com.qingshuimonk.tdoaclient.data_structrue;

import java.util.concurrent.BlockingQueue;

/***
 * �������ڶ���IQ������صı����ͷ���
 * @author Huang Bohao
 * @version 1.0
 * @since 2014.11.10
 */

public class IQ{
	private BlockingQueue<Integer> IQqueue;
	
	/***
	 * ���캯��
	 * @param _I
	 * @param _Q
	 */
	public IQ(int _I, int _Q){
		IQqueue.add(_I);
		IQqueue.add(_Q);
	}
	
	/***
	 * ���캯��
	 * @param _IQ
	 */
	public IQ(int _IQ[]){
		for(int i = 0; i < _IQ.length; i++){
			IQqueue.add(_IQ[i]);
		}
	}
	
	// getters
	public int[] getIQ(){
		int IQ[] = new int[2];
		IQ[0] = IQqueue.poll();
		IQ[1] = IQqueue.poll();
		return IQ;
	}

	/***
	 * ����vpp
	 * @param IQqueue
	 * @return Vpp
	 */
	public double CalVpp(BlockingQueue<Integer> IQqueue){
		double Vpp = 0;
		return Vpp;
	}

}