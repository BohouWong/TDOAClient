package com.qingshuimonk.tdoaclient;

import java.util.ArrayList;

import com.qingshuimonk.tdoaclient.data_structrue.Tuner;
import com.qingshuimonk.tdoaclient.data_structrue.User;


import android.app.Application;

/***
 * ��������Ϊ��activity�����ṩȫ�ֱ���
 * ��Ҫ��AndroidManifest.xml���޸�application��ǩ��name����Ϊ:com.qingshuimonk.tdoaclient.GlobalVariable
 * @author Huang Bohao
 * @version 2.0.1
 * @since 11/13/2014
 *
 * 12/15/2014 2.0.0 �޸�˵����
 * �����udp����������ز�����ϵͳ������ز���
 * ���ڼ���Ӳ���룬�淶���룬�������
 * 
 * 01/19/2015 2.0.1 �޸�˵��
 * ��������ڰٶȵ�ͼ��ʾ������������ɫ
 */
public class GlobalVariable extends Application
{	
	public User SysUser = new User();					// �û��࣬��½�󴴽������������������ݽṹ����
	// ��ѡ�ý��ջ���
	public ArrayList<Tuner> AvailableTuner = new ArrayList<Tuner>();
	
	public boolean DEBUG_NETWORK_CONDITION = true;	// ���������Բ���
	public boolean DEBUG_UDP_CONNECTION = true;		// Ϊtrue��ͨ��udp����ָ�����
	
	// udp���ӱ���
	public int Local_Port = 8080;
	public int Server_Port = 8080;
	public int Test_Port = 8080;
	public String Server_Address = "192.168.1.105";
	public String Test_Address = "192.168.1.108";
	
	// ϵͳ���ò���
	public long MIN_CENTER_FRE = 20000000L;			// ��С����Ƶ��
	public long MAX_CENTER_FRE = 3600000000L;			// �������Ƶ��
	public int MAX_IQ_NUM = 64;						// ���IQֵ
	public int MIN_IQ_NUM = 0;							// ��СIQֵ
	public int MAX_MGC = 50;							// ����ֶ�����
	public int MIN_MGC = 0;							// ��С�ֶ�����
	public int TIME_FOR_SET = 1;						// ʱ�䴥�������õ����ʱ��
	public int TIME_FOR_SERVER = 0;					// ʱ�䴥��������ɺ����������������ʱ��
	public int MIN_TRIG_POWER = -10;					// ��С��������ֵ
	public int MAX_TRIG_POWER = 128;					// �����������ֵ
	public int MAX_REC_NUM = 6;						// ������һ�ζ�λ�Ľ��ջ�ֵ
	public int MIN_REC_NUM = 4;						// ��С����һ�ζ�λ�Ľ��ջ�ֵ
	
	// ��ͼ��ʾ�����ɫ������
	public int MAP_BG_COLOR = 0x00000000;				// ȫ͸������ɫ
	public int MAP_FONT_COLOR_UNCHOSEN = 0xFF27408B;	// δѡ��������ɫΪ����ɫ
	public int MAP_FONT_COLOR_CHOSEN = 0x00EE0000;		// ѡ������Ϊ��ɫ
	public int MAP_FONT_SIZE = 10;
}