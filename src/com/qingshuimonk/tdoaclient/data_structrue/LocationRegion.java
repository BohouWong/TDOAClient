package com.qingshuimonk.tdoaclient.data_structrue;

/***
 * �������ڶ��嶨λ������صı����ͷ���
 * @author Huang Bohao
 * @version 1.0
 * @since 2014.11.10
 */

public class LocationRegion{
	private byte RegionMode;			// ��λ��������
	private double RegionValue1;
	private double RegionValue2;
	private double RegionValue3;
	private double RegionValue4;
	
	/***
	 * ���캯��
	 * @param _RegionMode		1:Բ�ζ�λ����;			0:���ζ�λ����
	 * @param _RegionValue1		1:Բ�ľ���;				0:���ϵ㾭�ȣ�
	 * @param _RegionValue2		1:Բ��γ��;				0:���ϵ�γ�ȣ�
	 * @param _RegionValue3		1:Բ�뾶;				0:���µ㾭�ȣ�
	 * @param _RegionValue4		1:ȱʡ;					0:���µ�γ�ȣ�
	 */
	public LocationRegion(byte _RegionMode, double _RegionValue1, double _RegionValue2, double _RegionValue3, double _RegionValue4){
		RegionMode = _RegionMode;
		RegionValue1 = _RegionValue1;
		RegionValue2 = _RegionValue2;
		RegionValue3 = _RegionValue3;
		RegionValue4 = _RegionValue4;
	}
	
	// setters 
	public void setRegionMode(byte _RegionMode){
		RegionMode = _RegionMode;
	}
	public void setRegionValue1(byte _RegionValue1){
		RegionValue1 = _RegionValue1;
	}
	public void setRegionValue2(byte _RegionValue2){
		RegionValue2 = _RegionValue2;
	}
	public void setRegionValue3(byte _RegionValue3){
		RegionValue3 = _RegionValue3;
	}
	public void setRegionValue4(byte _RegionValue4){
		RegionValue4 = _RegionValue4;
	}
	
	// getters
	public byte getRegionMode(){
		return RegionMode;
	}
	public double getRegionValue1(){
		return RegionValue1;
	}
	public double getRegionValue2(){
		return RegionValue2;
	}
	public double getRegionValue3(){
		return RegionValue3;
	}
	public double getRegionValue4(){
		return RegionValue4;
	}

}