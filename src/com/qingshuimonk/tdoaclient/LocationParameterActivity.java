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
 * ��activity���ڶ���������ý���
 * ����xml�ļ�: activity_location_parameter.xml
 * ����:		
 * 	1.�����û���ɶ�λ���������������;
 * 	2.������Ķ�λ�������䵽������;
 * @author Huang Bohao
 * @version 1.0.0
 * @since 2014.11.13
 *
 */
public class LocationParameterActivity extends Activity {
	
	// ����
	private int CenterFreUnit = 0;				// ����Ƶ�ʵ�λ(M or G)
	private long CenterFre;					// ����Ƶ�ʻ���
	private int BandWidth = 1000;
	private byte Sample;						// ������
	private int MGC;							// �ֶ�����
	private short PowerTrig;					// ��������ֵ
	private int CenterFreCheck = 0;			// ����Ƶ������ֵ�Ϸ���־λ
	private int SampleCheck = 0;				// ����������ֵ�Ϸ���־λ
	private int MGCCheck = 0;					// �ֶ���������ֵ�Ϸ���־λ
	private int TrigPowerCheck = 0;			// ������������ֵ�Ϸ���־λ
	private int TrigTimeCheck = 0;				// ʱ�䴥������ֵ�Ϸ���־λ
	private byte TrigModeChoose;				// ������ʽ(0:ʱ�䴥����1:��������)
	private short wYear;						
	private short wMonth;
	private short wDay;
	private short wHour;
	private short wMinute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_parameter);
		
		// ����activity��ӵ�SysApplication����
		SysApplication.getInstance().addActivity(this);
		
		// ��ȡGlobalVariable���ȫ�ֱ���
		final GlobalVariable GV = (GlobalVariable)getApplicationContext(); 
		
		// ��ȡ�ؼ�
		final EditText InputCenterFre = (EditText) findViewById(R.id.inputcenterfre);
		final EditText InputSample = (EditText) findViewById(R.id.inputsample);
		final EditText InputMGC = (EditText) findViewById(R.id.inputmgc);
		final RadioButton TrigModeTime = (RadioButton) findViewById(R.id.timetrig);
		final RadioButton TrigModePower = (RadioButton) findViewById(R.id.powertrig);
		final Spinner InputcenterFreSpinner = (Spinner) findViewById(R.id.inputcenterfrespinner);
		final Spinner InputBandWidth = (Spinner) findViewById(R.id.inputbandwidth);
		final Button SetReceiverNext = (Button) findViewById(R.id.setreceivernext);
		
		// ����action bar
		ActionBar LoginActionBar = this.getActionBar();
		LoginActionBar.setDisplayShowTitleEnabled(true); // ActionBarֻ��ʾtitle
		LoginActionBar.setDisplayShowHomeEnabled(false);
		
		// ����Ƶ�ʵ�λspinner������
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.hertzunit, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		InputcenterFreSpinner.setAdapter(adapter);
		// ����spinner������
		ArrayAdapter<CharSequence> bdadapter = ArrayAdapter.createFromResource(
				this, R.array.bdunit, android.R.layout.simple_spinner_item);
		bdadapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		InputBandWidth.setAdapter(bdadapter);

		// ����Ƶ�ʵ�λ��ֵ�ı����
		InputcenterFreSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						CenterFreUnit = position;
						// �ٴμ������ֵ�Ƿ�ı�
						try {
							// �������ֵ�Ϸ���
							String input = InputCenterFre.getText().toString().trim();
							Float temp = Float.parseFloat(input);
							String[] sArray = input.split("\\.");

							if ((temp * Math.pow(1000, CenterFreUnit + 2) > GV.MAX_CENTER_FRE)
									|| (temp * Math.pow(1000, CenterFreUnit + 2) < GV.MIN_CENTER_FRE)) {
								// ����ֵ���Ϸ�
								InputCenterFre.setTextColor(Color.rgb(255, 0, 0));	// �ı�����Ϊ��ɫ
								CenterFreCheck = 0;									// ����ֵ�Ƿ�
							} else {
								// ����ֵ�Ϸ�
								InputCenterFre.setTextColor(Color.rgb(0, 0, 0));	// �ı�����Ϊ��ɫ	
								if (CenterFreUnit == 0)								// ��λ: MHz
									CenterFre = (long) ((double) temp * Math.pow(1000, CenterFreUnit + 2));
								else {
									// ��λ: GHz
									int intpart = Integer.parseInt(sArray[0]);
									int decimalpart = Integer.parseInt(sArray[1]);
									CenterFre = (long) (intpart * (long) Math.pow(1000,CenterFreUnit + 2) + decimalpart
											* (long) Math.pow(1000,CenterFreUnit + 2)/ (long) Math.pow(10,sArray[1].length()));
								}
								CenterFreCheck = 1;									// ����ֵ�Ϸ�
							}
						} catch (Exception e) {
							// �쳣����
							// ��δ����ֵ 
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO ����Ƶ����ֵѡ�д�����
					}
				});

		// ����ֵѡ�����
		InputBandWidth.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				// ѡ��ͬitem��Ӧ���ö�Ӧ�Ĵ���
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
				// TODO ����û��ֵѡ�д�����
			}
		});

		// ����Ƶ�ʻ����������
		TextWatcher InputCenterFreWatcher = new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO ����Ƶ�ʻ���ֵ�ı������
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO ����Ƶ�ʻ���ֵ�ı�ǰ������

			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String input = s.toString().trim();
				try {
					// �������Ƶ���Ƿ�Ϸ�
					Float temp = Float.parseFloat(input);
					String[] sArray = input.split("\\.");
					if ((temp * Math.pow(1000, CenterFreUnit + 2) > GV.MAX_CENTER_FRE)
							|| (temp * Math.pow(1000, CenterFreUnit + 2) < GV.MIN_CENTER_FRE)) {
						// ���벻�Ϸ�
						InputCenterFre.setTextColor(Color.rgb(255, 0, 0));	// �ı�����Ϊ��ɫ
						CenterFreCheck = 0;									// ����Ƿ�
					} else {
						// ����ֵ�Ϸ�
						InputCenterFre.setTextColor(Color.rgb(0, 0, 0));	// �ı�����Ϊ��ɫ
						if (CenterFreUnit == 0)								// ��λ: MHz
							CenterFre = (long) ((double) temp * Math.pow(1000, CenterFreUnit + 2));
						else {												// ��λ: GHz
							int intpart = Integer.parseInt(sArray[0]);
							int decimalpart = Integer.parseInt(sArray[1]);
							CenterFre = (long) (intpart * (long) Math.pow(1000, CenterFreUnit + 2) + decimalpart
									* (long) Math.pow(1000, CenterFreUnit + 2) / (long) Math.pow(10, 
											sArray[1].length()));
						}
						CenterFreCheck = 1;									// ����Ϸ�
					}
				} catch (Exception e) {
					// �쳣����
					// û������ֵ
				}
			}
		};
		
		// ����������ֵ��������
		TextWatcher InputSampleWatcher = new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				// TODO ����������ֵ�ı������
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO ����������ֵ�ı�ǰ������
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String input = s.toString().trim();
				try {
					// �������ֵ�Ƿ�Ϸ�
					byte temp = Byte.parseByte(input);
					if ((temp > GV.MAX_IQ_NUM) || (temp < GV.MIN_IQ_NUM)) {
						// ����ֵ�Ƿ�
						InputSample.setTextColor(Color.rgb(255, 0, 0));		// �ı���Ϊ��ɫ
						SampleCheck = 0;									// ����ֵ�Ƿ�
					} else {												// ����ֵ�Ϸ�
						InputSample.setTextColor(Color.rgb(0, 0, 0));		// �ı���Ϊ��ɫ
						Sample = temp;	
						SampleCheck = 1;									// ����ֵ�Ϸ�
					}
				} catch (Exception e) {
					// �쳣����
					// û������ֵ
				}
			}
		};
		
		// �ֶ���������ֵ����
		TextWatcher InputMGCWatcher = new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				// TODO �ֶ�����ֵ����ǰ������
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO �ֶ�����ֵ���������
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String input = s.toString().trim();
				try {
					// ���MGC����ֵ�Ƿ�Ϸ�
					int temp = Integer.parseInt(input);
					if ((temp > GV.MAX_MGC) || (temp < GV.MIN_MGC)) {	// ����ֵ�Ƿ�
						InputMGC.setTextColor(Color.rgb(255, 0, 0));	// �ı���Ϊ��ɫ
						MGCCheck = 0;									// ����ֵ�Ƿ�
					} else {											// ����ֵ�Ϸ�
						InputMGC.setTextColor(Color.rgb(0, 0, 0));		// �ı����ɫ
						MGC = temp;
						MGCCheck = 1;									// ����ֵ�Ϸ�
					}
				} catch (Exception e) {
					// �쳣����
					// û������ֵ
				}
			}
		};

		// �Ը��ؼ���Ӽ���
		InputCenterFre.addTextChangedListener(InputCenterFreWatcher);
		InputSample.addTextChangedListener(InputSampleWatcher);
		InputMGC.addTextChangedListener(InputMGCWatcher);
		
		// ʱ�䴥����������
		TrigModeTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// timer trigger is chosen
				TrigPowerCheck = 0;				// ʱ�䴥����־λ��0
				TrigModeChoose = 0;				// ����Ϊʱ�䴥��
				
				// ���õ�������
				LayoutInflater inflater = (LayoutInflater) LocationParameterActivity.this
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				final View layout = inflater.inflate(R.layout.timetrig_dialog,
						(ViewGroup) findViewById(R.id.timetrigdialog));
				AlertDialog TimetrigDialog = new AlertDialog.Builder(
						LocationParameterActivity.this)
						.setView(layout).setIcon(android.R.drawable.ic_dialog_info)
						.setTitle("��ѡ�񴥷�ʱ�䣺").setNegativeButton("ȡ��",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// ʹ�Ի����޷��ر�
										try {
											Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
											field.setAccessible(true);
											field.set(dialog, false);
										} catch (Exception e) {
											e.printStackTrace();
										}
										// �رնԻ���
										try {
											Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
											field.setAccessible(true);
											field.set(dialog, true);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								})
						.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// ʹ�Ի����޷��رգ������û�δѡ��ʱ����˳�
										try {
											Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
											field.setAccessible(true);
											field.set(dialog, false);
										} catch (Exception e) {
											e.printStackTrace();
										}
										// ���������ʱ��
										int year, month, day, hour, minute;
										DatePicker TimetrigDatepicker = (DatePicker) layout.findViewById(R.id.timetrigdatePicker);
										TimePicker TimetrigTimepicker = (TimePicker) layout.findViewById(R.id.timetrigtimePicker);

										int TrigYear, TrigMonth, TrigDay;
										TrigYear = TimetrigDatepicker.getYear();
										TrigMonth = TimetrigDatepicker.getMonth() + 1;		// �·ݴ�0��ʼ������Ҫ+1
										TrigDay = TimetrigDatepicker.getDayOfMonth();

										int TrigHour, TrigMinute;
										TrigHour = Integer.parseInt(TimetrigTimepicker.getCurrentHour().toString().trim());
										TrigMinute = Integer.parseInt(TimetrigTimepicker.getCurrentMinute().toString().trim());

										// ��ȡϵͳʱ��
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
										
										// �ж�ʱ���Ƿ�Ϸ�
										if (currentTime + GV.TIME_FOR_SET > setTime) {
											// ʱ�䲻�Ϸ�
											Toast.makeText(
													getApplicationContext(),
													"����ʱ��������������룡\nע�⣺�벻Ҫ����"+GV.TIME_FOR_SET+"����֮�ڵĴ���ʱ�䡣",
													Toast.LENGTH_SHORT).show();
											TrigTimeCheck = 0;			// ʱ��Ϸ�λ��0
										} else {
											// ʱ������Ϸ�
											TrigTimeCheck = 1;
											wYear = (short) TrigYear;
											wMonth = (short) TrigMonth;
											wDay = (short) TrigDay;
											wHour = (short) TrigHour;
											wMinute = (short) TrigMinute;
											// �رնԻ���
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
		
		// ����������������
		TrigModePower.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ��������
				TrigPowerCheck = 0;			// ����������0		
				TrigModeChoose = 1;			// ����ģʽΪ��������
				
				// ���õ�������
				LayoutInflater inflater = (LayoutInflater) LocationParameterActivity.this
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				final View layout = inflater.inflate(R.layout.powertrig_dialog,
						(ViewGroup) findViewById(R.id.powertrigdialog));
				AlertDialog PowertrigDialog = new AlertDialog.Builder(
						LocationParameterActivity.this).setView(layout).setIcon(android.R.drawable.ic_dialog_info)
						.setTitle("����������������ֵ��").setNegativeButton("ȡ��",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// ʹ�Ի����޷��ر�
										try {
											Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
											field.setAccessible(true);
											field.set(dialog, false);
										} catch (Exception e) {
											e.printStackTrace();
										}
										// �رնԻ���
										try {
											Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
											field.setAccessible(true);
											field.set(dialog, true);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								})
						.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// ʹ�Ի����޷��رգ������û�δ������ֵ���˳�
										try {
											Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
											field.setAccessible(true);
											field.set(dialog, false);
										} catch (Exception e) {
											e.printStackTrace();
										}

										// ��ȡ����ֵ
										Short TrigPower;
										EditText Trig = (EditText) layout.findViewById(R.id.powertrigdialoginput);
										try {
											TrigPower = Short.parseShort(Trig.getText().toString().trim());
										} catch (Exception e) {
											// ��������ֵ��Ĭ��Ϊ��С������ֵ
											TrigPower = (short) GV.MIN_TRIG_POWER;
										}

										// ��������Ƿ�Ϸ�
										if ((TrigPower < GV.MIN_TRIG_POWER) || (TrigPower > GV.MAX_TRIG_POWER)) {
											// ����Ƿ�
											Trig.setText("");
											Trig.setHint("����ֵ��Ч��");
											Trig.setHintTextColor(Color.rgb(255, 0, 0));
											TrigPowerCheck = 0;
										} else {
											// ����Ϸ�
											// ��������ֵ
											PowerTrig = TrigPower;
											TrigPowerCheck = 1;
											// �رնԻ���
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
		
		// ����һ����������������
		SetReceiverNext.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// �����������Ƿ񶼺Ϸ�
				if(CenterFreCheck + SampleCheck + MGCCheck + TrigPowerCheck + TrigTimeCheck == 4){
					// ����������ȫ�ҺϷ�
					// �洢����
					TunerWorkGroup Group = GV.SysUser.getWorkGroup();		// ��ȡTunerWorkGroup����
					Group.getParameter().setCenterFreq(CenterFre);			
					Group.getParameter().setBandWidth(BandWidth);
					Group.getParameter().setIQNum(Sample);
					Group.getParameter().setMGC((byte) MGC);
					Group.getParameter().setTrigMode(TrigModeChoose);
					// ��������
					if(TrigPowerCheck == 1){
						Group.getParameter().setTrigPower(PowerTrig);
					}
					// ʱ�䴥��
					if(TrigTimeCheck == 1){
						DateTime TrigTime = new DateTime(wYear, wMonth, wDay, wHour, wMinute);
						Group.getParameter().setTrigTime(TrigTime);
					}
					
					// ����ȷ�϶Ի�����ʾ����
					TunerWorkParameter ShowParameter = GV.SysUser.getWorkGroup().getParameter();
					LocationRegion ShowRegion = ShowParameter.getLocationRegion();
					
					String show = new String();
					show = "����������ͣ� ";
					if(ShowRegion.getRegionMode() == 0){
						// ���μ������
						show = show + "����\n���Ͻǵ����꣺ \n����:" + ShowRegion.getRegionValue1()
								+"�� \nγ��:" + ShowRegion.getRegionValue2() + 
								"��\n���½ǵ����꣺ \n����:" + ShowRegion.getRegionValue3() + 
								"�� \nγ��:" + ShowRegion.getRegionValue4() + "��\n";
					}
					else{
						// Բ�μ������
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
						show = show + "ע�⣺Ϊ���������㹻ʱ�����ý��ջ����뱣֤����������ú��봥��ʱ������"+GV.TIME_FOR_SERVER
								+"���ӡ�\n";
					}
					else{
						// ��������
						show = show + "������ƽ�� " + ShowParameter.getTrigPower() + "dB��V\n";
					}
					
					// ����ȷ�Ͽ�
					Dialog AffirmDialog = new AlertDialog.Builder(LocationParameterActivity.this)
					.setIcon(android.R.drawable.ic_dialog_info).setTitle("��ȷ�����ò���:")
					.setMessage(show)
					.setNegativeButton("ȡ��", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO ȡ��ȷ�����ò���������
							
						}
					})
					.setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
						@SuppressLint("HandlerLeak")
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// ���������������
							
							// ����process dialog
							final ProgressDialog SendMessage = new ProgressDialog(LocationParameterActivity.this);
							SendMessage.setProgressStyle(ProgressDialog.STYLE_SPINNER);
							SendMessage.setMessage("�ȴ�����������Ͳ���");
							SendMessage.setCancelable(false);
							SendMessage.show();
							// set handler
							final Handler sendhandler = new Handler() {  
								@Override
								public void handleMessage(Message msg) {
									super.handleMessage(msg);
									if(msg.what == 0){
										// �رնԻ���
										SendMessage.dismiss();
										}
								}     
							}; 
							// ����Communicator������
							Communicator LocaPara_cator = new Communicator(sendhandler);
							LocaPara_cator.sendInstruction(GV, FRAME_TYPE.SET_PARAMETER);		// ����ָ��֡
							
							// ��ת����һactivity
							Intent _intent = new Intent(LocationParameterActivity.this, 
									ReceiverChooseActivity.class);
							startActivity(_intent);
						}
					}).create();
					AffirmDialog.show();
					
				}
				else{
					// ����ֵ�Ƿ�����ȫ
					Toast.makeText(getApplicationContext(), "����ֵȱʡ����Ч�������������룡\n",Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	// ��ȡ���ؼ�����¼�
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {  
        	// ������һactivity
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
	
	// ��ȡ"����"�����¼�
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
