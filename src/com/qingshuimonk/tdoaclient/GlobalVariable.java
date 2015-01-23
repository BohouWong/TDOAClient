package com.qingshuimonk.tdoaclient;

import java.util.ArrayList;

import com.qingshuimonk.tdoaclient.data_structrue.Tuner;
import com.qingshuimonk.tdoaclient.data_structrue.User;


import android.app.Application;

/***
 * 本类用于为个activity或类提供全局变量
 * 需要在AndroidManifest.xml中修改application标签下name属性为:com.qingshuimonk.tdoaclient.GlobalVariable
 * @author Huang Bohao
 * @version 2.0.1
 * @since 11/13/2014
 *
 * 12/15/2014 2.0.0 修改说明：
 * 添加了udp网络连接相关参数和系统设置相关参数
 * 用于减少硬编码，规范代码，方便调试
 * 
 * 01/19/2015 2.0.1 修改说明
 * 添加了用于百度地图显示的相关字体和颜色
 */
public class GlobalVariable extends Application
{	
	public User SysUser = new User();					// 用户类，登陆后创建，包含其他所有数据结构内容
	// 可选用接收机组
	public ArrayList<Tuner> AvailableTuner = new ArrayList<Tuner>();
	
	public boolean DEBUG_NETWORK_CONDITION = true;	// 互联网调试参数
	public boolean DEBUG_UDP_CONNECTION = true;		// 为true则通过udp接收指令并解析
	
	// udp连接变量
	public int Local_Port = 8080;
	public int Server_Port = 8080;
	public int Test_Port = 8080;
	public String Server_Address = "192.168.1.105";
	public String Test_Address = "192.168.1.108";
	
	// 系统设置参数
	public long MIN_CENTER_FRE = 20000000L;			// 最小中心频率
	public long MAX_CENTER_FRE = 3600000000L;			// 最大中心频率
	public int MAX_IQ_NUM = 64;						// 最大IQ值
	public int MIN_IQ_NUM = 0;							// 最小IQ值
	public int MAX_MGC = 50;							// 最大手动增益
	public int MIN_MGC = 0;							// 最小手动增益
	public int TIME_FOR_SET = 1;						// 时间触发能设置的最短时间
	public int TIME_FOR_SERVER = 0;					// 时间触发设置完成后留给服务器的最短时间
	public int MIN_TRIG_POWER = -10;					// 最小能量触发值
	public int MAX_TRIG_POWER = 128;					// 最大能量触发值
	public int MAX_REC_NUM = 6;						// 最大参与一次定位的接收机值
	public int MIN_REC_NUM = 4;						// 最小参与一次定位的接收机值
	
	// 地图显示相关颜色和字体
	public int MAP_BG_COLOR = 0x00000000;				// 全透明背景色
	public int MAP_FONT_COLOR_UNCHOSEN = 0xFF27408B;	// 未选中字体颜色为深蓝色
	public int MAP_FONT_COLOR_CHOSEN = 0x00EE0000;		// 选中字体为红色
	public int MAP_FONT_SIZE = 10;
}