package com.qingshuimonk.tdoaclient;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.qingshuimonk.tdoaclient.data_structrue.DateTime;
import com.qingshuimonk.tdoaclient.data_structrue.LocationRegion;
import com.qingshuimonk.tdoaclient.data_structrue.Position;
import com.qingshuimonk.tdoaclient.data_structrue.Tuner;
import com.qingshuimonk.tdoaclient.data_structrue.TunerWorkGroup;
import com.qingshuimonk.tdoaclient.data_structrue.TunerWorkParameter;
import com.qingshuimonk.tdoaclient.data_structrue.User;
import com.qingshuimonk.tdoaclient.utils.Communicator;
import com.qingshuimonk.tdoaclient.utils.ReceiverView;
import com.qingshuimonk.tdoaclient.utils.SysApplication;
import com.qingshuimonk.tdoaclient.utils.FrameFormer.FRAME_TYPE;

/***
 * 本activity用于定义接收机选择界面
 * 配套xml文件: activity_receiver_choose.xml
 * 功能:		
 * 1.向用户显示可用接收机及其工作状态；
 * 2.引导用户完成接收机选择；
 * 3.向服务器发送用户选择结果；
 * @author Huang Bohao
 * @version 1.2.0
 * @since 2014.11.18
 *  
 * 01/13/2015 1.1.0 修改说明：
 * 添加了结果地图显示功能
 * 
 * 01/17/2015 1.2.0 修改说明：
 * 在结果地图显示中运用新的view，显示图文覆盖物
 */
public class ReceiverChooseActivity extends Activity{
	
	// 创建ArrayList和ArrayAdapter
	ArrayList<Tuner> receiveritem = new ArrayList<Tuner>();					// 用于显示所有接收机
	ArrayList<Tuner> AvailableTunerGroup = new ArrayList<Tuner>();			// 用于用户选择接收机	
	ReceiverAdapter adapter;
	
	// Baidu地图
	MapView mMapView = null;  
	BaiduMap ReceiverBaiduMap = null;
	LatLng[] receiverpos = null;
	
	// 初始化确认数组
	String show = new String();
	
	// udp连接相关参数定义
	final int MAX_DATA_PACKET_LENGTH = 40;  
    byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];  
    DatagramSocket udpSocket;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.activity_receiver_choose);
		
		// 将此activity添加到SysApplication类中
		SysApplication.getInstance().addActivity(this);
		
		// 获取GlobalVariable类的全局变量
		final GlobalVariable GV = (GlobalVariable)getApplicationContext(); 
		
		// 创建action bar
		ActionBar LoginActionBar = this.getActionBar();
		LoginActionBar.setDisplayShowTitleEnabled(true);	// ActionBar只显示title
		LoginActionBar.setDisplayShowHomeEnabled(false);
		
		final ListView receiverList = (ListView)findViewById(R.id.receiverlist);
		final Button ReceiverChooseNext = (Button)findViewById(R.id.receiverchoosenext);
		
		// 链接ArrayList和ArrayAdapter
		adapter = new ReceiverAdapter(this, R.layout.item_receiver, receiveritem);
		receiverList.setAdapter(adapter);
		
		// 获取百度地图的引用
        mMapView = (MapView) findViewById(R.id.receiverchoose_Map);  
        ReceiverBaiduMap = mMapView.getMap();
		
		if(GV.DEBUG_UDP_CONNECTION){
			// 发送数据请求
			final ProgressDialog SendMessage = new ProgressDialog(ReceiverChooseActivity.this);
			SendMessage.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			SendMessage.setMessage("等待向服务器发送参数");
			SendMessage.setCancelable(false);
			SendMessage.show();
			// 配置handler
			final Handler sendhandler = new Handler() {  
				@SuppressLint("HandlerLeak")
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					if(msg.what == 0){
						SendMessage.dismiss();
						}
				}     
			}; 
			
			// 调用Communicator工具类
			Communicator DataRequest_cator = new Communicator(sendhandler);
			DataRequest_cator.sendDataRequest(GV, FRAME_TYPE.DATA_REQUEST, (byte) 0x02);
			
			// 获取服务器信息
			final ProgressDialog ReceiveMessage = new ProgressDialog(
					ReceiverChooseActivity.this);
			ReceiveMessage.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			ReceiveMessage.setMessage("等待从服务器接收参数");
			ReceiveMessage.setCancelable(false);
			ReceiveMessage.setButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					AlertDialog CancelReaffirmDialog = new AlertDialog.Builder(
							ReceiverChooseActivity.this)
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setTitle("取消接收信息:")
							.setMessage("是否确认取消本次接收？")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											// 监控取消
											Intent _intent = new Intent(
													ReceiverChooseActivity.this, LocationParameterActivity.class);
											startActivity(_intent);
										}
									}).setNegativeButton("取消", null).create();
					CancelReaffirmDialog.show();
				}
			});
			ReceiveMessage.show();
			// 配置handler
			final Handler receivehandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					if (msg.what != 0) {
						for(Tuner tuner:GV.AvailableTuner){
							receiveritem.add(tuner);
							AvailableTunerGroup.add(tuner);
							
							// 将接收机添加到地图
							addReceiver2Map(GV, ReceiverBaiduMap);
							
							// 隐藏百度地图logo和放大缩小控件
							int count = mMapView.getChildCount();
					        for (int i = 0; i < count; i++) {
					            View child = mMapView.getChildAt(i);
					            if (child instanceof ImageView || child instanceof ZoomControls)
					                child.setVisibility(View.INVISIBLE);
					        }
					        
					        LatLngBounds.Builder boundsbuilder = new LatLngBounds.Builder();
							for (int i = 0; i < receiverpos.length; i++) {
								boundsbuilder.include(receiverpos[i]);
							}
							LatLngBounds bounds = boundsbuilder.build();
							MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
							ReceiverBaiduMap.animateMapStatus(u, 1000);
						}
						adapter.notifyDataSetChanged();
						ReceiveMessage.dismiss();
					}
				}
			};
			
			// 调用Communicator类
			Communicator Receiver_cator = new Communicator(receivehandler);
			Receiver_cator.receiveData(GV, FRAME_TYPE.AVAILABLE_RECEIVER);	// 解析指令帧
		}
		else{
			// 无wifi连接
			// 创建虚拟接收机，并初始化其状态和位置
			Position receiverPos = new Position(12.34567,23.45678,34.56789);
			Tuner receiverTuner1 = new Tuner((byte)1,receiverPos,(byte)1,(short)10);
			Tuner receiverTuner2 = new Tuner((byte)2,receiverPos,(byte)1,(short)20);
			Tuner receiverTuner3 = new Tuner((byte)3,receiverPos,(byte)1,(short)30);
			Tuner receiverTuner4 = new Tuner((byte)4,receiverPos,(byte)1,(short)40);
			Tuner receiverTuner5 = new Tuner((byte)5,receiverPos,(byte)1,(short)50);
			receiveritem.add(receiveritem.size(),receiverTuner1);
			receiveritem.add(receiveritem.size(),receiverTuner2);
			receiveritem.add(receiveritem.size(),receiverTuner3);
			receiveritem.add(receiveritem.size(),receiverTuner4);
			receiveritem.add(receiveritem.size(),receiverTuner5);
			adapter.notifyDataSetChanged();
			
			// 向ArrayList添加接收机
			AvailableTunerGroup.add(receiverTuner1);
			AvailableTunerGroup.add(receiverTuner2);
			AvailableTunerGroup.add(receiverTuner3);
			AvailableTunerGroup.add(receiverTuner4);
			AvailableTunerGroup.add(receiverTuner5);
		}
		
		// “下一步”按键监听函数
		ReceiverChooseNext.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				byte ReceiverNum;
				final ArrayList<Integer> ChooseResult = adapter.getChooseResult();
				ReceiverNum = adapter.getReceiverNum();
				
				if((ReceiverNum > GV.MAX_REC_NUM)||(ReceiverNum < GV.MIN_REC_NUM)){
					// 接收机选择个数无效
					Toast.makeText(getApplicationContext(), "选择无效\n接收机选择数目应为"+GV.MIN_REC_NUM+"~"+GV.MAX_REC_NUM
							+"个！", Toast.LENGTH_SHORT).show();
				}
				else{
					// 接收机选择结果有效
					// 保存选择结果
					TunerWorkGroup Group = GV.SysUser.getWorkGroup();
					Group.setTunerGroup(AvailableTunerGroup, ChooseResult);
					
					// 产生确认字符串
					show = generateConfirmString(GV.SysUser);
					
					// 显示确认对话框
					Dialog AffirmDialog = new AlertDialog.Builder(ReceiverChooseActivity.this)
					.setIcon(android.R.drawable.ic_dialog_info).setTitle("请确认配置参数:")
					.setMessage(show)
					.setNegativeButton("取消", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO 取消确认配置参数处理函数
							
						}
					})
					.setPositiveButton("确定", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 判断是否有足够时间供服务器配置定位请求
							final TunerWorkParameter parameter = GV.SysUser.getWorkGroup().getParameter();
							final DateTime trigtime = parameter.getTrigTime();
							if(parameter.getTrigMode() == 0){
								// 再确认时间
								// 获取系统时间
								int year, month, day, hour ,minute;
								final Calendar c = Calendar.getInstance();
								year = c.get(Calendar.YEAR);
							    month = c.get(Calendar.MONTH)+1;  
							    day = c.get(Calendar.DAY_OF_MONTH);
							    hour = c.get(Calendar.HOUR_OF_DAY);  
							    minute = c.get(Calendar.MINUTE);
							    // 获取触发时间
							    int setYear, setMonth, setDay, setHour, setMinute;
							    setYear = trigtime.getYear();
							    setMonth = trigtime.getMonth();
							    setDay = trigtime.getDay();
							    setHour = trigtime.getHour();
							    setMinute = trigtime.getMinute();
							    // 计算时间差
							    long currentTime, setTime;
							    currentTime = 
							    		year*100000000L + month*1000000L + day*10000 + hour*100 + minute;
							    setTime = 
							    		setYear*100000000L + setMonth*1000000L + setDay*10000 + setHour*100 + setMinute;
							    // 时间差小于设定时间分钟
							    if(currentTime+ GV.TIME_FOR_SERVER > setTime){
							    	Dialog InvalidTrigTime = new AlertDialog.Builder(ReceiverChooseActivity.this)
							    	.setIcon(android.R.drawable.ic_dialog_alert).setTitle("无效触发时间")
							    	.setMessage("服务器所剩配置接收机时间不足"+GV.TIME_FOR_SERVER+"分钟，是否自动延长触发时间？")
							    	.setPositiveButton("确定", new DialogInterface.OnClickListener(){
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											trigtime.setMinute((short) (c.get(Calendar.MINUTE)+GV.TIME_FOR_SERVER));
											
											// 产生确认字符串
											show = generateConfirmString(GV.SysUser);
											
											// 显示确认对话框
											Dialog ReAffirmDialog = new AlertDialog.Builder(ReceiverChooseActivity.this)
											.setIcon(android.R.drawable.ic_dialog_info).setTitle("请确认配置参数:")
											.setMessage(show)
											.setPositiveButton("确定", new DialogInterface.OnClickListener(){
												@SuppressLint("HandlerLeak")
												@Override
												public void onClick(DialogInterface dialog,int which) {
													// 向服务器发送定位参数
													final ProgressDialog SendMessage = new ProgressDialog(ReceiverChooseActivity.this);
													SendMessage.setProgressStyle(ProgressDialog.STYLE_SPINNER);
													SendMessage.setMessage("等待向服务器发送参数");
													SendMessage.setCancelable(false);
													SendMessage.show();
													// 配置handler
													final Handler sendhandler = new Handler() {  
														@Override
														public void handleMessage(Message msg) {
															super.handleMessage(msg);
															if(msg.what == 0){
																SendMessage.dismiss();
																// 跳转至下一个activity
															    Intent _intent = new Intent(ReceiverChooseActivity.this, 
															    		LocationResultActivity.class);
															    startActivity(_intent);
																}
														}     
													}; 
													// 调用Communicator类
													Communicator Receiver_cator = new Communicator(sendhandler);
													Receiver_cator.sendInstruction(GV, FRAME_TYPE.CHOSEN_RECEIVER);
												}
											})
											.setNegativeButton("取消", null)
											.create();
											ReAffirmDialog.show();
										}
							    	})
							    	.setNegativeButton("重新配置时间", new DialogInterface.OnClickListener(){
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											// 回到LocationParameterActivity，重新配置定位时间
											Intent _intent = new Intent(ReceiverChooseActivity.this, LocationParameterActivity.class);
											startActivity(_intent);
										}
							    	})
							    	.setNeutralButton("取消", null)
							    	.create();
							    	InvalidTrigTime.show();
							    }
							    else{
							    	// 向服务器发送定位参数数据
									final ProgressDialog SendMessage = new ProgressDialog(ReceiverChooseActivity.this);
									SendMessage.setProgressStyle(ProgressDialog.STYLE_SPINNER);
									SendMessage.setMessage("等待向服务器发送参数");
									SendMessage.setCancelable(false);
									SendMessage.show();
									// 配置handler
									final Handler sendhandler = new Handler() {  
										@SuppressLint("HandlerLeak")
										@Override
										public void handleMessage(Message msg) {
											super.handleMessage(msg);
											if(msg.what == 0){
												SendMessage.dismiss();
												// 跳转至下一个activity
											    Intent _intent = new Intent(ReceiverChooseActivity.this, 
											    		LocationResultActivity.class);
											    startActivity(_intent);
												}
										}     
									}; 
									// 调用Communicator类
									Communicator Receiver_cator = new Communicator(sendhandler);
									Receiver_cator.sendInstruction(GV, FRAME_TYPE.CHOSEN_RECEIVER);
							    }
							    
							}
							else{
								// 能量触发模式
								// 向服务器发送定位参数数据
								final ProgressDialog SendMessage = new ProgressDialog(ReceiverChooseActivity.this);
								SendMessage.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								SendMessage.setMessage("等待向服务器发送参数");
								SendMessage.setCancelable(false);
								SendMessage.show();
								// 配置handler
								final Handler sendhandler = new Handler() {  
									@SuppressLint("HandlerLeak")
									@Override
									public void handleMessage(Message msg) {
										super.handleMessage(msg);
										if(msg.what == 0){
											SendMessage.dismiss();
											// 跳转至下一个activity
										    Intent _intent = new Intent(ReceiverChooseActivity.this, 
										    		LocationResultActivity.class);
										    startActivity(_intent);
											}
									}     
								}; 
								// 调用Communicator类
								Communicator Recei_cator = new Communicator(sendhandler);
								Recei_cator.sendInstruction(GV, FRAME_TYPE.CHOSEN_RECEIVER);
							}
						}
					}).create();
					AffirmDialog.show();
				}
			}
		});
	}
	
	// 产生确认字符串
	public String generateConfirmString(User SysUser){
		String show = new String();
		TunerWorkParameter ShowParameter = SysUser.getWorkGroup().getParameter();
		LocationRegion ShowRegion = ShowParameter.getLocationRegion();
		
		show = "监控区域类型： ";
		if(ShowRegion.getRegionMode() == 0){
			// 矩形定位区域
			show = show + "矩形\n左上角点坐标： \n经度:" + ShowRegion.getRegionValue1()
					+"° \n纬度:" + ShowRegion.getRegionValue2() + 
					"°\n右下角点坐标： \n经度:" + ShowRegion.getRegionValue3() + 
					"° \n纬度:" + ShowRegion.getRegionValue4() + "°\n";
		}
		else{
			// 圆形定位区域
			show = show + "圆形\n圆心坐标： \n经度：" + ShowRegion.getRegionValue1()
					+"° \n纬度:" + ShowRegion.getRegionValue2() +
					"°\n半径：" + ShowRegion.getRegionValue3() + "m\n";
		}
		show = show + "中心频率： " + ShowParameter.getCenterFreq() + "Hz\n"
				+ "带宽： " + ShowParameter.getBandWidth() + "Hz\n"
				+ "样本数： " + ShowParameter.getIQNum() + "\n"
				+ "手动增益参数： " + ShowParameter.getMGC() + "dB\n";
		if(ShowParameter.getTrigMode() == 0){
			// 时间触发
			show = show + "触发时间： " + ShowParameter.getTrigTime().getYear() + "/" + 
					ShowParameter.getTrigTime().getMonth() + "/" + ShowParameter.getTrigTime().getDay() + "/ " + 
					ShowParameter.getTrigTime().getHour() + ":" + ShowParameter.getTrigTime().getMinute() + ":00" + "\n";
		}
		else{
			// 能量触发
			show = show + "触发电平： " + ShowParameter.getTrigPower() + "dBμV\n";
		}
		show = show + "选择接收机数目： " + SysUser.getWorkGroup().getTunerGroup().size() + 
				"\n已选接收机编号：\n";
		for(Tuner tuner:SysUser.getWorkGroup().getTunerGroup()){
			show = show + "接收机名称：接收机 " + tuner.getTunerID() + "\n";
		}
		return show;
	}
	
	public void addReceiver2Map(GlobalVariable GV, BaiduMap mBaidumap){
		int i  = 0;
		receiverpos = new LatLng[GV.AvailableTuner.size()];
		for(Tuner tuner : GV.AvailableTuner){
			i++;
			// 读取接收机的坐标位置
			LatLng point = new LatLng(tuner.getPosition().getLatitude(), tuner.getPosition().getLongitude());
			receiverpos[i-1] = point;
			// 设置显示覆盖物view
			ReceiverView receiView = new ReceiverView(ReceiverChooseActivity.this, R.drawable.map_receiver);
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(receiView.
					getBitmapFromView(GV.MAP_FONT_COLOR_UNCHOSEN, GV.MAP_FONT_SIZE, "接收机"+tuner.getTunerID()));
			//构建MarkerOption，用于在地图上添加Marker  
			OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);  
			//在地图上添加Marker，并显示  
			mBaidumap.addOverlay(option);
		}
	}
	
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {  
        	Intent _intent = new Intent(ReceiverChooseActivity.this, LocationParameterActivity.class);
			startActivity(_intent);
        }
        return false;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.receiver_choose, menu);
		return true;
	}
	
	@Override  
	 public boolean onOptionsItemSelected(MenuItem item) {  
		 switch(item.getItemId()){
		 case R.id.action_settings:
			 Intent _intent = new Intent(ReceiverChooseActivity.this, SettingActivity.class);
			 startActivity(_intent);
			 return true;
		 default:
			 break;
		 }
		return false;
	 }
	
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        mMapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();  
    }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mMapView.onPause();  
    }

}
