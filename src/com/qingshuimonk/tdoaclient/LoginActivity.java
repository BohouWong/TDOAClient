package com.qingshuimonk.tdoaclient;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qingshuimonk.tdoaclient.data_structrue.LocationRegion;
import com.qingshuimonk.tdoaclient.data_structrue.TunerWorkGroup;
import com.qingshuimonk.tdoaclient.data_structrue.TunerWorkParameter;
import com.qingshuimonk.tdoaclient.utils.Communicator;
import com.qingshuimonk.tdoaclient.utils.SysApplication;
import com.qingshuimonk.tdoaclient.utils.FrameFormer.FRAME_TYPE;


/***
 * ��activity���ڶ����û���¼����
 * ����xml�ļ�: activity_login.xml
 * ����:		
 * 	1.�����û������˺����벢��¼ ;
 *  2.�ṩ�����һأ��Һ�ע�Ṧ�ܣ�
 * 	3.���û���¼�ɹ����˺����뼰IP��ַ���͸�������;
 * @author Huang Bohao
 * @version 1.0.0
 * @since 2014.11.11
 */
public class LoginActivity extends Activity {
	
	// �ؼ�
	EditText inputUserName;
	EditText inputPassWord;
	TextView forgetPassWord;
	TextView createNewAccount;
	Button Login;
	
	// �û���������
	String UserName, PassWord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		// ����activity��ӵ�SysApplication����
		SysApplication.getInstance().addActivity(this);

		// ��ȡGlobalVariable���ȫ�ֱ���
		final GlobalVariable GV = (GlobalVariable)getApplicationContext(); 
		
		// �ؼ�
		inputUserName = (EditText)findViewById(R.id.inputUserName);
		inputPassWord = (EditText)findViewById(R.id.inputPassWord);
		forgetPassWord = (TextView)findViewById(R.id.forgetPassWord);
		createNewAccount = (TextView)findViewById(R.id.createNewAccount);
		Login = (Button)findViewById(R.id.Login);
		
		// ��¼��������
		OnClickListener loginCheckListener;
		
		// ����action bar
		ActionBar LoginActionBar = this.getActionBar();
		LoginActionBar.setDisplayShowTitleEnabled(true);	// ActionBarֻ��ʾtitle
		LoginActionBar.setDisplayShowHomeEnabled(false);
		
		// ����sharedpreference�����ڱ����û���������
		final SharedPreferences AccountInfo = (SharedPreferences)this.getSharedPreferences("AccountInfo", MODE_PRIVATE);
		final SharedPreferences.Editor AccountEditor = AccountInfo.edit();
		
		// ����һ��������û���������
		// ֻ���ڵ���
		AccountEditor.putString("22", "22");
		AccountEditor.putString("22mail", "22@22.com");
		AccountEditor.commit();
		
		// ��¼������������
		loginCheckListener = new OnClickListener(){
			@Override
			public void onClick(View v) {
				try{
					UserName = inputUserName.getText().toString();
					PassWord = inputPassWord.getText().toString();
					// ��sharedpreferences��Ѱ�Ҷ�Ӧ�˺�
					String logincheck = AccountInfo.getString(UserName, "");
					
					// ����û����Ƿ����
					if((logincheck.trim().equals(""))){
						Dialog NoUserNameDialog = new AlertDialog.Builder(LoginActivity.this)
						.setIcon(android.R.drawable.ic_dialog_alert).setTitle("��½����")
						.setMessage("�޴��û������������롣")
						.setPositiveButton("ȷ��", null).setNegativeButton("ȡ��", null)
						.create();
						NoUserNameDialog.show();
						
						inputUserName.setText("");
						inputPassWord.setText("");
					}
					else{
						// ����˺������Ƿ����
						if(logincheck.trim().equals(PassWord)){
							// ���� User class
							GV.SysUser.setUserName(UserName);
							GV.SysUser.setPassWord(PassWord);
							
							// TODO �Զ��ж�������������
							if(GV.DEBUG_NETWORK_CONDITION){
								// Network���ӣ�ֱ��������һ��activity
								
								// ���˻�����IP��ַ��Ϣ���͵�������
								final ProgressDialog SendMessage = new ProgressDialog(LoginActivity.this);
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
								Communicator UserInfo_cator = new Communicator(sendhandler);
								UserInfo_cator.sendInstruction(GV, FRAME_TYPE.USER_INFO);
								
								// ��ת����һ��activity
								Intent _intent = new Intent(LoginActivity.this, RegionChooseActivity.class);
								startActivity(_intent);
							}
							else{
								// ��Network���ӣ��������ⶨλ�������ת��LocationParameterActivity
								LocationRegion Region = new LocationRegion((byte) 0,20.12345,20.12345,
										20.12345,20.12345);
								TunerWorkParameter Parameter = new TunerWorkParameter(Region);
								TunerWorkGroup Group = new TunerWorkGroup(Parameter);
								GV.SysUser.setWorkGroup(Group);
								Intent _intent = new Intent(LoginActivity.this, LocationParameterActivity.class);
								startActivity(_intent);
							}
							
							
						}
						// ���û������벻����
						else{
							// ��ʾ�������
							Dialog WrongPassword = new AlertDialog.Builder(LoginActivity.this).setIcon(R.drawable.ic_launcher)
									.setTitle("��½����").setMessage("��������������롣")
									.setNegativeButton("ȡ��", new DialogInterface.OnClickListener(){
										@Override
										public void onClick(DialogInterface dialog, int id) {
											inputPassWord.setText("");
										}
									}).setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
										public void onClick(DialogInterface dialog, int id){
											inputPassWord.setText("");
										}
									}).create();
							WrongPassword.show();
						}
					}
				}catch(Exception e){
					// �����쳣
				}
			}
		};
		
		// ��Ӽ�������
		Login.setOnClickListener(loginCheckListener);
		
		// ע���˺ż�������
		createNewAccount.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				LayoutInflater inflater = (LayoutInflater) LoginActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
				final View layout = inflater.inflate(R.layout.regist_dialog,  
                        (ViewGroup) findViewById(R.id.registdialog));
				
				AlertDialog RegistDialog = new AlertDialog.Builder(LoginActivity.this).setView(layout)
						.setTitle("ע�᣺")
						.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// ʹ�Ի����޷��ر�
								try { 
										Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing"); 
										field.setAccessible(true); 
										field.set(dialog, false);
									} 
								catch (Exception e) { 
										e.printStackTrace(); 
									}
								// ʹ�Ի���ر�
								try {
										Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
										field.setAccessible(true);
										field.set(dialog, true);
									} 
								catch (Exception e) {
										e.printStackTrace();
									}
							}
						}).setPositiveButton("ע��", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// ʹ�Ի����޷��ر�
								try { 
										Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing"); 
										field.setAccessible(true); 
										field.set(dialog, false);
									} 
								catch (Exception e) { 
										e.printStackTrace(); 
									}
								
								// ��ȡ�ؼ�id
								EditText RegistUsername = (EditText)layout.findViewById(R.id.newusername);
								EditText RegistPassword = (EditText)layout.findViewById(R.id.newpassword);
								EditText RegistMail = (EditText)layout.findViewById(R.id.newmail);
								// ������Ч����֤
								Pattern pattern = Pattern
										.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
								Matcher mc = pattern.matcher(RegistMail.getText().toString().trim());
								// ���˻�����������һ��Ϊ��
								if(RegistUsername.getText().toString().trim().equals("")||RegistPassword.getText()
										.toString().trim().equals("")||RegistMail.getText().toString().trim().equals("")){
									if(RegistUsername.getText().toString().trim().equals("")){
										RegistUsername.setHintTextColor(Color.RED);
									}
									if(RegistPassword.getText().toString().trim().equals("")){
										RegistPassword.setHintTextColor(Color.RED);
									}
									if(RegistMail.getText().toString().trim().equals("")){
										RegistMail.setHintTextColor(Color.RED);
									}
								}
								else if(AccountInfo.getString(RegistUsername.getText().toString().trim(), "") != ""){
									// ����ǰ�û����Ѵ���
									RegistUsername.setText("");
									RegistPassword.setText("");
									RegistMail.setText("");
									Toast.makeText(LoginActivity.this, "���û����ѱ�ע��!", Toast.LENGTH_SHORT).show();
								}
								else if(!mc.matches()){
									// �����ʽ����
									RegistMail.setText("");
									Toast.makeText(LoginActivity.this, "��������ȷ�����ַ", Toast.LENGTH_SHORT).show();
								}
								else{
									// �����û�����������
									AccountEditor.putString(RegistUsername.getText().toString().trim(), 
											RegistPassword.getText().toString().trim());
									AccountEditor.putString(RegistUsername.getText().toString().trim()+"mail", 
											RegistMail.getText().toString().trim());
									AccountEditor.commit();
									
									Toast.makeText(getApplicationContext(), "ע��ɹ�!", Toast.LENGTH_SHORT).show();
									
									// ʹ�Ի���ر�
									try {
											Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
											field.setAccessible(true);
											field.set(dialog, true);
										} 
									catch (Exception e) {
											e.printStackTrace();
										}
								}
							}
						}).create();
				RegistDialog.show();
			}
		});
		
		// ���������������
		forgetPassWord.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				LayoutInflater inflater = (LayoutInflater) LoginActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
				final View layout = inflater.inflate(R.layout.forgetpassword_dialog,  
                        (ViewGroup) findViewById(R.id.forgetpassworddialog));
				
				AlertDialog RegistDialog = new AlertDialog.Builder(LoginActivity.this).setView(layout)
						.setTitle("��������һ��˺ŵ��˺������䣺")
						.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// ʹ�Ի����޷��ر�
								try { 
										Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing"); 
										field.setAccessible(true); 
										field.set(dialog, false);
									} 
								catch (Exception e) { 
										e.printStackTrace(); 
									}
								// ʹ�Ի���ر�
								try {
										Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
										field.setAccessible(true);
										field.set(dialog, true);
									} 
								catch (Exception e) {
										e.printStackTrace();
									}
							}
						}).setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// ʹ�Ի����޷��ر�
								try { 
										Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing"); 
										field.setAccessible(true); 
										field.set(dialog, false);
									} 
								catch (Exception e) { 
										e.printStackTrace(); 
									}
								
								// ��ȡ�ؼ�id
								EditText ForgetUsername = (EditText)layout.findViewById(R.id.oldusername);
								EditText ForgetMail = (EditText)layout.findViewById(R.id.oldmail);
								// ������Ч����֤
								Pattern pattern = Pattern
										.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
								Matcher mc = pattern.matcher(ForgetMail.getText().toString().trim());
								// ���˻�����������һ��Ϊ��
								if(ForgetUsername.getText().toString().trim().equals("")||ForgetMail.getText().toString().
										trim().equals("")){
									if(ForgetUsername.getText().toString().trim().equals("")){
										ForgetUsername.setHintTextColor(Color.RED);
									}
									if(ForgetMail.getText().toString().trim().equals("")){
										ForgetMail.setHintTextColor(Color.RED);
									}
								}
								else if(!mc.matches()){
									// �����ʽ����
									ForgetMail.setText("");
									Toast.makeText(LoginActivity.this, "��������ȷ�����ַ", Toast.LENGTH_SHORT).show();
								}
								else{
									String mailcheck = AccountInfo.getString(ForgetUsername.getText().toString()+"mail", "");
									if(mailcheck.trim().equals(ForgetMail.getText().toString())){
										AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
										builder.setMessage("����:"+AccountInfo.getString(ForgetUsername.getText().
												toString(), "")).setIcon(android.R.drawable.ic_dialog_alert)
										       .setCancelable(false)
										       .setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
															@Override
															public void onClick(DialogInterface arg0, int arg1) {
																// TODO �û����������������
															}
										       })
										       .setNegativeButton("ȡ��", null);
										AlertDialog passwordHint = builder.create();
										passwordHint.show();
									}
									else{
										Toast.makeText(LoginActivity.this, "������������벻��ȷ", Toast.LENGTH_SHORT).show();
									}
									
									// ʹ�Ի���ر�
									try {
											Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
											field.setAccessible(true);
											field.set(dialog, true);
										} 
									catch (Exception e) {
											e.printStackTrace();
										}
								}
							}
						}).create();
				RegistDialog.show();
			}
		});
	}
	
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {  
        	AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
			builder.setMessage("�˳��밴ȷ��:").setIcon(android.R.drawable.ic_dialog_alert)
			       .setCancelable(false)
			       .setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									// ����SysApplication���exit����������Ƴ�����activity����
									SysApplication.getInstance().exit();
								}
			       })
			       .setNegativeButton("ȡ��", null);
			AlertDialog ExitAlert = builder.create();
			ExitAlert.show();
        }
        return false;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	@Override  
	 public boolean onOptionsItemSelected(MenuItem item) {  
		 switch(item.getItemId()){
		 case R.id.action_settings:
			 Intent _intent = new Intent(LoginActivity.this, SettingActivity.class);
			 startActivity(_intent);
			 return true;
		 default:
			 break;
		 }
		return false;
	 }

}
