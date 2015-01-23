package com.qingshuimonk.tdoaclient;

import java.lang.reflect.Field;
import java.util.Calendar;

import android.annotation.SuppressLint;
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
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.qingshuimonk.tdoaclient.data_structrue.DateTime;
import com.qingshuimonk.tdoaclient.data_structrue.LocationRegion;
import com.qingshuimonk.tdoaclient.data_structrue.TunerWorkGroup;
import com.qingshuimonk.tdoaclient.data_structrue.TunerWorkParameter;
import com.qingshuimonk.tdoaclient.utils.Communicator;
import com.qingshuimonk.tdoaclient.utils.SysApplication;
import com.qingshuimonk.tdoaclient.utils.FrameFormer.FRAME_TYPE;


/***
 * 本activity用于定义参数设置界面
 * 配套xml文件: activity_location_parameter.xml
 * 功能:		
 * 	1.引导用户完成定位参数的输入和设置;
 * 	2.将输入的定位参数传输到服务器;
 * @author Huang Bohao
 * @version 1.0.0
 * @since 2014.11.13
 *
 */
public class LocationParameterActivity extends Activity {
	
	// 变量
	private int CenterFreUnit = 0;				// 中心频率单位(M or G)
	private long CenterFre;					// 中心频率基数
	private int BandWidth = 1000;
	private byte Sample;						// 样本数
	private int MGC;							// 手动增益
	private short PowerTrig;					// 能量触发值
	private int CenterFreCheck = 0;			// 中心频率输入值合法标志位
	private int SampleCheck = 0;				// 样本数输入值合法标志位
	private int MGCCheck = 0;					// 手动增益输入值合法标志位
	private int TrigPowerCheck = 0;			// 能量触发输入值合法标志位
	private int TrigTimeCheck = 0;				// 时间触发输入值合法标志位
	private byte TrigModeChoose;				// 触发方式(0:时间触发，1:能量触发)
	private short wYear;						
	private short wMonth;
	private short wDay;
	private short wHour;
	private short wMinute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_parameter);
		
		// 将此activity添加到SysApplication类中
		SysApplication.getInstance().addActivity(this);
		
		// 获取GlobalVariable类的全局变量
		final GlobalVariable GV = (GlobalVariable)getApplicationContext(); 
		
		// 获取控件
		final EditText InputCenterFre = (EditText) findViewById(R.id.inputcenterfre);
		final EditText InputSample = (EditText) findViewById(R.id.inputsample);
		final EditText InputMGC = (EditText) findViewById(R.id.inputmgc);
		final RadioButton TrigModeTime = (RadioButton) findViewById(R.id.timetrig);
		final RadioButton TrigModePower = (RadioButton) findViewById(R.id.powertrig);
		final Spinner InputcenterFreSpinner = (Spinner) findViewById(R.id.inputcenterfrespinner);
		final Spinner InputBandWidth = (Spinner) findViewById(R.id.inputbandwidth);
		final Button SetReceiverNext = (Button) findViewById(R.id.setreceivernext);
		
		// 创建action bar
		ActionBar LoginActionBar = this.getActionBar();
		LoginActionBar.setDisplayShowTitleEnabled(true); // ActionBar只显示title
		LoginActionBar.setDisplayShowHomeEnabled(false);
		
		// 中心频率单位spinner的设置
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.hertzunit, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		InputcenterFreSpinner.setAdapter(adapter);
		// 带宽spinner的设置
		ArrayAdapter<CharSequence> bdadapter = ArrayAdapter.createFromResource(
				this, R.array.bdunit, android.R.layout.simple_spinner_item);
		bdadapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		InputBandWidth.setAdapter(bdadapter);

		// 中心频率单位键值改变监听
		InputcenterFreSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						CenterFreUnit = position;
						// 再次检查输入值是否改变
						try {
							// 检查输入值合法性
							String input = InputCenterFre.getText().toString().trim();
							Float temp = Float.parseFloat(input);
							String[] sArray = input.split("\\.");

							if ((temp * Math.pow(1000, CenterFreUnit + 2) > GV.MAX_CENTER_FRE)
									|| (temp * Math.pow(1000, CenterFreUnit + 2) < GV.MIN_CENTER_FRE)) {
								// 输入值不合法
								InputCenterFre.setTextColor(Color.rgb(255, 0, 0));	// 文本框标记为红色
								CenterFreCheck = 0;									// 输入值非法
							} else {
								// 输入值合法
								InputCenterFre.setTextColor(Color.rgb(0, 0, 0));	// 文本框标记为黑色	
								if (CenterFreUnit == 0)								// 单位: MHz
									CenterFre = (long) ((double) temp * Math.pow(1000, CenterFreUnit + 2));
								else {
									// 单位: GHz
									int intpart = Integer.parseInt(sArray[0]);
									int decimalpart = Integer.parseInt(sArray[1]);
									CenterFre = (long) (intpart * (long) Math.pow(1000,CenterFreUnit + 2) + decimalpart
											* (long) Math.pow(1000,CenterFreUnit + 2)/ (long) Math.pow(10,sArray[1].length()));
								}
								CenterFreCheck = 1;									// 输入值合法
							}
						} catch (Exception e) {
							// 异常处理
							// 还未输入值 
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO 中心频率无值选中处理函数
					}
				});

		// 带宽值选择监听
		InputBandWidth.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				// 选择不同item对应设置对应的带宽
				case 0:BandWidth = 1000;break;
				case 1:BandWidth = 3000;break;
				case 2:BandWidth = 5000;break;
				case 3:BandWidth = 10000;break;
				case 4:BandWidth = 12500;break;
				case 5:BandWidth = 25000;break;
				case 6:BandWidth = 30000;break;
				case 7:BandWidth = 50000;break;
				case 8:BandWidth = 100000;break;
				case 9:BandWidth = 150000;break;
				case 10:BandWidth = 200000;break;
				case 11:BandWidth = 500000;break;
				case 12:BandWidth = 1000000;break;
				case 13:BandWidth = 2000000;break;
				case 14:BandWidth = 5000000;break;
				case 15:BandWidth = 10000000;break;
				case 16:BandWidth = 20000000;break;
				default:BandWidth = 0;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO 带宽没有值选中处理函数
			}
		});

		// 中心频率基数输入监听
		TextWatcher InputCenterFreWatcher = new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO 中心频率基数值改变后处理函数
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO 中心频率基数值改变前处理函数

			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String input = s.toString().trim();
				try {
					// 检查输入频率是否合法
					Float temp = Float.parseFloat(input);
					String[] sArray = input.split("\\.");
					if ((temp * Math.pow(1000, CenterFreUnit + 2) > GV.MAX_CENTER_FRE)
							|| (temp * Math.pow(1000, CenterFreUnit + 2) < GV.MIN_CENTER_FRE)) {
						// 输入不合法
						InputCenterFre.setTextColor(Color.rgb(255, 0, 0));	// 文本框设为红色
						CenterFreCheck = 0;									// 输入非法
					} else {
						// 输入值合法
						InputCenterFre.setTextColor(Color.rgb(0, 0, 0));	// 文本框设为黑色
						if (CenterFreUnit == 0)								// 单位: MHz
							CenterFre = (long) ((double) temp * Math.pow(1000, CenterFreUnit + 2));
						else {												// 单位: GHz
							int intpart = Integer.parseInt(sArray[0]);
							int decimalpart = Integer.parseInt(sArray[1]);
							CenterFre = (long) (intpart * (long) Math.pow(1000, CenterFreUnit + 2) + decimalpart
									* (long) Math.pow(1000, CenterFreUnit + 2) / (long) Math.pow(10, 
											sArray[1].length()));
						}
						CenterFreCheck = 1;									// 输入合法
					}
				} catch (Exception e) {
					// 异常处理
					// 没有输入值
				}
			}
		};
		
		// 输入样本数值监听函数
		TextWatcher InputSampleWatcher = new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				// TODO 输入样本数值改变后处理函数
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO 输入样本数值改变前处理函数
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String input = s.toString().trim();
				try {
					// 检查输入值是否合法
					byte temp = Byte.parseByte(input);
					if ((temp > GV.MAX_IQ_NUM) || (temp < GV.MIN_IQ_NUM)) {
						// 输入值非法
						InputSample.setTextColor(Color.rgb(255, 0, 0));		// 文本框为红色
						SampleCheck = 0;									// 输入值非法
					} else {												// 输入值合法
						InputSample.setTextColor(Color.rgb(0, 0, 0));		// 文本框为黑色
						Sample = temp;	
						SampleCheck = 1;									// 输入值合法
					}
				} catch (Exception e) {
					// 异常处理
					// 没有输入值
				}
			}
		};
		
		// 手动增益输入值监听
		TextWatcher InputMGCWatcher = new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				// TODO 手动增益值输入前处理函数
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO 手动增益值输入后处理函数
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String input = s.toString().trim();
				try {
					// 检查MGC输入值是否合法
					int temp = Integer.parseInt(input);
					if ((temp > GV.MAX_MGC) || (temp < GV.MIN_MGC)) {	// 输入值非法
						InputMGC.setTextColor(Color.rgb(255, 0, 0));	// 文本框为红色
						MGCCheck = 0;									// 输入值非法
					} else {											// 输入值合法
						InputMGC.setTextColor(Color.rgb(0, 0, 0));		// 文本框黑色
						MGC = temp;
						MGCCheck = 1;									// 输入值合法
					}
				} catch (Exception e) {
					// 异常处理
					// 没有输入值
				}
			}
		};

		// 对各控件添加监听
		InputCenterFre.addTextChangedListener(InputCenterFreWatcher);
		InputSample.addTextChangedListener(InputSampleWatcher);
		InputMGC.addTextChangedListener(InputMGCWatcher);
		
		// 时间触发监听函数
		TrigModeTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// timer trigger is chosen
				TrigPowerCheck = 0;				// 时间触发标志位置0
				TrigModeChoose = 0;				// 设置为时间触发
				
				// 设置弹出界面
				LayoutInflater inflater = (LayoutInflater) LocationParameterActivity.this
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				final View layout = inflater.inflate(R.layout.timetrig_dialog,
						(ViewGroup) findViewById(R.id.timetrigdialog));
				AlertDialog TimetrigDialog = new AlertDialog.Builder(
						LocationParameterActivity.this)
						.setView(layout).setIcon(android.R.drawable.ic_dialog_info)
						.setTitle("请选择触发时间：").setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// 使对话框无法关闭
										try {
											Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
											field.setAccessible(true);
											field.set(dialog, false);
										} catch (Exception e) {
											e.printStackTrace();
										}
										// 关闭对话框
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
										// 使对话框无法关闭，避免用户未选择时间就退出
										try {
											Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
											field.setAccessible(true);
											field.set(dialog, false);
										} catch (Exception e) {
											e.printStackTrace();
										}
										// 处理输入的时间
										int year, month, day, hour, minute;
										DatePicker TimetrigDatepicker = (DatePicker) layout.findViewById(R.id.timetrigdatePicker);
										TimePicker TimetrigTimepicker = (TimePicker) layout.findViewById(R.id.timetrigtimePicker);

										int TrigYear, TrigMonth, TrigDay;
										TrigYear = TimetrigDatepicker.getYear();
										TrigMonth = TimetrigDatepicker.getMonth() + 1;		// 月份从0开始，所以要+1
										TrigDay = TimetrigDatepicker.getDayOfMonth();

										int TrigHour, TrigMinute;
										TrigHour = Integer.parseInt(TimetrigTimepicker.getCurrentHour().toString().trim());
										TrigMinute = Integer.parseInt(TimetrigTimepicker.getCurrentMinute().toString().trim());

										// 获取系统时间
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
										
										// 判断时间是否合法
										if (currentTime + GV.TIME_FOR_SET > setTime) {
											// 时间不合法
											Toast.makeText(
													getApplicationContext(),
													"输入时间错误，请重新输入！\n注意：请不要设置"+GV.TIME_FOR_SET+"分钟之内的触发时间。",
													Toast.LENGTH_SHORT).show();
											TrigTimeCheck = 0;			// 时间合法位置0
										} else {
											// 时间输入合法
											TrigTimeCheck = 1;
											wYear = (short) TrigYear;
											wMonth = (short) TrigMonth;
											wDay = (short) TrigDay;
											wHour = (short) TrigHour;
											wMinute = (short) TrigMinute;
											// 关闭对话框
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

				TimetrigDialog.show();
			}
		});
		
		// 能量触发监听函数
		TrigModePower.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 能量触发
				TrigPowerCheck = 0;			// 能量触发置0		
				TrigModeChoose = 1;			// 触发模式为能量触发
				
				// 设置弹出界面
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
										// 使对话框无法关闭
										try {
											Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
											field.setAccessible(true);
											field.set(dialog, false);
										} catch (Exception e) {
											e.printStackTrace();
										}
										// 关闭对话框
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
										// 使对话框无法关闭，避免用户未输入阈值就退出
										try {
											Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
											field.setAccessible(true);
											field.set(dialog, false);
										} catch (Exception e) {
											e.printStackTrace();
										}

										// 获取输入值
										Short TrigPower;
										EditText Trig = (EditText) layout.findViewById(R.id.powertrigdialoginput);
										try {
											TrigPower = Short.parseShort(Trig.getText().toString().trim());
										} catch (Exception e) {
											// 若无输入值就默认为最小触发阈值
											TrigPower = (short) GV.MIN_TRIG_POWER;
										}

										// 检查输入是否合法
										if ((TrigPower < GV.MIN_TRIG_POWER) || (TrigPower > GV.MAX_TRIG_POWER)) {
											// 输入非法
											Trig.setText("");
											Trig.setHint("输入值无效！");
											Trig.setHintTextColor(Color.rgb(255, 0, 0));
											TrigPowerCheck = 0;
										} else {
											// 输入合法
											// 处理输入值
											PowerTrig = TrigPower;
											TrigPowerCheck = 1;
											// 关闭对话框
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
		
		// “下一步”按键监听函数
		SetReceiverNext.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// 检查参数输入是否都合法
				if(CenterFreCheck + SampleCheck + MGCCheck + TrigPowerCheck + TrigTimeCheck == 4){
					// 参数输入完全且合法
					// 存储数据
					TunerWorkGroup Group = GV.SysUser.getWorkGroup();		// 获取TunerWorkGroup引用
					Group.getParameter().setCenterFreq(CenterFre);			
					Group.getParameter().setBandWidth(BandWidth);
					Group.getParameter().setIQNum(Sample);
					Group.getParameter().setMGC((byte) MGC);
					Group.getParameter().setTrigMode(TrigModeChoose);
					// 能量触发
					if(TrigPowerCheck == 1){
						Group.getParameter().setTrigPower(PowerTrig);
					}
					// 时间触发
					if(TrigTimeCheck == 1){
						DateTime TrigTime = new DateTime(wYear, wMonth, wDay, wHour, wMinute);
						Group.getParameter().setTrigTime(TrigTime);
					}
					
					// 生成确认对话框显示内容
					TunerWorkParameter ShowParameter = GV.SysUser.getWorkGroup().getParameter();
					LocationRegion ShowRegion = ShowParameter.getLocationRegion();
					
					String show = new String();
					show = "监控区域类型： ";
					if(ShowRegion.getRegionMode() == 0){
						// 矩形监控区域
						show = show + "矩形\n左上角点坐标： \n经度:" + ShowRegion.getRegionValue1()
								+"° \n纬度:" + ShowRegion.getRegionValue2() + 
								"°\n右下角点坐标： \n经度:" + ShowRegion.getRegionValue3() + 
								"° \n纬度:" + ShowRegion.getRegionValue4() + "°\n";
					}
					else{
						// 圆形监控区域
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
						show = show + "注意：为给服务器足够时间配置接收机，请保证完成余下配置后离触发时间仍有"+GV.TIME_FOR_SERVER
								+"分钟。\n";
					}
					else{
						// 能量触发
						show = show + "触发电平： " + ShowParameter.getTrigPower() + "dBμV\n";
					}
					
					// 弹出确认框
					Dialog AffirmDialog = new AlertDialog.Builder(LocationParameterActivity.this)
					.setIcon(android.R.drawable.ic_dialog_info).setTitle("请确认配置参数:")
					.setMessage(show)
					.setNegativeButton("取消", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO 取消确认配置参数处理函数
							
						}
					})
					.setPositiveButton("确定", new DialogInterface.OnClickListener(){
						@SuppressLint("HandlerLeak")
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 向服务器传输数据
							
							// 配置process dialog
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
										// 关闭对话框
										SendMessage.dismiss();
										}
								}     
							}; 
							// 调用Communicator工具类
							Communicator LocaPara_cator = new Communicator(sendhandler);
							LocaPara_cator.sendInstruction(GV, FRAME_TYPE.SET_PARAMETER);		// 解析指令帧
							
							// 跳转至下一activity
							Intent _intent = new Intent(LocationParameterActivity.this, 
									ReceiverChooseActivity.class);
							startActivity(_intent);
						}
					}).create();
					AffirmDialog.show();
					
				}
				else{
					// 输入值非法或不完全
					Toast.makeText(getApplicationContext(), "输入值缺省或无效，请检查您的输入！\n",Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	// 获取返回键点击事件
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {  
        	// 返回上一activity
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
	
	// 获取"设置"按键事件
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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
