package com.qingshuimonk.tdoaclient.data_structrue;

/***
 * �������ڶ�����ջ������鹤������������صı����ͷ���
 * @author Huang Bohao
 * @version 1.0
 * @since 2014.11.10
 */

public class TunerWorkParameter{
	private long CenterFreq;			// ����Ƶ��
	private int BandWidth; 			// ����
	private byte TrigMode;				// ������ʽ
	private DateTime TrigTime; 		// ����ʱ��
	private short TrigPower;			// ��������
	private byte IQNum;				// ÿ��IQ���ݣ���λkB
	private byte MGC;					// �ֶ��������
	private LocationRegion Region;		// ��λ����
	
	/***
	 * ����
	 * @param _CenterFreq
	 * @param _BandWidth
	 * @param _TrigMode: 	0:ʱ�䴥��ģʽ;	1:��������ģʽ
	 * @param _TrigTime
	 * @param _TrigPower
	 * @param _IQNum
	 * @param _MGC
	 * @param _Region
	 */
	public TunerWorkParameter(long _CenterFreq, int _BandWidth, byte _TrigMode, DateTime _TrigTime, 
			short _TrigPower, byte _IQNum, byte _MGC, LocationRegion _Region){
		CenterFreq = _CenterFreq;
		BandWidth = _BandWidth;
		TrigMode = _TrigMode;
		TrigTime = _TrigTime;
		TrigPower = _TrigPower;
		IQNum = _IQNum;
		MGC = _MGC;
		Region = _Region;
	}
	/***
	 * ���캯��
	 * @param _Region
	 */
	public TunerWorkParameter(LocationRegion _Region){
		Region = _Region;
	}
	
	// setters
	public void setCenterFreq(long _CenterFreq){
		CenterFreq = _CenterFreq;
	}
	public void setBandWidth(int _BandWidth){
		BandWidth = _BandWidth;
	}
	public void setTrigMode(byte _TrigMode){
		TrigMode = _TrigMode;
	}
	public void setTrigTime(DateTime _TrigTime){
		TrigTime = _TrigTime;
	}
	public void setTrigPower(short _TrigPower){
		TrigPower = _TrigPower;
	}
	public void setIQNum(byte _IQNum){
		IQNum = _IQNum;
	}
	public void setMGC(byte _MGC){
		MGC = _MGC;
	}
	public void setLocationRegion(LocationRegion _Region){
		Region = _Region;
	}
	
	// getters
	public long getCenterFreq(){
		return CenterFreq;
	}
	public int getBandWidth(){
		return BandWidth;
	}
	public byte getTrigMode(){
		return TrigMode;
	}
	public DateTime getTrigTime(){
		return TrigTime;
	}
	public short getTrigPower(){
		return TrigPower;
	}
	public byte getIQNum(){
		return IQNum;
	}
	public byte getMGC(){
		return MGC;
	}
	public LocationRegion getLocationRegion(){
		return Region;
	}

}