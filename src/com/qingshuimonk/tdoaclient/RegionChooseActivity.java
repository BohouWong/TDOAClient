package com.qingshuimonk.tdoaclient;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.qingshuimonk.tdoaclient.data_structrue.LocationRegion;
import com.qingshuimonk.tdoaclient.data_structrue.Tuner;
import com.qingshuimonk.tdoaclient.data_structrue.TunerWorkGroup;
import com.qingshuimonk.tdoaclient.data_structrue.TunerWorkParameter;
import com.qingshuimonk.tdoaclient.utils.Communicator;
import com.qingshuimonk.tdoaclient.utils.ReceiverView;
import com.qingshuimonk.tdoaclient.utils.SysApplication;
import com.qingshuimonk.tdoaclient.utils.FrameFormer.FRAME_TYPE;


/***
 * 本activity用于定义区域选择界面
 * 配套xml文件: activity_location_result.xml
 * 功能:		
 * 1.在百度地图上显示用于定位区域坐标点；
 * 2.在百度地图上显示接收机位置；
 * 3.向服务器发送定位区域选择信息；
 * @author Huang Bohao
 * @version 1.2.0
 * @since 2014.11.11
 *
  * 01/13/2015 1.1.0 修改说明：
 * 添加了结果地图显示功能
 * 
 * 01/17/2015 1.2.0 修改说明：
 * 在结果地图显示中运用新的view，显示图文覆盖物
 */
public class RegionChooseActivity extends Activity{
	
	MapView mMapView = null;  
	int regionFlag = 0;
	LatLng lastpoint = null;
	Double[] Location1 = new Double[2];
	Double[] Location2 = new Double[2]; 
	
	// 用于存储接收机的经纬度坐标
	LatLng[] receiverpos = null;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.activity_region_choose);
		
		// 将此activity添加到SysApplication类中
		SysApplication.getInstance().addActivity(this);
		
		// 获取GlobalVariable类的全局变量
		final GlobalVariable GV = (GlobalVariable)getApplicationContext(); 
		
		// 定义百度地图
		mMapView = (MapView) findViewById(R.id.bmapView);  
		final BaiduMap mBaidumap = mMapView.getMap();
		
		// 隐藏百度地图logo和放大缩小控件
		int count = mMapView.getChildCount();
		for (int i = 0; i < count; i++) {
			View child = mMapView.getChildAt(i);
			if (child instanceof ImageView || child instanceof ZoomControls)
				child.setVisibility(View.INVISIBLE);
		}
		
		// 控件
		final TextView firstPointLocation = (TextView)findViewById(R.id.firstPoint);
		final TextView secondPointLocation = (TextView)findViewById(R.id.secondPoint);
		final Button Confirm = (Button)findViewById(R.id.confirm);
		final Button Next = (Button)findViewById(R.id.next);
		final Button Cancel = (Button)findViewById(R.id.cancel);
		
		// 创建action bar
		ActionBar LoginActionBar = this.getActionBar();
		LoginActionBar.setDisplayShowTitleEnabled(true);	// ActionBar只显示title
		LoginActionBar.setDisplayShowHomeEnabled(false);
		
		// 向服务器发送数据请求
		final ProgressDialog SendMessage = new ProgressDialog(RegionChooseActivity.this);
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
		// 调用Communicator类
		Communicator DataRequest_cator = new Communicator(sendhandler);
		DataRequest_cator.sendDataRequest(GV, FRAME_TYPE.DATA_REQUEST, (byte) 0x01);
		
		if(GV.DEBUG_UDP_CONNECTION){
			// 有wifi连接
			// 获取接收机数据
			final ProgressDialog ReceiveMessage = new ProgressDialog(
					RegionChooseActivity.this);
			ReceiveMessage.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			ReceiveMessage.setMessage("等待从服务器接收参数");
			ReceiveMessage.setCancelable(false);
			ReceiveMessage.setButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				AlertDialog CancelReaffirmDialog = new AlertDialog.Builder(RegionChooseActivity.this)
					.setIcon(android.R.drawable.ic_dialog_alert).setTitle("取消接收信息:").setMessage("是否确认取消本次接收？")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 取消定位，跳转至登录界面
							Intent _intent = new Intent(RegionChooseActivity.this, LoginActivity.class);
							startActivity(_intent);
						}
					}).setNegativeButton("取消", null).create();
				CancelReaffirmDialog.show();
				}
			});
			ReceiveMessage.show();
			// 配置handler
			final Handler receivehandler = new Handler() {
				@SuppressLint("HandlerLeak")
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					if (msg.what != 0) {
						// 向百度地图添加工作接收机
						addReceiver2Map(GV, mBaidumap);
						
						LatLngBounds.Builder boundsbuilder = new LatLngBounds.Builder();
						for(int i = 0; i < GV.AvailableTuner.size(); i ++){
							boundsbuilder.include(receiverpos[i]);
						}
						LatLngBounds bounds =  boundsbuilder.build();
						MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
						mBaidumap.animateMapStatus(u, 1000);
						ReceiveMessage.dismiss();
					}
				}
			};
			Communicator Receiver_cator = new Communicator(receivehandler);
			Receiver_cator.receiveData(GV, FRAME_TYPE.AVAILABLE_RECEIVER_MAP);
		}
		else{
			// 无wifi连接
			// 获取当前位置
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			final LocationListener locationListener = new LocationListener() {
			    public void onLocationChanged(Location location) { 
			    	// 当前地理坐标改变时触发
			    }

			    public void onProviderDisabled(String provider) {
			    	// 当当前位置提供器失效时触发，例如关闭GPS
			    }

			    public void onProviderEnabled(String provider) {
			    	// 当当前位置提供器有效时触发，例如打开GPS
			    }

			    public void onStatusChanged(String provider, int status, Bundle extras) {
			    	// 当当前位置提供器改变时触发
			    }
			};
			
			// 开始获取定位信息
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,6000, 1, locationListener);
			Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			double latitude, longitude;
			for(int i = 10000; i > 0; i--){
				location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
			if(location == null){
				latitude = 30.7600400000;
				longitude = 103.9411360000;
			}
			else{
				latitude = location.getLatitude();     	// 设置当前位置经度
				longitude = location.getLongitude(); 	// 设置当前位置纬度
			}
			
			// 设置中心点
			LatLng cenpt = new LatLng(latitude,longitude); 
			locationManager.removeUpdates(locationListener);
			
			// 定义地图状态
			MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(20).build();
			final MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
			mBaidumap.setMapStatus(mMapStatusUpdate);
		}
		
		//添加定位位置
		OnMapLongClickListener listener = new OnMapLongClickListener() {  
		    /***
		    * 地图长按事件监听
		    * @param point 长按处地理坐标
		    */  
		    public void onMapLongClick(LatLng point){  
		    	regionFlag++;
		    	OverlayOptions option1 = new MarkerOptions();
		    	OverlayOptions option2 = new MarkerOptions();
		    	
		    	if(regionFlag == 1){
		    		// 添加地图覆盖物
			    	BitmapDescriptor bitmap = BitmapDescriptorFactory  
			    	    .fromResource(R.drawable.icon_marka);  
			    	option1 = new MarkerOptions()  
			    	    .position(point)  
			    	    .icon(bitmap);
			    	mBaidumap.addOverlay(option1);
			    	
			    	// 存储位置数据
			    	Location1[0] = point.latitude;
			    	Location1[1] = point.longitude;
			    	firstPointLocation.setText("第一点坐标:纬度"+Location1[0]+"° 经度:"+Location1[1]);
			    	
			    	lastpoint = point;
		    	}
		    	if(regionFlag == 2){
		    		// 添加地图覆盖物
			    	BitmapDescriptor bitmap = BitmapDescriptorFactory  
			    	    .fromResource(R.drawable.icon_markb);  
			    	option2 = new MarkerOptions()  
			    	    .position(point)  
			    	    .icon(bitmap);
			    	mBaidumap.addOverlay(option2);
			    	
			    	// 存储位置数据
			    	Location2[0] = point.latitude;
			    	Location2[1] = point.longitude;
			    	secondPointLocation.setText("第二点坐标:纬度"+Location2[0]+"° 经度:"+Location2[1]);
			    	
			    	lastpoint = point;
		    	}
		    	if(regionFlag == 3){
		    		// 清空已有地图覆盖物
		    		mBaidumap.clear();
		    		addReceiver2Map(GV, mBaidumap);		// 添加接收机位置
		    		// 添加地图覆盖物  
			    	BitmapDescriptor bitmap = BitmapDescriptorFactory  
			    	    .fromResource(R.drawable.icon_markb);  
			    	option1 = new MarkerOptions()  
			    	    .position(lastpoint)  
			    	    .icon(bitmap);  
			    	mBaidumap.addOverlay(option1);
			    	// 存储位置数据
			    	Location1[0] = point.latitude;
			    	Location1[1] = point.longitude;
			    	firstPointLocation.setText("第一点坐标:纬度"+Location1[0]+"° 经度:"+Location1[1]);
		    		
			    	// 添加地图覆盖物
			    	BitmapDescriptor bitmap2 = BitmapDescriptorFactory  
			    	    .fromResource(R.drawable.icon_marka);  
			    	option2 = new MarkerOptions()  
			    	    .position(point)  
			    	    .icon(bitmap2);
			    	mBaidumap.addOverlay(option2);
			    	lastpoint = point;
		    	}
		    	if(regionFlag == 4){
		    		// 清空已有地图覆盖物
		    		mBaidumap.clear();
		    		addReceiver2Map(GV, mBaidumap);		// 添加接收机位置
		    		// 添加地图覆盖物  
			    	BitmapDescriptor bitmap = BitmapDescriptorFactory  
			    	    .fromResource(R.drawable.icon_markb);  
			    	option1 = new MarkerOptions()  
			    	    .position(point)  
			    	    .icon(bitmap);
			    	mBaidumap.addOverlay(option1);
		    		
			    	// 添加地图覆盖物  
			    	BitmapDescriptor bitmap2 = BitmapDescriptorFactory  
			    	    .fromResource(R.drawable.icon_marka);  
			    	option2 = new MarkerOptions()  
			    	    .position(lastpoint)  
			    	    .icon(bitmap2);
			    	mBaidumap.addOverlay(option2);
			    	// 存储位置数据
			    	Location2[0] = point.latitude;
			    	Location2[1] = point.longitude;
			    	secondPointLocation.setText("第二点坐标:纬度"+Location2[0]+"° 经度:"+Location2[1]);
			    	lastpoint = point;
		    		regionFlag = 2;
		    	}
		    }  
		};
		
		mBaidumap.setOnMapLongClickListener(listener);
		
		Confirm.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				try{
					// 添加矩形覆盖物
					LatLng pt1 = new LatLng(Location1[0], Location1[1]);  
					LatLng pt2 = new LatLng(Location1[0], Location2[1]);  
					LatLng pt3 = new LatLng(Location2[0], Location2[1]);  
					LatLng pt4 = new LatLng(Location2[0], Location1[1]);  
					List<LatLng> pts = new ArrayList<LatLng>();  
					pts.add(pt1);  
					pts.add(pt2);  
					pts.add(pt3);  
					pts.add(pt4);   
					OverlayOptions polygonOption = new PolygonOptions()  
					    .points(pts)  
					    .stroke(new Stroke(5, 0xAA00FF00))  
					    .fillColor(0xAAFFFF00);  
					mBaidumap.addOverlay(polygonOption);
				}catch(Exception e){
					Dialog NoPointSelected = new AlertDialog.Builder(RegionChooseActivity.this)
					.setIcon(android.R.drawable.ic_dialog_alert).setTitle("请先选择坐标点：")
					.setMessage("长按以添加标记确定定位范围")
					.setPositiveButton("确定", null).setNegativeButton("取消", null)
					.create();
					NoPointSelected.show();
				}
			}
		});
		
		// 清空已选择定位区域信息
		Cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// 清空并重设数据
				mBaidumap.clear();
				addReceiver2Map(GV, mBaidumap);
				regionFlag = 0;
				Location1 = new Double[2];
				Location2 = new Double[2];
				firstPointLocation.setText("第一点坐标:");
				secondPointLocation.setText("第二点坐标:");
			}
		});
		
		// “下一步”按键监听函数
		Next.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				try{
					LocationRegion Region = new LocationRegion((byte) 0,Location1[1],Location1[0],
							Location2[1],Location2[0]);
					TunerWorkParameter Parameter = new TunerWorkParameter(Region);
					TunerWorkGroup Group = new TunerWorkGroup(Parameter);
					GV.SysUser.setWorkGroup(Group);
					
					Dialog PointSelected = new AlertDialog.Builder(RegionChooseActivity.this)
					.setIcon(android.R.drawable.ic_dialog_alert).setTitle("确认坐标点:")
					.setMessage("第一点:纬度:"+Region.getRegionValue1()+" 经度:"+Region.getRegionValue2()
							+"\n第二点:纬度:"+Region.getRegionValue3()+" 经度:"+Region.getRegionValue4())
					.setPositiveButton("确定", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 跳转至下一个activity
							Intent _intent = new Intent(RegionChooseActivity.this, LocationParameterActivity.class);
							startActivity(_intent);
						}
					})
					.setNegativeButton("取消", null)
					.create();
					PointSelected.show();
				}catch(Exception e){
					Dialog NoPointSelected = new AlertDialog.Builder(RegionChooseActivity.this)
					.setIcon(android.R.drawable.ic_dialog_alert).setTitle("请先选择坐标点：")
					.setMessage("长按以添加标记确定定位范围")
					.setPositiveButton("确定", null).setNegativeButton("取消", null)
					.create();
					NoPointSelected.show();
				}
			}
		});
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
			ReceiverView receiView = new ReceiverView(RegionChooseActivity.this, R.drawable.map_receiver);
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(receiView.
					getBitmapFromView(GV.MAP_FONT_COLOR_UNCHOSEN, GV.MAP_FONT_SIZE, "接收机"+tuner.getTunerID()));
			//构建MarkerOption，用于在地图上添加Marker  
			OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);  
			//在地图上添加Marker，并显示  
			mBaidumap.addOverlay(option);
		}
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
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mMapView.onPause();  
        }  
    
    @Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {  
        	Intent _intent = new Intent(RegionChooseActivity.this, LoginActivity.class);
			startActivity(_intent);
        }
        return false;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.region_choose, menu);
		return true;
	}
	
	@Override  
	 public boolean onOptionsItemSelected(MenuItem item) {  
		 switch(item.getItemId()){
		 case R.id.action_settings:
			 Intent _intent = new Intent(RegionChooseActivity.this, SettingActivity.class);
			 startActivity(_intent);
			 return true;
		 default:
			 break;
		 }
		return false;
	 }

}
