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
 * ��activity���ڶ�����ջ�ѡ�����
 * ����xml�ļ�: activity_receiver_choose.xml
 * ����:		
 * 1.���û���ʾ���ý��ջ����乤��״̬��
 * 2.�����û���ɽ��ջ�ѡ��
 * 3.������������û�ѡ������
 * @author Huang Bohao
 * @version 1.2.0
 * @since 2014.11.18
 *  
 * 01/13/2015 1.1.0 �޸�˵����
 * ����˽����ͼ��ʾ����
 * 
 * 01/17/2015 1.2.0 �޸�˵����
 * �ڽ����ͼ��ʾ�������µ�view����ʾͼ�ĸ�����
 */
public class ReceiverChooseActivity extends Activity{
	
	// ����ArrayList��ArrayAdapter
	ArrayList<Tuner> receiveritem = new ArrayList<Tuner>();					// ������ʾ���н��ջ�
	ArrayList<Tuner> AvailableTunerGroup = new ArrayList<Tuner>();			// �����û�ѡ����ջ�	
	ReceiverAdapter adapter;
	
	// Baidu��ͼ
	MapView mMapView = null;  
	BaiduMap ReceiverBaiduMap = null;
	LatLng[] receiverpos = null;
	
	// ��ʼ��ȷ������
	String show = new String();
	
	// udp������ز�������
	final int MAX_DATA_PACKET_LENGTH = 40;  
    byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];  
    DatagramSocket udpSocket;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.activity_receiver_choose);
		
		// ����activity��ӵ�SysApplication����
		SysApplication.getInstance().addActivity(this);
		
		// ��ȡGlobalVariable���ȫ�ֱ���
		final GlobalVariable GV = (GlobalVariable)getApplicationContext(); 
		
		// ����action bar
		ActionBar LoginActionBar = this.getActionBar();
		LoginActionBar.setDisplayShowTitleEnabled(true);	// ActionBarֻ��ʾtitle
		LoginActionBar.setDisplayShowHomeEnabled(false);
		
		final ListView receiverList = (ListView)findViewById(R.id.receiverlist);
		final Button ReceiverChooseNext = (Button)findViewById(R.id.receiverchoosenext);
		
		// ����ArrayList��ArrayAdapter
		adapter = new ReceiverAdapter(this, R.layout.item_receiver, receiveritem);
		receiverList.setAdapter(adapter);
		
		// ��ȡ�ٶȵ�ͼ������
        mMapView = (MapView) findViewById(R.id.receiverchoose_Map);  
        ReceiverBaiduMap = mMapView.getMap();
		
		if(GV.DEBUG_UDP_CONNECTION){
			// ������������
			final ProgressDialog SendMessage = new ProgressDialog(ReceiverChooseActivity.this);
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
			
			// ����Communicator������
			Communicator DataRequest_cator = new Communicator(sendhandler);
			DataRequest_cator.sendDataRequest(GV, FRAME_TYPE.DATA_REQUEST, (byte) 0x02);
			
			// ��ȡ��������Ϣ
			final ProgressDialog ReceiveMessage = new ProgressDialog(
					ReceiverChooseActivity.this);
			ReceiveMessage.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			ReceiveMessage.setMessage("�ȴ��ӷ��������ղ���");
			ReceiveMessage.setCancelable(false);
			ReceiveMessage.setButton("ȡ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					AlertDialog CancelReaffirmDialog = new AlertDialog.Builder(
							ReceiverChooseActivity.this)
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setTitle("ȡ��������Ϣ:")
							.setMessage("�Ƿ�ȷ��ȡ�����ν��գ�")
							.setPositiveButton("ȷ��",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											// ���ȡ��
											Intent _intent = new Intent(
													ReceiverChooseActivity.this, LocationParameterActivity.class);
											startActivity(_intent);
										}
									}).setNegativeButton("ȡ��", null).create();
					CancelReaffirmDialog.show();
				}
			});
			ReceiveMessage.show();
			// ����handler
			final Handler receivehandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					if (msg.what != 0) {
						for(Tuner tuner:GV.AvailableTuner){
							receiveritem.add(tuner);
							AvailableTunerGroup.add(tuner);
							
							// �����ջ���ӵ���ͼ
							addReceiver2Map(GV, ReceiverBaiduMap);
							
							// ���ذٶȵ�ͼlogo�ͷŴ���С�ؼ�
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
			
			// ����Communicator��
			Communicator Receiver_cator = new Communicator(receivehandler);
			Receiver_cator.receiveData(GV, FRAME_TYPE.AVAILABLE_RECEIVER);	// ����ָ��֡
		}
		else{
			// ��wifi����
			// ����������ջ�������ʼ����״̬��λ��
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
			
			// ��ArrayList��ӽ��ջ�
			AvailableTunerGroup.add(receiverTuner1);
			AvailableTunerGroup.add(receiverTuner2);
			AvailableTunerGroup.add(receiverTuner3);
			AvailableTunerGroup.add(receiverTuner4);
			AvailableTunerGroup.add(receiverTuner5);
		}
		
		// ����һ����������������
		ReceiverChooseNext.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				byte ReceiverNum;
				final ArrayList<Integer> ChooseResult = adapter.getChooseResult();
				ReceiverNum = adapter.getReceiverNum();
				
				if((ReceiverNum > GV.MAX_REC_NUM)||(ReceiverNum < GV.MIN_REC_NUM)){
					// ���ջ�ѡ�������Ч
					Toast.makeText(getApplicationContext(), "ѡ����Ч\n���ջ�ѡ����ĿӦΪ"+GV.MIN_REC_NUM+"~"+GV.MAX_REC_NUM
							+"����", Toast.LENGTH_SHORT).show();
				}
				else{
					// ���ջ�ѡ������Ч
					// ����ѡ����
					TunerWorkGroup Group = GV.SysUser.getWorkGroup();
					Group.setTunerGroup(AvailableTunerGroup, ChooseResult);
					
					// ����ȷ���ַ���
					show = generateConfirmString(GV.SysUser);
					
					// ��ʾȷ�϶Ի���
					Dialog AffirmDialog = new AlertDialog.Builder(ReceiverChooseActivity.this)
					.setIcon(android.R.drawable.ic_dialog_info).setTitle("��ȷ�����ò���:")
					.setMessage(show)
					.setNegativeButton("ȡ��", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO ȡ��ȷ�����ò���������
							
						}
					})
					.setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// �ж��Ƿ����㹻ʱ�乩���������ö�λ����
							final TunerWorkParameter parameter = GV.SysUser.getWorkGroup().getParameter();
							final DateTime trigtime = parameter.getTrigTime();
							if(parameter.getTrigMode() == 0){
								// ��ȷ��ʱ��
								// ��ȡϵͳʱ��
								int year, month, day, hour ,minute;
								final Calendar c = Calendar.getInstance();
								year = c.get(Calendar.YEAR);
							    month = c.get(Calendar.MONTH)+1;  
							    day = c.get(Calendar.DAY_OF_MONTH);
							    hour = c.get(Calendar.HOUR_OF_DAY);  
							    minute = c.get(Calendar.MINUTE);
							    // ��ȡ����ʱ��
							    int setYear, setMonth, setDay, setHour, setMinute;
							    setYear = trigtime.getYear();
							    setMonth = trigtime.getMonth();
							    setDay = trigtime.getDay();
							    setHour = trigtime.getHour();
							    setMinute = trigtime.getMinute();
							    // ����ʱ���
							    long currentTime, setTime;
							    currentTime = 
							    		year*100000000L + month*1000000L + day*10000 + hour*100 + minute;
							    setTime = 
							    		setYear*100000000L + setMonth*1000000L + setDay*10000 + setHour*100 + setMinute;
							    // ʱ���С���趨ʱ�����
							    if(currentTime+ GV.TIME_FOR_SERVER > setTime){
							    	Dialog InvalidTrigTime = new AlertDialog.Builder(ReceiverChooseActivity.this)
							    	.setIcon(android.R.drawable.ic_dialog_alert).setTitle("��Ч����ʱ��")
							    	.setMessage("��������ʣ���ý��ջ�ʱ�䲻��"+GV.TIME_FOR_SERVER+"���ӣ��Ƿ��Զ��ӳ�����ʱ�䣿")
							    	.setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											trigtime.setMinute((short) (c.get(Calendar.MINUTE)+GV.TIME_FOR_SERVER));
											
											// ����ȷ���ַ���
											show = generateConfirmString(GV.SysUser);
											
											// ��ʾȷ�϶Ի���
											Dialog ReAffirmDialog = new AlertDialog.Builder(ReceiverChooseActivity.this)
											.setIcon(android.R.drawable.ic_dialog_info).setTitle("��ȷ�����ò���:")
											.setMessage(show)
											.setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
												@SuppressLint("HandlerLeak")
												@Override
												public void onClick(DialogInterface dialog,int which) {
													// ����������Ͷ�λ����
													final ProgressDialog SendMessage = new ProgressDialog(ReceiverChooseActivity.this);
													SendMessage.setProgressStyle(ProgressDialog.STYLE_SPINNER);
													SendMessage.setMessage("�ȴ�����������Ͳ���");
													SendMessage.setCancelable(false);
													SendMessage.show();
													// ����handler
													final Handler sendhandler = new Handler() {  
														@Override
														public void handleMessage(Message msg) {
															super.handleMessage(msg);
															if(msg.what == 0){
																SendMessage.dismiss();
																// ��ת����һ��activity
															    Intent _intent = new Intent(ReceiverChooseActivity.this, 
															    		LocationResultActivity.class);
															    startActivity(_intent);
																}
														}     
													}; 
													// ����Communicator��
													Communicator Receiver_cator = new Communicator(sendhandler);
													Receiver_cator.sendInstruction(GV, FRAME_TYPE.CHOSEN_RECEIVER);
												}
											})
											.setNegativeButton("ȡ��", null)
											.create();
											ReAffirmDialog.show();
										}
							    	})
							    	.setNegativeButton("��������ʱ��", new DialogInterface.OnClickListener(){
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											// �ص�LocationParameterActivity���������ö�λʱ��
											Intent _intent = new Intent(ReceiverChooseActivity.this, LocationParameterActivity.class);
											startActivity(_intent);
										}
							    	})
							    	.setNeutralButton("ȡ��", null)
							    	.create();
							    	InvalidTrigTime.show();
							    }
							    else{
							    	// ����������Ͷ�λ��������
									final ProgressDialog SendMessage = new ProgressDialog(ReceiverChooseActivity.this);
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
												// ��ת����һ��activity
											    Intent _intent = new Intent(ReceiverChooseActivity.this, 
											    		LocationResultActivity.class);
											    startActivity(_intent);
												}
										}     
									}; 
									// ����Communicator��
									Communicator Receiver_cator = new Communicator(sendhandler);
									Receiver_cator.sendInstruction(GV, FRAME_TYPE.CHOSEN_RECEIVER);
							    }
							    
							}
							else{
								// ��������ģʽ
								// ����������Ͷ�λ��������
								final ProgressDialog SendMessage = new ProgressDialog(ReceiverChooseActivity.this);
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
											// ��ת����һ��activity
										    Intent _intent = new Intent(ReceiverChooseActivity.this, 
										    		LocationResultActivity.class);
										    startActivity(_intent);
											}
									}     
								}; 
								// ����Communicator��
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
	
	// ����ȷ���ַ���
	public String generateConfirmString(User SysUser){
		String show = new String();
		TunerWorkParameter ShowParameter = SysUser.getWorkGroup().getParameter();
		LocationRegion ShowRegion = ShowParameter.getLocationRegion();
		
		show = "����������ͣ� ";
		if(ShowRegion.getRegionMode() == 0){
			// ���ζ�λ����
			show = show + "����\n���Ͻǵ����꣺ \n����:" + ShowRegion.getRegionValue1()
					+"�� \nγ��:" + ShowRegion.getRegionValue2() + 
					"��\n���½ǵ����꣺ \n����:" + ShowRegion.getRegionValue3() + 
					"�� \nγ��:" + ShowRegion.getRegionValue4() + "��\n";
		}
		else{
			// Բ�ζ�λ����
			show = show + "Բ��\nԲ�����꣺ \n���ȣ�" + ShowRegion.getRegionValue1()
					+"�� \nγ��:" + ShowRegion.getRegionValue2() +
					"��\n�뾶��" + ShowRegion.getRegionValue3() + "m\n";
		}
		show = show + "����Ƶ�ʣ� " + ShowParameter.getCenterFreq() + "Hz\n"
				+ "���� " + ShowParameter.getBandWidth() + "Hz\n"
				+ "�������� " + ShowParameter.getIQNum() + "\n"
				+ "�ֶ���������� " + ShowParameter.getMGC() + "dB\n";
		if(ShowParameter.getTrigMode() == 0){
			// ʱ�䴥��
			show = show + "����ʱ�䣺 " + ShowParameter.getTrigTime().getYear() + "/" + 
					ShowParameter.getTrigTime().getMonth() + "/" + ShowParameter.getTrigTime().getDay() + "/ " + 
					ShowParameter.getTrigTime().getHour() + ":" + ShowParameter.getTrigTime().getMinute() + ":00" + "\n";
		}
		else{
			// ��������
			show = show + "������ƽ�� " + ShowParameter.getTrigPower() + "dB��V\n";
		}
		show = show + "ѡ����ջ���Ŀ�� " + SysUser.getWorkGroup().getTunerGroup().size() + 
				"\n��ѡ���ջ���ţ�\n";
		for(Tuner tuner:SysUser.getWorkGroup().getTunerGroup()){
			show = show + "���ջ����ƣ����ջ� " + tuner.getTunerID() + "\n";
		}
		return show;
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
			ReceiverView receiView = new ReceiverView(ReceiverChooseActivity.this, R.drawable.map_receiver);
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(receiView.
					getBitmapFromView(GV.MAP_FONT_COLOR_UNCHOSEN, GV.MAP_FONT_SIZE, "���ջ�"+tuner.getTunerID()));
			//����MarkerOption�������ڵ�ͼ�����Marker  
			OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);  
			//�ڵ�ͼ�����Marker������ʾ  
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
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onResume();  
    }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onPause();  
    }

}
