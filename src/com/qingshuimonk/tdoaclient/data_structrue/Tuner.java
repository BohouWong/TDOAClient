package com.qingshuimonk.tdoaclient.data_structrue;


/***
 * �������ڶ�����ջ�������صı����ͷ���
 * @author Huang Bohao
 * @version 1.0
 * @since 2014.11.10
 * 
 * @Notice TunerIP,Pos,WorkMode,AveVoltage�ڿͻ��˲��ܸ���
 */

public class Tuner{
	private byte TunerID;						// ���ջ�ID
	private Position Pos;						// ���ջ�λ��
	private byte WorkMode;						// ���ջ�����ģʽ
	private TunerWorkParameter Parameter;		// ���ջ���������
	private double AveVoltage;					// ƽ����ƽֵ
	private IQ IQqueue;						// IQ����
	private FreqSpectrum FreqSpectrum;			// Ƶ������

	/***
	 * ���캯��		
	 * @param _TunerID			���ջ�ID
	 * @param _Pos				���ջ�λ��
	 * @param _WorkMode			���ջ�����ģʽ: 1:����	0:������
	 * @param AveVoltage		ƽ����ƽֵ
	 */
	public Tuner(byte _TunerID, Position _Pos, byte _WorkMode, double _AveVoltage){
		TunerID = _TunerID;
		Pos = _Pos;
		WorkMode = _WorkMode;
		AveVoltage = _AveVoltage;
	}
	
	// setters
	public void setTunerWorkParameter(TunerWorkParameter _Parameter){
		Parameter = _Parameter;
	}
	public void setIQData(IQ _IQqueue){
		IQqueue = _IQqueue;
	}
	public void setFreqSpectrum(FreqSpectrum _FreqSpectrum){
		FreqSpectrum = _FreqSpectrum;
	}
	
	// getters
	public byte getTunerID(){
		return TunerID;
	}
	public Position getPosition(){
		return Pos;
	}
	public byte getWorkMode(){
		return WorkMode;
	}
	public TunerWorkParameter getParameter(){
		return Parameter;
	}
	public double getAveVoltage(){
		return AveVoltage;
	}
	public IQ getIQqueue(){
		return IQqueue;
	}
	public FreqSpectrum getFreqSpectrum(){
		return FreqSpectrum;
	}

}