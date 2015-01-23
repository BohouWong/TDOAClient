package com.qingshuimonk.tdoaclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.qingshuimonk.tdoaclient.data_structrue.DateTime;
import com.qingshuimonk.tdoaclient.data_structrue.Position;
import com.qingshuimonk.tdoaclient.data_structrue.Tuner;
import com.qingshuimonk.tdoaclient.data_structrue.TunerWorkParameter;
import com.qingshuimonk.tdoaclient.utils.Communicator;
import com.qingshuimonk.tdoaclient.utils.ReceiverView;
import com.qingshuimonk.tdoaclient.utils.SysApplication;
import com.qingshuimonk.tdoaclient.utils.FrameFormer.FRAME_TYPE;
import com.qingshuimonk.tdoaclient.utils.HomeWatcher;
import com.qingshuimonk.tdoaclient.utils.HomeWatcher.OnHomePressedListener;

/***
 * ��activity���ڶ�������ʾ����
 * ����xml�ļ�: activity_location_result.xml
 * ����:		
 * 	1.���շ�������λ�����������ʾ;
 * 	2.�����û�ѡ����Ҫ�۲����ֵ��Ƶ�׵Ľ��ջ��������������͸�������;
 * @author Huang Bohao
 * @version 1.2.0
 * @since 2014.11.13
 *
 * 01/13/2015 1.1.0 �޸�˵����
 * ����˽����ͼ��ʾ����
 * 
 * 01/17/2015 1.2.0 �޸�˵����
 * �ڽ����ͼ��ʾ�������µ�view����ʾͼ�ĸ�����
 */
public class LocationResultActivity extends Activity implements OnMapLoadedCallback {
	
	// �ٶȵ�ͼ
	MapView Result_MapView = null; 
	BaiduMap Result_Baidumap = null;
	
	// �ؼ�
	TextView Banner = null;
	TextView Longitude = null;
	TextView Latitude = null;
	TextView Altitude = null;
	TextView Variance = null;
	TextView Time = null;
	TextView Correlation = null;
	TextView Spectrum = null;
	
	// ����
    long TotalTime = 0;
    java.util.Date now,set;
	LatLng[] receiverpos = null;				// ���ڴ洢���ջ��ľ�γ������
	boolean AvailableResult = false;			// ���ý����־λ
	boolean isBackGround = false;				// ��̨���б�־λ
	private HomeWatcher mHomeWatcher;			// home����������
	
	// notification֪ͨ
	private Notification notification;  
    private NotificationManager nManager;  
    private Intent intent;  
    private PendingIntent pIntent;  
    private static final int ID = 1;			// Notification�ı�ʾID  

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext()); 
		setContentView(R.layout.activity_location_result);
		
		// ����activity��ӵ�SysApplication����
		SysApplication.getInstance().addActivity(this);

		// ��ȡGlobalVariable���ȫ�ֱ���
		final GlobalVariable GV = (GlobalVariable) getApplicationContext();
		final TunerWorkParameter parameter = GV.SysUser.getWorkGroup().getParameter();

		// ����action bar
		ActionBar LoginActionBar = this.getActionBar();
		LoginActionBar.setDisplayShowTitleEnabled(true); // ActionBarֻ��ʾtitle
		LoginActionBar.setDisplayShowHomeEnabled(false);
		
		// ��ðٶȵ�ͼ�ؼ�
		Result_MapView = (MapView) findViewById(R.id.result_bmapView);  
		Result_Baidumap = Result_MapView.getMap();
		Result_Baidumap.setOnMapLoadedCallback(this);
		
		// ���������ջ���ʾ�ڵ�ͼ��
		addReceiver2Map(GV, Result_Baidumap);
		
		// ���ذٶȵ�ͼlogo�ͷŴ���С�ؼ�
		int count = Result_MapView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = Result_MapView.getChildAt(i);
            if (child instanceof ImageView || child instanceof ZoomControls)
                child.setVisibility(View.INVISIBLE);
        }
        
        // ��ȡ�ؼ�
        Banner = (TextView)findViewById(R.id.result_banner_large);
        Longitude = (TextView)findViewById(R.id.result_longtitude_value);
        Latitude = (TextView)findViewById(R.id.result_latitude_value);
        Altitude = (TextView)findViewById(R.id.result_altitude_value);
        Variance = (TextView)findViewById(R.id.result_variance_value);
        Time = (TextView)findViewById(R.id.result_time_value);
        Correlation = (TextView)findViewById(R.id.result_correlation);
        Spectrum = (TextView)findViewById(R.id.result_spectrum);
        
        // ��ʼ��notification
        String service = NOTIFICATION_SERVICE;  
        nManager = (NotificationManager) this.getSystemService(service); 
        notification = new Notification();  
        
        // ����home������
        mHomeWatcher = new HomeWatcher(this); 
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() { 
            @SuppressWarnings("deprecation")
			@Override 
            public void onHomePressed() {  
            	isBackGround = true;
                String tickerText = "ʱ�λ�ͻ�����ת���̨����"; // ֪ͨ��ʾ  
                // ��ʾʱ��  
                long when = System.currentTimeMillis();  
                notification.icon = android.R.drawable.ic_dialog_info;// ����֪ͨ��ͼ��  
                notification.tickerText = tickerText; // ��ʾ��״̬���е�����  
                notification.when = when; // ������֪ͨʱ��ʱ��  
                notification.flags = Notification.FLAG_NO_CLEAR; // ��������ťʱ�ͻ������Ϣ֪ͨ,���ǵ��֪ͨ����֪ͨʱ������ʧ
                // ����֪ͨ�����ת��NotificationResult��  
                intent = new Intent(LocationResultActivity.this, LocationResultActivity.class);  
                // ��ȡPendingIntent,���ʱ���͸�Intent  
                pIntent = PendingIntent.getActivity(LocationResultActivity.this, 0, intent, 0);  
                // ����֪ͨ�ı��������  
                notification.setLatestEventInfo(LocationResultActivity.this, "ʱ�λϵͳ���ں�̨����", "���ڵȴ����ն�λ���", pIntent);  
                // ����֪ͨ  
                nManager.notify(ID, notification); 
            } 
   
            @Override 
            public void onHomeLongPressed() { 
            } 
        }); 
        mHomeWatcher.startWatch(); 
        
        if(parameter.getTrigMode() == 0){
        	// ʱ�䴥��ģʽ
        	Longitude.setText("�ȴ�����ʱ��...");
        	Latitude.setText("�ȴ�����ʱ��...");
        	Altitude.setText("�ȴ�����ʱ��...");
        	Variance.setText("�ȴ�����ʱ��...");
        	Time.setText("�ȴ�����ʱ��...");
        	// ��ȡ����ʱ��
		    final int setYear, setMonth, setDay, setHour, setMinute;
		    setYear = parameter.getTrigTime().getYear();
		    setMonth = parameter.getTrigTime().getMonth();
		    setDay = parameter.getTrigTime().getDay();
		    setHour = parameter.getTrigTime().getHour();
		    setMinute = parameter.getTrigTime().getMinute();
		    // ת��Ϊ�ַ������ڻ��ʱ��
		    String setTime = String.format("%04d", setYear)+"-"+String.format("%02d", setMonth)+"-"+String.format("%02d", setDay)
		    		+" "+String.format("%02d", setHour)+":"+String.format("%02d", setMinute)+":00";
		    
		    // ��ȡϵͳʱ��
			int year, month, day, hour ,minute, second;
			final Calendar c = Calendar.getInstance();
			year = c.get(Calendar.YEAR);
		    month = c.get(Calendar.MONTH)+1;  
		    day = c.get(Calendar.DAY_OF_MONTH);
		    hour = c.get(Calendar.HOUR_OF_DAY);  
		    minute = c.get(Calendar.MINUTE);
		    second = c.get(Calendar.SECOND);
		    // ת��Ϊ�ַ������ڻ�ȡʱ��
		    String currentTime = String.format("%04d", year)+"-"+String.format("%02d", month)+"-"+String.format("%02d", day)
		    		+" "+String.format("%02d", hour)+":"+String.format("%02d", minute)+":"+String.format("%02d", second);
		    
		    // ����ȴ�ʱ��
		    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    try {
				now = df.parse(setTime);
				set = df.parse(currentTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}   
		    TotalTime = (now.getTime() - set.getTime())/1000;   
		    
		    final Handler handler = new Handler();  
		    Runnable runnable = new Runnable() {  
		        @Override  
		        public void run() {  
		        	if(!AvailableResult){				// û�ж�λ���
		        		TotalTime--;					// ����ȴ���ʱ���֡���
			        	int leftSecond = (int)TotalTime % 60;
					    int leftMinute = (int)(TotalTime / 60 % 24);
					    int leftHour = (int)(TotalTime / 60 / 60);
					    // ��ʾʣ��ʱ��
					    String cntStr = String.format("%02d", leftHour) + ":" + String.format("%02d", leftMinute) + ":" 
					    		+ String.format("%02d", leftSecond);
					    Banner.setText(cntStr);
					    // ÿ1������һ�Σ���������ʱЧ��
			            if(TotalTime > 0)handler.postDelayed(this, 1000);  
		        	}
		        }  
		    };  
		    handler.postDelayed(runnable, 1000);  
        }
        else{
        	// ��������ģʽ
        	final String wait4trig[] = {"�ȴ���������.", "�ȴ���������..", "�ȴ���������..."};
        	final Handler handler = new Handler();  
		    Runnable runnable = new Runnable() {  
		        @Override  
		        public void run() {  
		        	TotalTime++;
		        	if(TotalTime == 3)TotalTime = 0;
		        	if(!AvailableResult){
		        		// ʹʡ�ԺŸ�����1��3���ε������������� -_-'
		        		Longitude.setText(wait4trig[(int) TotalTime]);
			        	Latitude.setText(wait4trig[(int) TotalTime]);
			        	Altitude.setText(wait4trig[(int) TotalTime]);
			        	Variance.setText(wait4trig[(int) TotalTime]);
			        	Time.setText(wait4trig[(int) TotalTime]);
			        	handler.postDelayed(this, 1000); 
		        	}
		        }  
		    };  
		    handler.postDelayed(runnable, 1000);  
        }
		
        // �ȴ���������λ���
        // ����handler
		final Handler receivehandler = new Handler() {
			@SuppressLint("HandlerLeak")
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what != 0) {
					// ��ö�λ���
					AvailableResult = true;		// ��λ�����־λ��λ
					Position locationPosition = GV.SysUser.getWorkGroup().getResult().getPosition();
					DateTime locationTime = GV.SysUser.getWorkGroup().getResult().getTime();
					double Var = 0;
					try{
						Var = GV.SysUser.getWorkGroup().getResult().getVariance().getVar().poll();
						Variance.setText(Var+"");
					}
					catch(Exception e){
						Variance.setText("����ֵ���ɷ���");
					}
					Banner.setText("���������");
					Longitude.setText(locationPosition.getLongitude()+"��");
					Latitude.setText(locationPosition.getLatitude()+"��");
					Altitude.setText(locationPosition.getAltitude()+"m");
					String resultTime = String.format("%04d", locationTime.getYear())+"/"+String.format("%02d", locationTime.getMonth())
							+"/"+String.format("%02d", locationTime.getDay())+" "+String.format("%02d", locationTime.getHour())+
							":"+String.format("%02d", locationTime.getMinute())+":"+String.format("%02d", locationTime.getSecond());
					Time.setText(resultTime);
					
					// �ڰٶȵ�ͼ����ʾ
					Result_Baidumap.clear();
					addReceiver2Map(GV, Result_Baidumap);
					LatLng TargetPoint = new LatLng(locationPosition.getLatitude(), locationPosition.getLongitude());
					// ������ͼ�����ﲢ�ڵ�ͼ�������ʾ
			    	BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);  
			    	OverlayOptions option = new MarkerOptions().position(TargetPoint).icon(bitmap);
			    	Result_Baidumap.addOverlay(option);
				}
			}
		};
		// ����Communicator������
		Communicator Receiver_cator = new Communicator(receivehandler);
		Receiver_cator.receiveData(GV, FRAME_TYPE.LOCATION_RESULT);		// ����ָ��֡
	}
	
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
        Result_MapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        Result_MapView.onResume();  
        isBackGround = false;
		nManager.cancel(ID);
		mHomeWatcher.startWatch();
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
        Result_MapView.onPause();  
        mHomeWatcher.stopWatch();// ��onPause��ֹͣ��������Ȼ�ᱨ���
        }  
    @Override
	public void onRestart(){
		super.onRestart();
		// activity���»�ý���
		isBackGround = false;
		nManager.cancel(ID);
		mHomeWatcher.startWatch();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location_result, menu);
		return true;
	}
	
	// ��ȡ"����"�����¼�
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent _intent = new Intent(LocationResultActivity.this, SettingActivity.class);
			startActivity(_intent);
			return true;
		default:
			break;
		}
		return false;
	}
	
	// ��ٶȵ�ͼ��ӽ��ջ�λ��
	public void addReceiver2Map(GlobalVariable GV, BaiduMap mBaidumap){
		int i  = 0;
		receiverpos = new LatLng[GV.SysUser.getWorkGroup().getTunerGroup().size()];
		for(Tuner tuner : GV.SysUser.getWorkGroup().getTunerGroup()){
			i++;
			// ��ȡ���ջ�������λ��
			LatLng point = new LatLng(tuner.getPosition().getLatitude(), tuner.getPosition().getLongitude());
			receiverpos[i-1] = point;
			// ������ʾ������view
			ReceiverView receiView = new ReceiverView(LocationResultActivity.this, R.drawable.map_receiver);
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(receiView.
					getBitmapFromView(GV.MAP_FONT_COLOR_UNCHOSEN, GV.MAP_FONT_SIZE, "���ջ�"+tuner.getTunerID()));
			//����MarkerOption�������ڵ�ͼ�����Marker  
			OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);  
			//�ڵ�ͼ�����Marker������ʾ  
			mBaidumap.addOverlay(option);
		}
	}

	@Override
	public void onMapLoaded() {
		// �ڵ�ͼ������ɺ�ʹ��ͼ��ת����������ʾ����
		LatLngBounds.Builder boundsbuilder = new LatLngBounds.Builder();
		for(int i = 0; i < receiverpos.length; i ++){
			boundsbuilder.include(receiverpos[i]);
		}
		LatLngBounds bounds =  boundsbuilder.build();
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
		Result_Baidumap.animateMapStatus(u, 1000);
	}
	
	// ��ȡ���ؼ�����¼�
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {  
        	// ������һactivity
        	Intent _intent = new Intent(LocationResultActivity.this, LocationParameterActivity.class);
			startActivity(_intent);
        }
        return false;
    }

}
