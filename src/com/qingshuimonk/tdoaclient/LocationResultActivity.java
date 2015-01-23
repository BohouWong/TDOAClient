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
 * 本activity用于定义结果显示界面
 * 配套xml文件: activity_location_result.xml
 * 功能:		
 * 	1.接收服务器定位结果参数并显示;
 * 	2.引导用户选择需要观察相关值和频谱的接收机，并将参数发送给服务器;
 * @author Huang Bohao
 * @version 1.2.0
 * @since 2014.11.13
 *
 * 01/13/2015 1.1.0 修改说明：
 * 添加了结果地图显示功能
 * 
 * 01/17/2015 1.2.0 修改说明：
 * 在结果地图显示中运用新的view，显示图文覆盖物
 */
public class LocationResultActivity extends Activity implements OnMapLoadedCallback {
	
	// 百度地图
	MapView Result_MapView = null; 
	BaiduMap Result_Baidumap = null;
	
	// 控件
	TextView Banner = null;
	TextView Longitude = null;
	TextView Latitude = null;
	TextView Altitude = null;
	TextView Variance = null;
	TextView Time = null;
	TextView Correlation = null;
	TextView Spectrum = null;
	
	// 变量
    long TotalTime = 0;
    java.util.Date now,set;
	LatLng[] receiverpos = null;				// 用于存储接收机的经纬度坐标
	boolean AvailableResult = false;			// 可用结果标志位
	boolean isBackGround = false;				// 后台运行标志位
	private HomeWatcher mHomeWatcher;			// home键监听方法
	
	// notification通知
	private Notification notification;  
    private NotificationManager nManager;  
    private Intent intent;  
    private PendingIntent pIntent;  
    private static final int ID = 1;			// Notification的标示ID  

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext()); 
		setContentView(R.layout.activity_location_result);
		
		// 将此activity添加到SysApplication类中
		SysApplication.getInstance().addActivity(this);

		// 获取GlobalVariable类的全局变量
		final GlobalVariable GV = (GlobalVariable) getApplicationContext();
		final TunerWorkParameter parameter = GV.SysUser.getWorkGroup().getParameter();

		// 创建action bar
		ActionBar LoginActionBar = this.getActionBar();
		LoginActionBar.setDisplayShowTitleEnabled(true); // ActionBar只显示title
		LoginActionBar.setDisplayShowHomeEnabled(false);
		
		// 获得百度地图控件
		Result_MapView = (MapView) findViewById(R.id.result_bmapView);  
		Result_Baidumap = Result_MapView.getMap();
		Result_Baidumap.setOnMapLoadedCallback(this);
		
		// 将工作接收机显示在地图上
		addReceiver2Map(GV, Result_Baidumap);
		
		// 隐藏百度地图logo和放大缩小控件
		int count = Result_MapView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = Result_MapView.getChildAt(i);
            if (child instanceof ImageView || child instanceof ZoomControls)
                child.setVisibility(View.INVISIBLE);
        }
        
        // 获取控件
        Banner = (TextView)findViewById(R.id.result_banner_large);
        Longitude = (TextView)findViewById(R.id.result_longtitude_value);
        Latitude = (TextView)findViewById(R.id.result_latitude_value);
        Altitude = (TextView)findViewById(R.id.result_altitude_value);
        Variance = (TextView)findViewById(R.id.result_variance_value);
        Time = (TextView)findViewById(R.id.result_time_value);
        Correlation = (TextView)findViewById(R.id.result_correlation);
        Spectrum = (TextView)findViewById(R.id.result_spectrum);
        
        // 初始化notification
        String service = NOTIFICATION_SERVICE;  
        nManager = (NotificationManager) this.getSystemService(service); 
        notification = new Notification();  
        
        // 设置home键监听
        mHomeWatcher = new HomeWatcher(this); 
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() { 
            @SuppressWarnings("deprecation")
			@Override 
            public void onHomePressed() {  
            	isBackGround = true;
                String tickerText = "时差定位客户端已转入后台运行"; // 通知提示  
                // 显示时间  
                long when = System.currentTimeMillis();  
                notification.icon = android.R.drawable.ic_dialog_info;// 设置通知的图标  
                notification.tickerText = tickerText; // 显示在状态栏中的文字  
                notification.when = when; // 设置来通知时的时间  
                notification.flags = Notification.FLAG_NO_CLEAR; // 点击清除按钮时就会清除消息通知,但是点击通知栏的通知时不会消失
                // 单击通知后会跳转到NotificationResult类  
                intent = new Intent(LocationResultActivity.this, LocationResultActivity.class);  
                // 获取PendingIntent,点击时发送该Intent  
                pIntent = PendingIntent.getActivity(LocationResultActivity.this, 0, intent, 0);  
                // 设置通知的标题和内容  
                notification.setLatestEventInfo(LocationResultActivity.this, "时差定位系统正在后台运行", "正在等待接收定位结果", pIntent);  
                // 发出通知  
                nManager.notify(ID, notification); 
            } 
   
            @Override 
            public void onHomeLongPressed() { 
            } 
        }); 
        mHomeWatcher.startWatch(); 
        
        if(parameter.getTrigMode() == 0){
        	// 时间触发模式
        	Longitude.setText("等待触发时间...");
        	Latitude.setText("等待触发时间...");
        	Altitude.setText("等待触发时间...");
        	Variance.setText("等待触发时间...");
        	Time.setText("等待触发时间...");
        	// 获取触发时间
		    final int setYear, setMonth, setDay, setHour, setMinute;
		    setYear = parameter.getTrigTime().getYear();
		    setMonth = parameter.getTrigTime().getMonth();
		    setDay = parameter.getTrigTime().getDay();
		    setHour = parameter.getTrigTime().getHour();
		    setMinute = parameter.getTrigTime().getMinute();
		    // 转换为字符串用于获得时间
		    String setTime = String.format("%04d", setYear)+"-"+String.format("%02d", setMonth)+"-"+String.format("%02d", setDay)
		    		+" "+String.format("%02d", setHour)+":"+String.format("%02d", setMinute)+":00";
		    
		    // 获取系统时间
			int year, month, day, hour ,minute, second;
			final Calendar c = Calendar.getInstance();
			year = c.get(Calendar.YEAR);
		    month = c.get(Calendar.MONTH)+1;  
		    day = c.get(Calendar.DAY_OF_MONTH);
		    hour = c.get(Calendar.HOUR_OF_DAY);  
		    minute = c.get(Calendar.MINUTE);
		    second = c.get(Calendar.SECOND);
		    // 转换为字符串用于获取时间
		    String currentTime = String.format("%04d", year)+"-"+String.format("%02d", month)+"-"+String.format("%02d", day)
		    		+" "+String.format("%02d", hour)+":"+String.format("%02d", minute)+":"+String.format("%02d", second);
		    
		    // 计算等待时间
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
		        	if(!AvailableResult){				// 没有定位结果
		        		TotalTime--;					// 计算等待的时、分、秒
			        	int leftSecond = (int)TotalTime % 60;
					    int leftMinute = (int)(TotalTime / 60 % 24);
					    int leftHour = (int)(TotalTime / 60 / 60);
					    // 显示剩余时间
					    String cntStr = String.format("%02d", leftHour) + ":" + String.format("%02d", leftMinute) + ":" 
					    		+ String.format("%02d", leftSecond);
					    Banner.setText(cntStr);
					    // 每1秒运行一次，产生倒计时效果
			            if(TotalTime > 0)handler.postDelayed(this, 1000);  
		        	}
		        }  
		    };  
		    handler.postDelayed(runnable, 1000);  
        }
        else{
        	// 能量触发模式
        	final String wait4trig[] = {"等待触发能量.", "等待触发能量..", "等待触发能量..."};
        	final Handler handler = new Handler();  
		    Runnable runnable = new Runnable() {  
		        @Override  
		        public void run() {  
		        	TotalTime++;
		        	if(TotalTime == 3)TotalTime = 0;
		        	if(!AvailableResult){
		        		// 使省略号个数从1到3依次递增，避免无聊 -_-'
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
		
        // 等待服务器定位结果
        // 配置handler
		final Handler receivehandler = new Handler() {
			@SuppressLint("HandlerLeak")
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what != 0) {
					// 获得定位结果
					AvailableResult = true;		// 定位结果标志位置位
					Position locationPosition = GV.SysUser.getWorkGroup().getResult().getPosition();
					DateTime locationTime = GV.SysUser.getWorkGroup().getResult().getTime();
					double Var = 0;
					try{
						Var = GV.SysUser.getWorkGroup().getResult().getVariance().getVar().poll();
						Variance.setText(Var+"");
					}
					catch(Exception e){
						Variance.setText("方差值不可访问");
					}
					Banner.setText("结果接收中");
					Longitude.setText(locationPosition.getLongitude()+"°");
					Latitude.setText(locationPosition.getLatitude()+"°");
					Altitude.setText(locationPosition.getAltitude()+"m");
					String resultTime = String.format("%04d", locationTime.getYear())+"/"+String.format("%02d", locationTime.getMonth())
							+"/"+String.format("%02d", locationTime.getDay())+" "+String.format("%02d", locationTime.getHour())+
							":"+String.format("%02d", locationTime.getMinute())+":"+String.format("%02d", locationTime.getSecond());
					Time.setText(resultTime);
					
					// 在百度地图中显示
					Result_Baidumap.clear();
					addReceiver2Map(GV, Result_Baidumap);
					LatLng TargetPoint = new LatLng(locationPosition.getLatitude(), locationPosition.getLongitude());
					// 建立地图覆盖物并在地图上添加显示
			    	BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);  
			    	OverlayOptions option = new MarkerOptions().position(TargetPoint).icon(bitmap);
			    	Result_Baidumap.addOverlay(option);
				}
			}
		};
		// 调用Communicator工具类
		Communicator Receiver_cator = new Communicator(receivehandler);
		Receiver_cator.receiveData(GV, FRAME_TYPE.LOCATION_RESULT);		// 解析指令帧
	}
	
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        Result_MapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        Result_MapView.onResume();  
        isBackGround = false;
		nManager.cancel(ID);
		mHomeWatcher.startWatch();
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        Result_MapView.onPause();  
        mHomeWatcher.stopWatch();// 在onPause中停止监听，不然会报错的
        }  
    @Override
	public void onRestart(){
		super.onRestart();
		// activity重新获得焦点
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
	
	// 获取"设置"按键事件
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
	
	// 向百度地图添加接收机位置
	public void addReceiver2Map(GlobalVariable GV, BaiduMap mBaidumap){
		int i  = 0;
		receiverpos = new LatLng[GV.SysUser.getWorkGroup().getTunerGroup().size()];
		for(Tuner tuner : GV.SysUser.getWorkGroup().getTunerGroup()){
			i++;
			// 读取接收机的坐标位置
			LatLng point = new LatLng(tuner.getPosition().getLatitude(), tuner.getPosition().getLongitude());
			receiverpos[i-1] = point;
			// 设置显示覆盖物view
			ReceiverView receiView = new ReceiverView(LocationResultActivity.this, R.drawable.map_receiver);
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(receiView.
					getBitmapFromView(GV.MAP_FONT_COLOR_UNCHOSEN, GV.MAP_FONT_SIZE, "接收机"+tuner.getTunerID()));
			//构建MarkerOption，用于在地图上添加Marker  
			OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);  
			//在地图上添加Marker，并显示  
			mBaidumap.addOverlay(option);
		}
	}

	@Override
	public void onMapLoaded() {
		// 在地图加载完成后使地图跳转到覆盖物显示区域
		LatLngBounds.Builder boundsbuilder = new LatLngBounds.Builder();
		for(int i = 0; i < receiverpos.length; i ++){
			boundsbuilder.include(receiverpos[i]);
		}
		LatLngBounds bounds =  boundsbuilder.build();
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
		Result_Baidumap.animateMapStatus(u, 1000);
	}
	
	// 获取返回键点击事件
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {  
        	// 返回上一activity
        	Intent _intent = new Intent(LocationResultActivity.this, LocationParameterActivity.class);
			startActivity(_intent);
        }
        return false;
    }

}
