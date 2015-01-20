package com.qingshuimonk.tdoaclient;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Calendar;

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
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
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
import com.qingshuimonk.tdoaclient.utils.FrameFormer.FRAME_TYPE;

/***
 * This is an android activity file:
 * function:	1.Show user available receivers and their working status;
 * 				2.Guide user to finish receiver selecting;
 * 				3.Transmit Location parameters to the client;
 * @author Huang Bohao
 * @version 1.0.0
 * @since 2014.11.18
 *
 */
public class ReceiverChooseActivity extends Activity{
	
	// create ArrayList and ArrayAdapter
	ArrayList<Tuner> receiveritem = new ArrayList<Tuner>();
	ArrayList<Tuner> AvailableTunerGroup = new ArrayList<Tuner>();
	ReceiverAdapter adapter;
	
	// Baidu map
	MapView mMapView = null;  
	BaiduMap ReceiverBaiduMap = null;
	LatLng[] receiverpos = null;
	
	// initial the string which is used to present confirm message
	String show = new String();
	
	final int MAX_DATA_PACKET_LENGTH = 40;  
    byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];  
    DatagramSocket udpSocket;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.activity_receiver_choose);
		
		// add activity to list
		SysApplication.getInstance().addActivity(this);
		
		// Used to access activity-level global variable
		final GlobalVariable GV = (GlobalVariable)getApplicationContext(); 
		
		// create action bar
		ActionBar LoginActionBar = this.getActionBar();
		LoginActionBar.setDisplayShowTitleEnabled(true); // ActionBar shows title only
		LoginActionBar.setDisplayShowHomeEnabled(false);
		
		final ListView receiverList = (ListView)findViewById(R.id.receiverlist);
		final Button ReceiverChooseNext = (Button)findViewById(R.id.receiverchoosenext);
		
		// bind ArrayList and ArrayAdapter
		adapter = new ReceiverAdapter(this, R.layout.item_receiver, receiveritem);
		receiverList.setAdapter(adapter);
		
		// get the reference of baidu map  
        mMapView = (MapView) findViewById(R.id.receiverchoose_Map);  
        ReceiverBaiduMap = mMapView.getMap();
		
		if(GV.DEBUG_UDP_CONNECTION){
			// send data request
			final ProgressDialog SendMessage = new ProgressDialog(ReceiverChooseActivity.this);
			SendMessage.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			SendMessage.setMessage("等待向服务器发送参数");
			SendMessage.setCancelable(false);
			SendMessage.show();
			// set handler
			final Handler sendhandler = new Handler() {  
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					if(msg.what == 0){
						SendMessage.dismiss();
						}
				}     
			}; 
			Communicator DataRequest_cator = new Communicator(sendhandler);
			DataRequest_cator.sendDataRequest(GV, FRAME_TYPE.DATA_REQUEST, (byte) 0x02);
			
			// get receivers information
			final ProgressDialog ReceiveMessage = new ProgressDialog(
					ReceiverChooseActivity.this);
			ReceiveMessage.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			ReceiveMessage.setMessage("等待从服务器接收参数");
			ReceiveMessage.setCancelable(false);
			ReceiveMessage.setButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
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
											// TODO Auto-generated method stub
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
							
							// add receiver to map
							addReceiver2Map(GV, ReceiverBaiduMap);
							
							// hide baidu logo ZoomControl
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
			
			Communicator Receiver_cator = new Communicator(receivehandler);
			Receiver_cator.receiveData(GV, FRAME_TYPE.AVAILABLE_RECEIVER);
		}
		else{
			// create virtual receiver data
			// for no wifi debug only
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
			
			// add Tuner to ArrayList
			AvailableTunerGroup.add(receiverTuner1);
			AvailableTunerGroup.add(receiverTuner2);
			AvailableTunerGroup.add(receiverTuner3);
			AvailableTunerGroup.add(receiverTuner4);
			AvailableTunerGroup.add(receiverTuner5);
		}
		
		ReceiverChooseNext.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				byte ReceiverNum;
				final ArrayList<Integer> ChooseResult = adapter.getChooseResult();
				ReceiverNum = adapter.getReceiverNum();
				
				if((ReceiverNum > GV.MAX_REC_NUM)||(ReceiverNum < GV.MIN_REC_NUM)){
					Toast.makeText(getApplicationContext(), "选择无效\n接收机选择数目应为"+GV.MIN_REC_NUM+"~"+GV.MAX_REC_NUM
							+"个！", Toast.LENGTH_SHORT).show();
				}
				else{
					// the choose of receiver is complete and legal
					// store choose result
					TunerWorkGroup Group = GV.SysUser.getWorkGroup();
					Group.setTunerGroup(AvailableTunerGroup, ChooseResult);
					
					// generate confirm string
					show = generateConfirmString(GV.SysUser);
					
					// show confirm dialog
					Dialog AffirmDialog = new AlertDialog.Builder(ReceiverChooseActivity.this)
					.setIcon(android.R.drawable.ic_dialog_info).setTitle("请确认配置参数:")
					.setMessage(show)
					.setNegativeButton("取消", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					})
					.setPositiveButton("确定", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							// judge if there is enough time for sever to deploy the location mission
							// 3 minutes is needed
							final TunerWorkParameter parameter = GV.SysUser.getWorkGroup().getParameter();
							final DateTime trigtime = parameter.getTrigTime();
							if(parameter.getTrigMode() == 0){
								// re-confirm time
								// get current system time
								int year, month, day, hour ,minute;
								final Calendar c = Calendar.getInstance();
								year = c.get(Calendar.YEAR);
							    month = c.get(Calendar.MONTH)+1;  
							    day = c.get(Calendar.DAY_OF_MONTH);
							    hour = c.get(Calendar.HOUR_OF_DAY);  
							    minute = c.get(Calendar.MINUTE);
							    // get trig time
							    int setYear, setMonth, setDay, setHour, setMinute;
							    setYear = trigtime.getYear();
							    setMonth = trigtime.getMonth();
							    setDay = trigtime.getDay();
							    setHour = trigtime.getHour();
							    setMinute = trigtime.getMinute();
							    // get time difference
							    long currentTime, setTime;
							    currentTime = 
							    		year*100000000L + month*1000000L + day*10000 + hour*100 + minute;
							    setTime = 
							    		setYear*100000000L + setMonth*1000000L + setDay*10000 + setHour*100 + setMinute;
							    // time is less than 3 minutes
							    if(currentTime+ GV.TIME_FOR_SERVER > setTime){
							    	Dialog InvalidTrigTime = new AlertDialog.Builder(ReceiverChooseActivity.this)
							    	.setIcon(android.R.drawable.ic_dialog_alert).setTitle("无效触发时间")
							    	.setMessage("服务器所剩配置接收机时间不足"+GV.TIME_FOR_SERVER+"分钟，是否自动延长触发时间？")
							    	.setPositiveButton("确定", new DialogInterface.OnClickListener(){
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											trigtime.setMinute((short) (c.get(Calendar.MINUTE)+GV.TIME_FOR_SERVER));
											
											// generate confirm string
											show = generateConfirmString(GV.SysUser);
											
											// show confirm dialog
											Dialog ReAffirmDialog = new AlertDialog.Builder(ReceiverChooseActivity.this)
											.setIcon(android.R.drawable.ic_dialog_info).setTitle("请确认配置参数:")
											.setMessage(show)
											.setPositiveButton("确定", new DialogInterface.OnClickListener(){
												@Override
												public void onClick(DialogInterface dialog,int which) {
													// TODO Auto-generated method stub
													// send location parameter to server
													final ProgressDialog SendMessage = new ProgressDialog(ReceiverChooseActivity.this);
													SendMessage.setProgressStyle(ProgressDialog.STYLE_SPINNER);
													SendMessage.setMessage("等待向服务器发送参数");
													SendMessage.setCancelable(false);
													SendMessage.show();
													// set handler
													final Handler sendhandler = new Handler() {  
														@Override
														public void handleMessage(Message msg) {
															super.handleMessage(msg);
															if(msg.what == 0){
																SendMessage.dismiss();
																// jump to next activity
															    Intent _intent = new Intent(ReceiverChooseActivity.this, 
															    		LocationResultActivity.class);
															    startActivity(_intent);
																}
														}     
													}; 
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
											// TODO Auto-generated method stub
											Intent _intent = new Intent(ReceiverChooseActivity.this, LocationParameterActivity.class);
											startActivity(_intent);
										}
							    	})
							    	.setNeutralButton("取消", null)
							    	.create();
							    	InvalidTrigTime.show();
							    }
							    else{
							    	// send location parameter to server
									final ProgressDialog SendMessage = new ProgressDialog(ReceiverChooseActivity.this);
									SendMessage.setProgressStyle(ProgressDialog.STYLE_SPINNER);
									SendMessage.setMessage("等待向服务器发送参数");
									SendMessage.setCancelable(false);
									SendMessage.show();
									// set handler
									final Handler sendhandler = new Handler() {  
										@Override
										public void handleMessage(Message msg) {
											super.handleMessage(msg);
											if(msg.what == 0){
												SendMessage.dismiss();
												// jump to next activity
											    Intent _intent = new Intent(ReceiverChooseActivity.this, 
											    		LocationResultActivity.class);
											    startActivity(_intent);
												}
										}     
									}; 
									Communicator Receiver_cator = new Communicator(sendhandler);
									Receiver_cator.sendInstruction(GV, FRAME_TYPE.CHOSEN_RECEIVER);
							    }
							    
							}
							else{
								// power trigger mode
								// send location parameter to server
								final ProgressDialog SendMessage = new ProgressDialog(ReceiverChooseActivity.this);
								SendMessage.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								SendMessage.setMessage("等待向服务器发送参数");
								SendMessage.setCancelable(false);
								SendMessage.show();
								// set handler
								final Handler sendhandler = new Handler() {  
									@Override
									public void handleMessage(Message msg) {
										super.handleMessage(msg);
										if(msg.what == 0){
											SendMessage.dismiss();
											// jump to next activity
										    Intent _intent = new Intent(ReceiverChooseActivity.this, 
										    		LocationResultActivity.class);
										    startActivity(_intent);
											}
									}     
								}; 
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
	
	public String generateConfirmString(User SysUser){
		// generate confirm string
		String show = new String();
		TunerWorkParameter ShowParameter = SysUser.getWorkGroup().getParameter();
		LocationRegion ShowRegion = ShowParameter.getLocationRegion();
		
		show = "监控区域类型： ";
		if(ShowRegion.getRegionMode() == 0){
			// rectangle input
			show = show + "矩形\n左上角点坐标： \n经度:" + ShowRegion.getRegionValue1()
					+"° \n纬度:" + ShowRegion.getRegionValue2() + 
					"°\n右下角点坐标： \n经度:" + ShowRegion.getRegionValue3() + 
					"° \n纬度:" + ShowRegion.getRegionValue4() + "°\n";
		}
		else{
			// circular input
			show = show + "圆形\n圆心坐标： \n经度：" + ShowRegion.getRegionValue1()
					+"° \n纬度:" + ShowRegion.getRegionValue2() +
					"°\n半径：" + ShowRegion.getRegionValue3() + "m\n";
		}
		show = show + "中心频率： " + ShowParameter.getCenterFreq() + "Hz\n"
				+ "带宽： " + ShowParameter.getBandWidth() + "Hz\n"
				+ "样本数： " + ShowParameter.getIQNum() + "\n"
				+ "手动增益参数： " + ShowParameter.getMGC() + "dB\n";
		if(ShowParameter.getTrigMode() == 0){
			// time trigger
			show = show + "触发时间： " + ShowParameter.getTrigTime().getYear() + "/" + 
					ShowParameter.getTrigTime().getMonth() + "/" + ShowParameter.getTrigTime().getDay() + "/ " + 
					ShowParameter.getTrigTime().getHour() + ":" + ShowParameter.getTrigTime().getMinute() + ":00" + "\n";
		}
		else{
			// power trigger
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
		 // TODO Auto-generated method stub  
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
