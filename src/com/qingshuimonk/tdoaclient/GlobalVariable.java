package com.qingshuimonk.tdoaclient;

import java.util.ArrayList;

import com.qingshuimonk.tdoaclient.data_structrue.Tuner;
import com.qingshuimonk.tdoaclient.data_structrue.User;


import android.app.Application;

/***
 * This is a fine used to provide access to global variables for different activities 
 * The default application should be changed in AndroidManifest.xml to set this application
 * as the first launched activity
 * @author Huang Bohao
 * @version 2.0.0
 * @since 2014.11.13
 *
 */
public class GlobalVariable extends Application
{	
	public User SysUser = new User();
	public ArrayList<Tuner> AvailableTuner = new ArrayList<Tuner>();
	
	public boolean DEBUG_NETWORK_CONDITION = true;	// used for debug situation
	public boolean DEBUG_UDP_CONNECTION = true;
	
	//	udp connection variables
	public int Local_Port = 8080;
	public int Server_Port = 8080;
	public int Test_Port = 8080;
	public String Server_Address = "192.168.1.105";
	public String Test_Address = "192.168.1.108";
	
	// parameter set variables
	public long MIN_CENTER_FRE = 20000000L;
	public long MAX_CENTER_FRE = 3600000000L;
	public int MAX_IQ_NUM = 64;
	public int MIN_IQ_NUM = 0;
	public int MAX_MGC = 50;
	public int MIN_MGC = 0;
	public int TIME_FOR_SET = 1;
	public int TIME_FOR_SERVER = 0;
	public int MIN_TRIG_POWER = -10;
	public int MAX_TRIG_POWER = 128;
	public int MAX_REC_NUM = 6;
	public int MIN_REC_NUM = 4;
	
	// 地图显示相关颜色和字体
	public int MAP_BG_COLOR = 0x00000000;						// 全透明背景色
	public int MAP_FONT_COLOR_UNCHOSEN = 0xFF27408B;			// 未选中字体颜色为深蓝色
	public int MAP_FONT_COLOR_CHOSEN = 0x00EE0000;				// 选中字体为红色
	public int MAP_FONT_SIZE = 10;
}