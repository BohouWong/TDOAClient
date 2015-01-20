package com.qingshuimonk.tdoaclient;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Calendar;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.qingshuimonk.tdoaclient.data_structrue.DateTime;
import com.qingshuimonk.tdoaclient.data_structrue.LocationRegion;
import com.qingshuimonk.tdoaclient.data_structrue.TunerWorkGroup;
import com.qingshuimonk.tdoaclient.data_structrue.TunerWorkParameter;
import com.qingshuimonk.tdoaclient.utils.Communicator;
import com.qingshuimonk.tdoaclient.utils.FrameFormer;
import com.qingshuimonk.tdoaclient.utils.FrameFormer.FRAME_TYPE;


/***
 * This is an android activity file:
 * fuction:		1.Guide user to finish inputting location parameters;
 * 				2.Transmit Location parameters to the client;
 * @author Huang Bohao
 * @version 1.0.0
 * @since 2014.11.13
 *
 */
public class LocationParameterActivity extends Activity {
	
	// variables
	private int CenterFreUnit = 0;
	private long CenterFre;
	private int BandWidth = 1000;
	private byte Sample;
	private int MGC;
	private short PowerTrig;
	private int CenterFreCheck = 0;
	private int SampleCheck = 0;
	private int MGCCheck = 0;
	private int TrigPowerCheck = 0;
	private int TrigTimeCheck = 0;
	private byte TrigModeChoose;
	private short wYear;
	private short wMonth;
	private short wDay;
	private short wHour;
	private short wMinute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_parameter);
		
		// add activity to list
		SysApplication.getInstance().addActivity(this);
		
		// Used to access activity-level global variable
		final GlobalVariable GV = (GlobalVariable)getApplicationContext(); 
		
		// variables for udp connection
		final int Local_Port = GV.Local_Port;
		final int Server_Port = GV.Server_Port;
		final String Server_Address = GV.Server_Address;
		final int Test_Port = GV.Test_Port;
		final String Test_Address = GV.Test_Address;
		
		// get widgets' ID
		final EditText InputCenterFre = (EditText) findViewById(R.id.inputcenterfre);
		final EditText InputSample = (EditText) findViewById(R.id.inputsample);
		final EditText InputMGC = (EditText) findViewById(R.id.inputmgc);
		final RadioGroup TrigMode = (RadioGroup) findViewById(R.id.trigmodegroup);
		final RadioButton TrigModeTime = (RadioButton) findViewById(R.id.timetrig);
		final RadioButton TrigModePower = (RadioButton) findViewById(R.id.powertrig);
		final Spinner InputcenterFreSpinner = (Spinner) findViewById(R.id.inputcenterfrespinner);
		final Spinner InputBandWidth = (Spinner) findViewById(R.id.inputbandwidth);
		final Button SetReceiverNext = (Button) findViewById(R.id.setreceivernext);
		
		// create action bar
		ActionBar LoginActionBar = this.getActionBar();
		LoginActionBar.setDisplayShowTitleEnabled(true); // ActionBar shows title only
		LoginActionBar.setDisplayShowHomeEnabled(false);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.hertzunit, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		InputcenterFreSpinner.setAdapter(adapter);

		ArrayAdapter<CharSequence> bdadapter = ArrayAdapter.createFromResource(
				this, R.array.bdunit, android.R.layout.simple_spinner_item);
		bdadapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		InputBandWidth.setAdapter(bdadapter);

		// Listener of center frequency's unit
		InputcenterFreSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// TODO Auto-generated method stub
						CenterFreUnit = position;
						// re-check of input frequency for unit is changed
						try {
							// check if the input frequency is legal
							String input = InputCenterFre.getText().toString()
									.trim();
							Float temp = Float.parseFloat(input);
							String[] sArray = input.split("\\.");

							if ((temp * Math.pow(1000, CenterFreUnit + 2) > GV.MAX_CENTER_FRE)
									|| (temp
											* Math.pow(1000, CenterFreUnit + 2) < GV.MIN_CENTER_FRE)) {
								InputCenterFre.setTextColor(Color.rgb(255, 0, 0));
								CenterFreCheck = 0;
							} else {
								InputCenterFre.setTextColor(Color.rgb(0, 0, 0));
								if (CenterFreUnit == 0)
									CenterFre = (long) ((double) temp * Math.pow(1000, CenterFreUnit + 2));
								else {
									int intpart = Integer.parseInt(sArray[0]);
									int decimalpart = Integer.parseInt(sArray[1]);
									CenterFre = (long) (intpart* (long) Math.pow(1000,CenterFreUnit + 2) + 
											decimalpart * (long) Math.pow(1000,CenterFreUnit + 2)
											/ (long) Math.pow(10, sArray[1].length()));
								}
								CenterFreCheck = 1;
							}
						} catch (Exception e) {

						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
					}
				});

		// Listener of bandwidth choosing result
		InputBandWidth.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					BandWidth = 1000;
					break;
				case 1:
					BandWidth = 3000;
					break;
				case 2:
					BandWidth = 5000;
					break;
				case 3:
					BandWidth = 10000;
					break;
				case 4:
					BandWidth = 12500;
					break;
				case 5:
					BandWidth = 25000;
					break;
				case 6:
					BandWidth = 30000;
					break;
				case 7:
					BandWidth = 50000;
					break;
				case 8:
					BandWidth = 100000;
					break;
				case 9:
					BandWidth = 150000;
					break;
				case 10:
					BandWidth = 200000;
					break;
				case 11:
					BandWidth = 500000;
					break;
				case 12:
					BandWidth = 1000000;
					break;
				case 13:
					BandWidth = 2000000;
					break;
				case 14:
					BandWidth = 5000000;
					break;
				case 15:
					BandWidth = 10000000;
					break;
				case 16:
					BandWidth = 20000000;
					break;
				default:
					BandWidth = 0;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});

		// Listener of edittext input change 
		TextWatcher InputCenterFreWatcher = new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				// Toast.makeText(LocationParameterActivity.this, CenterFreCheck+"",
				// Toast.LENGTH_SHORT).show();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				String input = s.toString().trim();
				try {
					// check if input center frequency is legal
					Float temp = Float.parseFloat(input);
					String[] sArray = input.split("\\.");
					if ((temp * Math.pow(1000, CenterFreUnit + 2) > GV.MAX_CENTER_FRE)
							|| (temp * Math.pow(1000, CenterFreUnit + 2) < GV.MIN_CENTER_FRE)) {
						InputCenterFre.setTextColor(Color.rgb(255, 0, 0));
						CenterFreCheck = 0;
					} else {
						InputCenterFre.setTextColor(Color.rgb(0, 0, 0));
						if (CenterFreUnit == 0)
							CenterFre = (long) ((double) temp * Math.pow(1000, CenterFreUnit + 2));
						else {
							int intpart = Integer.parseInt(sArray[0]);
							int decimalpart = Integer.parseInt(sArray[1]);
							CenterFre = (long) (intpart * (long) Math.pow(1000, CenterFreUnit + 2) + decimalpart
									* (long) Math.pow(1000, CenterFreUnit + 2) / (long) Math.pow(10, 
											sArray[1].length()));
						}
						CenterFreCheck = 1;
					}
				} catch (Exception e) {
				}
			}
		};

		TextWatcher InputSampleWatcher = new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				String input = s.toString().trim();
				try {
					// check if sample number is legal
					byte temp = Byte.parseByte(input);
					if ((temp > GV.MAX_IQ_NUM) || (temp < GV.MIN_IQ_NUM)) {
						InputSample.setTextColor(Color.rgb(255, 0, 0));
						SampleCheck = 0;
					} else {
						InputSample.setTextColor(Color.rgb(0, 0, 0));
						Sample = temp;
						SampleCheck = 1;
					}
				} catch (Exception e) {
				}
			}
		};

		TextWatcher InputMGCWatcher = new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				String input = s.toString().trim();
				try {
					// check if MGC input is legal
					int temp = Integer.parseInt(input);
					if ((temp > GV.MAX_MGC) || (temp < GV.MIN_MGC)) {
						InputMGC.setTextColor(Color.rgb(255, 0, 0));
						MGCCheck = 0;
					} else {
						InputMGC.setTextColor(Color.rgb(0, 0, 0));
						MGC = temp;
						MGCCheck = 1;
					}
				} catch (Exception e) {
				}
			}
		};

		// listener of editText's input change
		InputCenterFre.addTextChangedListener(InputCenterFreWatcher);
		InputSample.addTextChangedListener(InputSampleWatcher);
		InputMGC.addTextChangedListener(InputMGCWatcher);

		TrigModeTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// timer trigger is chosen
				TrigPowerCheck = 0;
				TrigModeChoose = 0;
				LayoutInflater inflater = (LayoutInflater) LocationParameterActivity.this
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				final View layout = inflater.inflate(R.layout.timetrig_dialog,
						(ViewGroup) findViewById(R.id.timetrigdialog));
				AlertDialog TimetrigDialog = new AlertDialog.Builder(
						LocationParameterActivity.this)
						.setView(layout)
						.setIcon(android.R.drawable.ic_dialog_info)
						.setTitle("请选择触发时间：")
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										// make the dialog cannot be closed
										try {
											Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
											field.setAccessible(true);
											field.set(dialog, false);
										} catch (Exception e) {
											e.printStackTrace();
										}
										// close dialog
										try {
											Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
											field.setAccessible(true);
											field.set(dialog, true);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								})
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										// make the dialog cannot be closed
										try {
											Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
											field.setAccessible(true);
											field.set(dialog, false);
										} catch (Exception e) {
											e.printStackTrace();
										}
										// deal time
										int year, month, day, hour, minute;
										DatePicker TimetrigDatepicker = (DatePicker) layout.findViewById(R.id.timetrigdatePicker);
										TimePicker TimetrigTimepicker = (TimePicker) layout.findViewById(R.id.timetrigtimePicker);

										int TrigYear, TrigMonth, TrigDay;
										TrigYear = TimetrigDatepicker.getYear();
										TrigMonth = TimetrigDatepicker.getMonth() + 1;
										TrigDay = TimetrigDatepicker.getDayOfMonth();

										int TrigHour, TrigMinute;
										TrigHour = Integer.parseInt(TimetrigTimepicker.getCurrentHour().toString().trim());
										TrigMinute = Integer.parseInt(TimetrigTimepicker.getCurrentMinute().toString().trim());

										// get system's time
										Calendar c = Calendar.getInstance();
										year = c.get(Calendar.YEAR);
										month = c.get(Calendar.MONTH) + 1;
										day = c.get(Calendar.DAY_OF_MONTH);
										hour = c.get(Calendar.HOUR_OF_DAY);
										minute = c.get(Calendar.MINUTE);

										long currentTime, setTime;
										currentTime = year * 100000000L + month * 1000000L + day * 10000 + hour
												* 100 + minute;
										setTime = TrigYear * 100000000L + TrigMonth * 1000000L + TrigDay * 10000 + 
												TrigHour * 100 + TrigMinute;
										
										// judge if the input time is legal
										if (currentTime + GV.TIME_FOR_SET > setTime) {
											// illegal
											Toast.makeText(
													getApplicationContext(),
													"输入时间错误，请重新输入！\n注意：请不要设置"+GV.TIME_FOR_SET+"分钟之内的触发时间。",
													Toast.LENGTH_SHORT).show();
											TrigTimeCheck = 0;
										} else {
											// legal
											TrigTimeCheck = 1;
											wYear = (short) TrigYear;
											wMonth = (short) TrigMonth;
											wDay = (short) TrigDay;
											wHour = (short) TrigHour;
											wMinute = (short) TrigMinute;
											// close dialog
											try {
												Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
												field.setAccessible(true);
												field.set(dialog, true);
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								}).create();
				TimePicker TimetrigTimepicker = (TimePicker) layout
						.findViewById(R.id.timetrigtimePicker);

				TimetrigDialog.show();
			}
		});

		TrigModePower.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// power trigger is chosen
				TrigTimeCheck = 0;
				TrigModeChoose = 1;
				LayoutInflater inflater = (LayoutInflater) LocationParameterActivity.this
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				final View layout = inflater.inflate(R.layout.powertrig_dialog,
						(ViewGroup) findViewById(R.id.powertrigdialog));
				AlertDialog PowertrigDialog = new AlertDialog.Builder(
						LocationParameterActivity.this).setView(layout).setIcon(android.R.drawable.ic_dialog_info)
						.setTitle("请输入能量触发阀值：").setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										// make the dialog cannot be closed
										try {
											Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
											field.setAccessible(true);
											field.set(dialog, false);
										} catch (Exception e) {
											e.printStackTrace();
										}
										// close dialog
										try {
											Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
											field.setAccessible(true);
											field.set(dialog, true);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								})
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										// make the dialog cannot be closed
										try {
											Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
											field.setAccessible(true);
											field.set(dialog, false);
										} catch (Exception e) {
											e.printStackTrace();
										}

										// get trigger voltage
										Short TrigPower;
										EditText Trig = (EditText) layout
												.findViewById(R.id.powertrigdialoginput);
										try {
											TrigPower = Short.parseShort(Trig.getText().toString().trim());
										} catch (Exception e) {
											TrigPower = -20;
										}

										// check legal input region
										if ((TrigPower < GV.MIN_TRIG_POWER) || (TrigPower > GV.MAX_TRIG_POWER)) {
											// input illegal
											Trig.setText("");
											Trig.setHint("输入值无效！");
											Trig.setHintTextColor(Color.rgb(255, 0, 0));
											TrigPowerCheck = 0;
										} else {
											// input legal
											// deal with input
											PowerTrig = TrigPower;
											TrigPowerCheck = 1;
											// close dialog
											try {
												Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
												field.setAccessible(true);
												field.set(dialog, true);
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								}).create();
				PowertrigDialog.show();
			}
		});
		
		SetReceiverNext.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// check input again
				if(CenterFreCheck + SampleCheck + MGCCheck + TrigPowerCheck + TrigTimeCheck == 4){
					// input is complete and all legal
					// store data
					TunerWorkGroup Group = GV.SysUser.getWorkGroup();
					Group.getParameter().setCenterFreq(CenterFre);
					Group.getParameter().setBandWidth(BandWidth);
					Group.getParameter().setIQNum(Sample);
					Group.getParameter().setMGC((byte) MGC);
					Group.getParameter().setTrigMode(TrigModeChoose);
					// power trig
					if(TrigPowerCheck == 1){
						Group.getParameter().setTrigPower(PowerTrig);
					}
					// time trig
					if(TrigTimeCheck == 1){
						DateTime TrigTime = new DateTime(wYear, wMonth, wDay, wHour, wMinute);
						Group.getParameter().setTrigTime(TrigTime);
					}
					
					// generate confirm string
					TunerWorkParameter ShowParameter = GV.SysUser.getWorkGroup().getParameter();
					LocationRegion ShowRegion = ShowParameter.getLocationRegion();
					
					String show = new String();
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
						show = show + "注意：为给服务器足够时间配置接收机，请保证完成余下配置后离触发时间仍有"+GV.TIME_FOR_SERVER
								+"分钟。\n";
					}
					else{
						// power trigger
						show = show + "触发电平： " + ShowParameter.getTrigPower() + "dBμV\n";
					}
					
					// 弹出确认框
					Dialog AffirmDialog = new AlertDialog.Builder(LocationParameterActivity.this)
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
							// send data to server
							final ProgressDialog SendMessage = new ProgressDialog(LocationParameterActivity.this);
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
							Communicator LocaPara_cator = new Communicator(sendhandler);
							LocaPara_cator.sendInstruction(GV, FRAME_TYPE.SET_PARAMETER);
							
							// jump to next activity
							Intent _intent = new Intent(LocationParameterActivity.this, 
									ReceiverChooseActivity.class);
							startActivity(_intent);
						}
					}).create();
					AffirmDialog.show();
					
				}
				else{
					// input is incomplete or not all legal
					Toast.makeText(getApplicationContext(), 
							"输入值缺省或无效，请检查您的输入！\n"
							,Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {  
        	Intent _intent = new Intent(LocationParameterActivity.this, RegionChooseActivity.class);
			startActivity(_intent);
        }
        return false;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location_parameter, menu);
		return true;
	}
	
	@Override  
	 public boolean onOptionsItemSelected(MenuItem item) {  
		 // TODO Auto-generated method stub  
		 switch(item.getItemId()){
		 case R.id.action_settings:
			 Intent _intent = new Intent(LocationParameterActivity.this, SettingActivity.class);
			 startActivity(_intent);
			 return true;
		 default:
			 break;
		 }
		return false;
	 }

}
