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
 * ��activity���ڶ�������ѡ�����
 * ����xml�ļ�: activity_location_result.xml
 * ����:		
 * 1.�ڰٶȵ�ͼ����ʾ���ڶ�λ��������㣻
 * 2.�ڰٶȵ�ͼ����ʾ���ջ�λ�ã�
 * 3.����������Ͷ�λ����ѡ����Ϣ��
 * @author Huang Bohao
 * @version 1.2.0
 * @since 2014.11.11
 *
  * 01/13/2015 1.1.0 �޸�˵����
 * ����˽����ͼ��ʾ����
 * 
 * 01/17/2015 1.2.0 �޸�˵����
 * �ڽ����ͼ��ʾ�������µ�view����ʾͼ�ĸ�����
 */
public class RegionChooseActivity extends Activity{
	
	MapView mMapView = null;  
	int regionFlag = 0;
	LatLng lastpoint = null;
	Double[] Location1 = new Double[2];
	Double[] Location2 = new Double[2]; 
	
	// ���ڴ洢���ջ��ľ�γ������
	LatLng[] receiverpos = null;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.activity_region_choose);
		
		// ����activity��ӵ�SysApplication����
		SysApplication.getInstance().addActivity(this);
		
		// ��ȡGlobalVariable���ȫ�ֱ���
		final GlobalVariable GV = (GlobalVariable)getApplicationContext(); 
		
		// ����ٶȵ�ͼ
		mMapView = (MapView) findViewById(R.id.bmapView);  
		final BaiduMap mBaidumap = mMapView.getMap();
		
		// ���ذٶȵ�ͼlogo�ͷŴ���С�ؼ�
		int count = mMapView.getChildCount();
		for (int i = 0; i < count; i++) {
			View child = mMapView.getChildAt(i);
			if (child instanceof ImageView || child instanceof ZoomControls)
				child.setVisibility(View.INVISIBLE);
		}
		
		// �ؼ�
		final TextView firstPointLocation = (TextView)findViewById(R.id.firstPoint);
		final TextView secondPointLocation = (TextView)findViewById(R.id.secondPoint);
		final Button Confirm = (Button)findViewById(R.id.confirm);
		final Button Next = (Button)findViewById(R.id.next);
		final Button Cancel = (Button)findViewById(R.id.cancel);
		
		// ����action bar
		ActionBar LoginActionBar = this.getActionBar();
		LoginActionBar.setDisplayShowTitleEnabled(true);	// ActionBarֻ��ʾtitle
		LoginActionBar.setDisplayShowHomeEnabled(false);
		
		// �������������������
		final ProgressDialog SendMessage = new ProgressDialog(RegionChooseActivity.this);
		SendMessage.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		SendMessage.setMessage("�ȴ�����������Ͳ���");
		SendMessage.setCancelable(false);
		SendMessage.show();
		// ����handler
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
		// ����Communicator��
		Communicator DataRequest_cator = new Communicator(sendhandler);
		DataRequest_cator.sendDataRequest(GV, FRAME_TYPE.DATA_REQUEST, (byte) 0x01);
		
		if(GV.DEBUG_UDP_CONNECTION){
			// ��wifi����
			// ��ȡ���ջ�����
			final ProgressDialog ReceiveMessage = new ProgressDialog(
					RegionChooseActivity.this);
			ReceiveMessage.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			ReceiveMessage.setMessage("�ȴ��ӷ��������ղ���");
			ReceiveMessage.setCancelable(false);
			ReceiveMessage.setButton("ȡ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				AlertDialog CancelReaffirmDialog = new AlertDialog.Builder(RegionChooseActivity.this)
					.setIcon(android.R.drawable.ic_dialog_alert).setTitle("ȡ��������Ϣ:").setMessage("�Ƿ�ȷ��ȡ�����ν��գ�")
					.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// ȡ����λ����ת����¼����
							Intent _intent = new Intent(RegionChooseActivity.this, LoginActivity.class);
							startActivity(_intent);
						}
					}).setNegativeButton("ȡ��", null).create();
				CancelReaffirmDialog.show();
				}
			});
			ReceiveMessage.show();
			// ����handler
			final Handler receivehandler = new Handler() {
				@SuppressLint("HandlerLeak")
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					if (msg.what != 0) {
						// ��ٶȵ�ͼ��ӹ������ջ�
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
			// ��wifi����
			// ��ȡ��ǰλ��
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			final LocationListener locationListener = new LocationListener() {
			    public void onLocationChanged(Location location) { 
			    	// ��ǰ��������ı�ʱ����
			    }

			    public void onProviderDisabled(String provider) {
			    	// ����ǰλ���ṩ��ʧЧʱ����������ر�GPS
			    }

			    public void onProviderEnabled(String provider) {
			    	// ����ǰλ���ṩ����Чʱ�����������GPS
			    }

			    public void onStatusChanged(String provider, int status, Bundle extras) {
			    	// ����ǰλ���ṩ���ı�ʱ����
			    }
			};
			
			// ��ʼ��ȡ��λ��Ϣ
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
				latitude = location.getLatitude();     	// ���õ�ǰλ�þ���
				longitude = location.getLongitude(); 	// ���õ�ǰλ��γ��
			}
			
			// �������ĵ�
			LatLng cenpt = new LatLng(latitude,longitude); 
			locationManager.removeUpdates(locationListener);
			
			// �����ͼ״̬
			MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(20).build();
			final MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
			mBaidumap.setMapStatus(mMapStatusUpdate);
		}
		
		//��Ӷ�λλ��
		OnMapLongClickListener listener = new OnMapLongClickListener() {  
		    /***
		    * ��ͼ�����¼�����
		    * @param point ��������������
		    */  
		    public void onMapLongClick(LatLng point){  
		    	regionFlag++;
		    	OverlayOptions option1 = new MarkerOptions();
		    	OverlayOptions option2 = new MarkerOptions();
		    	
		    	if(regionFlag == 1){
		    		// ��ӵ�ͼ������
			    	BitmapDescriptor bitmap = BitmapDescriptorFactory  
			    	    .fromResource(R.drawable.icon_marka);  
			    	option1 = new MarkerOptions()  
			    	    .position(point)  
			    	    .icon(bitmap);
			    	mBaidumap.addOverlay(option1);
			    	
			    	// �洢λ������
			    	Location1[0] = point.latitude;
			    	Location1[1] = point.longitude;
			    	firstPointLocation.setText("��һ������:γ��"+Location1[0]+"�� ����:"+Location1[1]);
			    	
			    	lastpoint = point;
		    	}
		    	if(regionFlag == 2){
		    		// ��ӵ�ͼ������
			    	BitmapDescriptor bitmap = BitmapDescriptorFactory  
			    	    .fromResource(R.drawable.icon_markb);  
			    	option2 = new MarkerOptions()  
			    	    .position(point)  
			    	    .icon(bitmap);
			    	mBaidumap.addOverlay(option2);
			    	
			    	// �洢λ������
			    	Location2[0] = point.latitude;
			    	Location2[1] = point.longitude;
			    	secondPointLocation.setText("�ڶ�������:γ��"+Location2[0]+"�� ����:"+Location2[1]);
			    	
			    	lastpoint = point;
		    	}
		    	if(regionFlag == 3){
		    		// ������е�ͼ������
		    		mBaidumap.clear();
		    		addReceiver2Map(GV, mBaidumap);		// ��ӽ��ջ�λ��
		    		// ��ӵ�ͼ������  
			    	BitmapDescriptor bitmap = BitmapDescriptorFactory  
			    	    .fromResource(R.drawable.icon_markb);  
			    	option1 = new MarkerOptions()  
			    	    .position(lastpoint)  
			    	    .icon(bitmap);  
			    	mBaidumap.addOverlay(option1);
			    	// �洢λ������
			    	Location1[0] = point.latitude;
			    	Location1[1] = point.longitude;
			    	firstPointLocation.setText("��һ������:γ��"+Location1[0]+"�� ����:"+Location1[1]);
		    		
			    	// ��ӵ�ͼ������
			    	BitmapDescriptor bitmap2 = BitmapDescriptorFactory  
			    	    .fromResource(R.drawable.icon_marka);  
			    	option2 = new MarkerOptions()  
			    	    .position(point)  
			    	    .icon(bitmap2);
			    	mBaidumap.addOverlay(option2);
			    	lastpoint = point;
		    	}
		    	if(regionFlag == 4){
		    		// ������е�ͼ������
		    		mBaidumap.clear();
		    		addReceiver2Map(GV, mBaidumap);		// ��ӽ��ջ�λ��
		    		// ��ӵ�ͼ������  
			    	BitmapDescriptor bitmap = BitmapDescriptorFactory  
			    	    .fromResource(R.drawable.icon_markb);  
			    	option1 = new MarkerOptions()  
			    	    .position(point)  
			    	    .icon(bitmap);
			    	mBaidumap.addOverlay(option1);
		    		
			    	// ��ӵ�ͼ������  
			    	BitmapDescriptor bitmap2 = BitmapDescriptorFactory  
			    	    .fromResource(R.drawable.icon_marka);  
			    	option2 = new MarkerOptions()  
			    	    .position(lastpoint)  
			    	    .icon(bitmap2);
			    	mBaidumap.addOverlay(option2);
			    	// �洢λ������
			    	Location2[0] = point.latitude;
			    	Location2[1] = point.longitude;
			    	secondPointLocation.setText("�ڶ�������:γ��"+Location2[0]+"�� ����:"+Location2[1]);
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
					// ��Ӿ��θ�����
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
					.setIcon(android.R.drawable.ic_dialog_alert).setTitle("����ѡ������㣺")
					.setMessage("��������ӱ��ȷ����λ��Χ")
					.setPositiveButton("ȷ��", null).setNegativeButton("ȡ��", null)
					.create();
					NoPointSelected.show();
				}
			}
		});
		
		// �����ѡ��λ������Ϣ
		Cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// ��ղ���������
				mBaidumap.clear();
				addReceiver2Map(GV, mBaidumap);
				regionFlag = 0;
				Location1 = new Double[2];
				Location2 = new Double[2];
				firstPointLocation.setText("��һ������:");
				secondPointLocation.setText("�ڶ�������:");
			}
		});
		
		// ����һ����������������
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
					.setIcon(android.R.drawable.ic_dialog_alert).setTitle("ȷ�������:")
					.setMessage("��һ��:γ��:"+Region.getRegionValue1()+" ����:"+Region.getRegionValue2()
							+"\n�ڶ���:γ��:"+Region.getRegionValue3()+" ����:"+Region.getRegionValue4())
					.setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// ��ת����һ��activity
							Intent _intent = new Intent(RegionChooseActivity.this, LocationParameterActivity.class);
							startActivity(_intent);
						}
					})
					.setNegativeButton("ȡ��", null)
					.create();
					PointSelected.show();
				}catch(Exception e){
					Dialog NoPointSelected = new AlertDialog.Builder(RegionChooseActivity.this)
					.setIcon(android.R.drawable.ic_dialog_alert).setTitle("����ѡ������㣺")
					.setMessage("��������ӱ��ȷ����λ��Χ")
					.setPositiveButton("ȷ��", null).setNegativeButton("ȡ��", null)
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
			// ��ȡ���ջ�������λ��
			LatLng point = new LatLng(tuner.getPosition().getLatitude(), tuner.getPosition().getLongitude());
			receiverpos[i-1] = point;
			// ������ʾ������view
			ReceiverView receiView = new ReceiverView(RegionChooseActivity.this, R.drawable.map_receiver);
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(receiView.
					getBitmapFromView(GV.MAP_FONT_COLOR_UNCHOSEN, GV.MAP_FONT_SIZE, "���ջ�"+tuner.getTunerID()));
			//����MarkerOption�������ڵ�ͼ�����Marker  
			OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);  
			//�ڵ�ͼ�����Marker������ʾ  
			mBaidumap.addOverlay(option);
		}
	}
	
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        // ��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        // ��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
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
