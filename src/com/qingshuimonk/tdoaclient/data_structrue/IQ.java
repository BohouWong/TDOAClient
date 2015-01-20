package com.qingshuimonk.tdoaclient.data_structrue;

import java.util.concurrent.BlockingQueue;

/***
 * This is a class defines variables and functions of IQ
 * @author Huang Bohao
 * @version 1.0
 * @since 2014.11.10
 */

public class IQ{
	private BlockingQueue<Integer> IQqueue;
	
	/***
	 * Constructor of IQ: add a pair of IQ data to queue
	 * @param _I
	 * @param _Q
	 */
	public IQ(int _I, int _Q){
		IQqueue.add(_I);
		IQqueue.add(_Q);
	}
	
	/***
	 * Constructor of IQ: add a block of IQ data to queue
	 * @param _IQ
	 */
	public IQ(int _IQ[]){
		for(int i = 0; i < _IQ.length; i++){
			IQqueue.add(_IQ[i]);
		}
	}
	
	/***
	 * Get the head of IQ data in queue
	 * @return IQ: the pair of IQ data in the head of the queue
	 */
	public int[] getIQ(){
		int IQ[] = new int[2];
		IQ[0] = IQqueue.poll();
		IQ[1] = IQqueue.poll();
		return IQ;
	}

	/***
	 * Calculate Vpp based IQ queue
	 * @param IQqueue
	 * @return Vpp
	 */
	public double CalVpp(BlockingQueue<Integer> IQqueue){
		double Vpp = 0;
		return Vpp;
	}

}