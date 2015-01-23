package com.qingshuimonk.tdoaclient;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.qingshuimonk.tdoaclient.data_structrue.Correlation;
import com.qingshuimonk.tdoaclient.data_structrue.DateTime;
import com.qingshuimonk.tdoaclient.data_structrue.Position;
import com.qingshuimonk.tdoaclient.data_structrue.Result;
import com.qingshuimonk.tdoaclient.data_structrue.TunerWorkParameter;
import com.qingshuimonk.tdoaclient.data_structrue.Variance;
import com.qingshuimonk.tdoaclient.utils.SysApplication;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/***
 * This is an android activity file:
 * function:	1.Show user location results;
 * 				2.Provide entrance of the presentation of details of correlation and variance;
 * @author Huang Bohao
 * @version 1.0.0
 * @since 2014.11.21
 *
 */
/***
 * FIXME
 * @author Huang Bohao
 * 本activity已被LocationResultActivity代替
 */
public class ResultActivity extends Activity {
	int TimePowerCheck = 0;
	int isBackGround = 0;
	
	// create ArrayList and ArrayAdapterk
	ArrayList<Result> resultitem = new ArrayList<Result>();
	ResultAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		
		// add activity to list
		SysApplication.getInstance().addActivity(this);
		
		// Used to access activity-level global variable
		final GlobalVariable GV = (GlobalVariable) getApplicationContext();
		final TunerWorkParameter parameter = GV.SysUser.getWorkGroup().getParameter();	

		// create action bar
		ActionBar LoginActionBar = this.getActionBar();
		LoginActionBar.setDisplayShowTitleEnabled(true); // ActionBar shows title only
		LoginActionBar.setDisplayShowHomeEnabled(false);
		
		// get ID of widgets
		final ListView resultList = (ListView)findViewById(R.id.resultList);
		Button Back = (Button)findViewById(R.id.resultBack);
		Button RunInBackGround = (Button)findViewById(R.id.resultBackGround);
		Button Exit = (Button)findViewById(R.id.resultExit);
		
		// bind ArrayList and ArrayAdapter
		adapter = new ResultAdapter(this, R.layout.item_result,resultitem);
		resultList.setAdapter(adapter);
		
		if(!GV.DEBUG_UDP_CONNECTION){
			// create virtual location result 
			// for debug only
			DateTime time1 = new DateTime((short)2014,(short)11,(short)21,(short)21,(short)32,(short)20,(short)21);
			DateTime time2 = new DateTime((short)2014,(short)11,(short)21,(short)21,(short)32,(short)25,(short)21);
			DateTime time3 = new DateTime((short)2014,(short)11,(short)21,(short)21,(short)32,(short)30,(short)21);
			Position Pos = new Position(12.34567,23.45678,34.56789);
			ArrayList<Correlation> CorList = null;
			Variance Var = null;
			
			Result result1 = new Result(CorList, Pos, Var, time1);
			Result result2 = new Result(CorList, Pos, Var, time2);
			Result result3 = new Result(CorList, Pos, Var, time3);
			
			resultitem.add(resultitem.size(),result1);
			resultitem.add(resultitem.size(),result2);
			resultitem.add(resultitem.size(),result3);
			resultitem.add(resultitem.size(),result3);
			resultitem.add(resultitem.size(),result3);
			resultitem.add(resultitem.size(),result3);
			resultitem.add(resultitem.size(),result3);
			adapter.notifyDataSetChanged();
		}
		
		
		Back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
				builder.setMessage("返回请按确定:").setIcon(android.R.drawable.ic_dialog_alert)
				       .setCancelable(false)
				       .setPositiveButton("确定", new DialogInterface.OnClickListener(){
									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										Intent intent = new Intent(ResultActivity.this, ReceiverChooseActivity.class);
										startActivity(intent);
									}
				       })
				       .setNegativeButton("取消", null);
				AlertDialog BackAlert = builder.create();
				BackAlert.show();
			}
		});
		
		RunInBackGround.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
				builder.setMessage("后台运行请按确定:").setIcon(android.R.drawable.ic_dialog_alert)
				       .setCancelable(false)
				       .setPositiveButton("确定", new DialogInterface.OnClickListener(){
									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										// add method to run in back ground
									}
				       })
				       .setNegativeButton("取消", null);
				AlertDialog RunInBackGroundAlert = builder.create();
				RunInBackGroundAlert.show();
			}
		});
		
		Exit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
				builder.setMessage("退出请按确定:").setIcon(android.R.drawable.ic_dialog_alert)
				       .setCancelable(false)
				       .setPositiveButton("确定", new DialogInterface.OnClickListener(){
									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										// add method to exit
										SysApplication.getInstance().exit();
									}
				       })
				       .setNegativeButton("取消", null);
				AlertDialog ExitAlert = builder.create();
				ExitAlert.show();
			}
		});
		
		if(parameter.getTrigMode() == 0){
			// time trigger
			LayoutInflater inflater = 
					(LayoutInflater) ResultActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
			final View layout = inflater.inflate(R.layout.timeprocess_dialog,  
                    (ViewGroup) findViewById(R.id.timeprocessdiaog));
			// 创建dialog
			final AlertDialog TimeprocessDialog = new AlertDialog.Builder(ResultActivity.this)
			.setView(layout)
			.setIcon(android.R.drawable.ic_dialog_info)
			.setTitle("等待触发时间")
			.setPositiveButton("取消本次定位", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					AlertDialog CancelReaffirmDialog = new AlertDialog.Builder(ResultActivity.this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("取消定位:")
					.setMessage("是否确认取消本次定位？")
					.setPositiveButton("确定", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// cancel the monitor
							Intent _intent = new Intent(ResultActivity.this, RegionChooseActivity.class);
							startActivity(_intent);
						}
					})
					.setNegativeButton("取消", null)
					.create();
					CancelReaffirmDialog.show();
				}
			})
			.setNeutralButton("后台运行", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					isBackGround = 1;
					Intent intent = new Intent(Intent.ACTION_MAIN);
			        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			        intent.addCategory(Intent.CATEGORY_HOME);
			        ResultActivity.this.startActivity(intent);
				}
			})
			.create();
			TimeprocessDialog.show();
			
			//make dialog cannot be closed
			try {
				Field field = TimeprocessDialog.getClass().getSuperclass().getDeclaredField("mShowing");
				field.setAccessible(true);
				field.set(TimeprocessDialog, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
	    	// get widget ID
	    	final ProgressBar TimeProcessBar = (ProgressBar)layout.findViewById(R.id.timeprogressBar);
	    	final TextView TimeCountDown = (TextView)layout.findViewById(R.id.timeprocesstime);
	    	final TextView text = (TextView)layout.findViewById(R.id.timeprocessbanner);
	    	
			// set handler
			final Handler handler = new Handler() {    
				public void handleMessage(Message msg) { 
					int resttimepercent, resttime;
					resttimepercent = msg.what%100;
					resttime = msg.what/1000*1000;
					TimeProcessBar.setProgress(resttimepercent);
					int days = resttime / (1000 * 60 * 60 * 24);
		    		int hours = (resttime-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
		    		int minutes = (resttime-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
		    		int seconds = (resttime-days*(1000*60*60*24)-hours*(1000*60*60)-minutes*(1000*60))/1000;
		    		TimeCountDown.setText(""+days+"天"+hours+"小时"+minutes+"分"+seconds+"秒");
		            super.handleMessage(msg);  
		            if((msg.what == 0)||(resttime <= 0)){
		            	// close dialog
		            	try {
		    				Field field = TimeprocessDialog.getClass().getSuperclass().getDeclaredField("mShowing");
		    				field.setAccessible(true);
		    				field.set(TimeprocessDialog, true);
		    			} catch (Exception e) {
		    				e.printStackTrace();
		    			}
					    TimePowerCheck = 1;		// mark the flag variable to 1
					    
					    if(isBackGround == 1){
					    	// send notification
					    	NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
					    	Notification notification = new Notification(android.R.drawable.ic_dialog_info, "触发时间到", 
					    			System.currentTimeMillis());
					    	notification.flags |= Notification.FLAG_AUTO_CANCEL; // notification will auto cancel when clicked
					    	// set notified sound and vibrate
					    	//notification.defaults |= notification.DEFAULT_SOUND | notification.DEFAULT_VIBRATE;
					    	Intent openintent= new Intent(ResultActivity.this, ResultActivity.class);
					    	PendingIntent contentIntent = PendingIntent.getActivity(ResultActivity.this, 
					    			0, openintent,0);
					    	notification.setLatestEventInfo(ResultActivity.this, "注意", "触发时间到！", contentIntent);
					    	mNotificationManager.notify(0, notification);
					    }
					    
					    // 打开服务器信息读取进程
					    //WaitMessage.show();
						//waitThread.start();   
		            	TimeprocessDialog.dismiss();
		            }
				}     
			};   
			
			// 开启线程
			Thread testThread = new Thread() {       
				public void run() {     
					//等待时间  
					
					// 获取系统时间
					int year, month, day, hour ,minute, second;
					final Calendar c = Calendar.getInstance();
					year = c.get(Calendar.YEAR);
				    month = c.get(Calendar.MONTH)+1;  
				    day = c.get(Calendar.DAY_OF_MONTH);
				    hour = c.get(Calendar.HOUR_OF_DAY);  
				    minute = c.get(Calendar.MINUTE);
				    second = c.get(Calendar.SECOND);
				    // 获取设定时间
				    int setYear, setMonth, setDay, setHour, setMinute;
				    setYear = parameter.getTrigTime().getYear();
				    setMonth = parameter.getTrigTime().getMonth();
				    setDay = parameter.getTrigTime().getDay();
				    setHour = parameter.getTrigTime().getHour();
				    setMinute = parameter.getTrigTime().getMinute();
				    
				    // 计算总等待时间
				    long TotalTime;
				    long currentTime, setTime;
				    currentTime = 
				    		year*10000000000L + month*100000000L + day*1000000L + hour*10000 + minute*100 + second;
				    setTime = 
				    		setYear*10000000000L + setMonth*100000000L + setDay*1000000L + setHour*10000 + setMinute*100;
				    TotalTime = (int)(setTime - currentTime);
				    
				    // 判断时间是否相等
				    while((year!=setYear)||(month!=setMonth)||(day!=setDay)||(hour!=setHour)
				    		||(minute!=setMinute)||(second!=0)){
				    	// 时间不等
				    	
				    	// 计算剩余时间
				    	// 获取系统时间
				    	Calendar c1 = Calendar.getInstance();
						year = c1.get(Calendar.YEAR);
					    month = c1.get(Calendar.MONTH)+1;  
					    day = c1.get(Calendar.DAY_OF_MONTH);
					    hour = c1.get(Calendar.HOUR_OF_DAY);  
					    minute = c1.get(Calendar.MINUTE);
					    second = c1.get(Calendar.SECOND);
					    // 计算剩余等待时间
					    long RestTime;
					    currentTime = 
					    		year*10000000000L + month*100000000L + day*1000000L + hour*10000 + minute*100 + second;
					    RestTime = (int)(setTime - currentTime);
				    	//TimeProcessBar.setProgress((int)(RestTime/TotalTime)*100);
				    	Message processmessage = new Message();
				    	processmessage.what = 100 - (int)(RestTime*100/TotalTime);
				    	// 计算时间差
				    	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
				    	try{
				    		Date current = df.parse(""+currentTime);
				    		Date set = df.parse(""+setTime);
				    		long diff = set.getTime() - current.getTime();//这样得到的差值是微秒级别
				    		// 传递参数
				    		processmessage.what = (int)diff + processmessage.what;
				    		handler.sendMessage(processmessage);
				    	}
				    	catch (Exception e){
				    	}
				    	
				    }
				    Message Endmessage = new Message();
				    Endmessage.what = 0;    // 告诉handler  
				    handler.sendMessage(Endmessage);
				    //waitThread.start();   
					}     
				};     
			testThread.start();    
				   
		}
	}
	
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {  
        	Intent _intent = new Intent(ResultActivity.this, ReceiverChooseActivity.class);
			startActivity(_intent);
        }
        return false;
    }
	
	@Override
	public void onRestart(){
		super.onRestart();
		// activity has get focus back
		isBackGround = 0;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.result, menu);
		return true;
	}
	
	@Override  
	 public boolean onOptionsItemSelected(MenuItem item) {  
		 switch(item.getItemId()){
		 case R.id.action_settings:
			 Intent _intent = new Intent(ResultActivity.this, SettingActivity.class);
			 startActivity(_intent);
			 return true;
		 default:
			 break;
		 }
		return false;
	 }

}
