package com.qingshuimonk.tdoaclient;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.qingshuimonk.tdoaclient.utils.FrameFormer.FRAME_TYPE;


/***
 * This is an android activity file:
 * function:	1.Launch activity;
 * 				2.Guide user to create a new account or enter an old one;
 * @author Huang Bohao
 * @version 1.0.0
 * @since 2014.11.11
 *
 */
public class LoginActivity extends Activity {
	
	// Create widgets
	EditText inputUserName;
	EditText inputPassWord;
	TextView forgetPassWord;
	TextView createNewAccount;
	Button Login;
	
	// Global variables
	String UserName, PassWord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		// add activity to list
		SysApplication.getInstance().addActivity(this);

		// Used to access activity-level global variable
		final GlobalVariable GV = (GlobalVariable)getApplicationContext(); 
		
		// variables for udp connection
		final int Local_Port = GV.Local_Port;
		final int Server_Port = GV.Server_Port;
		final String Server_Address = GV.Server_Address;
		
		// Get widgets ID
		inputUserName = (EditText)findViewById(R.id.inputUserName);
		inputPassWord = (EditText)findViewById(R.id.inputPassWord);
		forgetPassWord = (TextView)findViewById(R.id.forgetPassWord);
		createNewAccount = (TextView)findViewById(R.id.createNewAccount);
		Login = (Button)findViewById(R.id.Login);
		
		// Listeners
		OnClickListener loginCheckListener;
		
		// create action bar
		ActionBar LoginActionBar = this.getActionBar();
		LoginActionBar.setDisplayShowTitleEnabled(true);	// ActionBar shows title only
		LoginActionBar.setDisplayShowHomeEnabled(false);
		
		// create a sharedpreference
		final SharedPreferences AccountInfo = (SharedPreferences)this.getSharedPreferences("AccountInfo", MODE_PRIVATE);
		final SharedPreferences.Editor AccountEditor = AccountInfo.edit();
		
		// input a temporary username and password
		// for debug only
		AccountEditor.putString("22", "22");
		AccountEditor.putString("22mail", "22@22.com");
		AccountEditor.commit();
		
		// define operations of listeners
		loginCheckListener = new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try{
					UserName = inputUserName.getText().toString();
					PassWord = inputPassWord.getText().toString();
					// find corresponding password in sharedpreferences
					String logincheck = AccountInfo.getString(UserName, "");
					
					// check if the username exists
					if((logincheck.trim().equals(""))){
						Dialog NoUserNameDialog = new AlertDialog.Builder(LoginActivity.this)
						.setIcon(android.R.drawable.ic_dialog_alert).setTitle("登陆错误：")
						.setMessage("无此用户名，请检查输入。")
						.setPositiveButton("确定", null).setNegativeButton("取消", null)
						.create();
						NoUserNameDialog.show();
						
						inputUserName.setText("");
						inputPassWord.setText("");
					}
					else{
						// if user name and password matches
						if(logincheck.trim().equals(PassWord)){
							// create the user class
							GV.SysUser.setUserName(UserName);
							GV.SysUser.setPassWord(PassWord);
							
							if(GV.DEBUG_NETWORK_CONDITION){
								// jump to next activity, for wifi debug
								
								// send data to server
								final ProgressDialog SendMessage = new ProgressDialog(LoginActivity.this);
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
								
								Communicator UserInfo_cator = new Communicator(sendhandler);
								UserInfo_cator.sendInstruction(GV, FRAME_TYPE.USER_INFO);
								
								Intent _intent = new Intent(LoginActivity.this, RegionChooseActivity.class);
								startActivity(_intent);
							}
							else{
								// jump to next activity
								// for no wifi debug only
								LocationRegion Region = new LocationRegion((byte) 0,20.12345,20.12345,
										20.12345,20.12345);
								TunerWorkParameter Parameter = new TunerWorkParameter(Region);
								TunerWorkGroup Group = new TunerWorkGroup(Parameter);
								GV.SysUser.setWorkGroup(Group);
								Intent _intent = new Intent(LoginActivity.this, LocationParameterActivity.class);
								startActivity(_intent);
							}
							
							
						}
						// if user name does not match password
						else{
							// warning about wrong password
							Dialog WrongPassword = new AlertDialog.Builder(LoginActivity.this).setIcon(R.drawable.ic_launcher)
									.setTitle("登陆错误：").setMessage("密码错误，请检查输入。")
									.setNegativeButton("取消", new DialogInterface.OnClickListener(){
										@Override
										public void onClick(DialogInterface dialog, int id) {
											// TODO Auto-generated method stub
											inputPassWord.setText("");
										}
									}).setPositiveButton("确定", new DialogInterface.OnClickListener(){
										public void onClick(DialogInterface dialog, int id){
											inputPassWord.setText("");
										}
									}).create();
							WrongPassword.show();
						}
					}
				}catch(Exception e){
					// deal exception
				}
			}
		};
		
		// bind listener to widgets
		Login.setOnClickListener(loginCheckListener);
		
		createNewAccount.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutInflater inflater = (LayoutInflater) LoginActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
				final View layout = inflater.inflate(R.layout.regist_dialog,  
                        (ViewGroup) findViewById(R.id.registdialog));
				
				AlertDialog RegistDialog = new AlertDialog.Builder(LoginActivity.this).setView(layout)
						.setTitle("注册：")
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								// 使对话框无法关闭
								try { 
										Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing"); 
										field.setAccessible(true); 
										field.set(dialog, false);
									} 
								catch (Exception e) { 
										e.printStackTrace(); 
									}
								// 使对话框关闭
								try {
										Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
										field.setAccessible(true);
										field.set(dialog, true);
									} 
								catch (Exception e) {
										e.printStackTrace();
									}
							}
						}).setPositiveButton("注册", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
								// 使对话框无法关闭
								try { 
										Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing"); 
										field.setAccessible(true); 
										field.set(dialog, false);
									} 
								catch (Exception e) { 
										e.printStackTrace(); 
									}
								
								// 获取控件id
								EditText RegistUsername = (EditText)layout.findViewById(R.id.newusername);
								EditText RegistPassword = (EditText)layout.findViewById(R.id.newpassword);
								EditText RegistMail = (EditText)layout.findViewById(R.id.newmail);
								// 获取密码长度
								int PasswordLength = RegistPassword.getText().toString().length();
								// 邮箱有效性验证
								Pattern pattern = Pattern
										.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
								Matcher mc = pattern.matcher(RegistMail.getText().toString().trim());
								// 若账户密码邮箱有一项为空
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
									// 若当前用户名已存在
									RegistUsername.setText("");
									RegistPassword.setText("");
									RegistMail.setText("");
									Toast.makeText(LoginActivity.this, "该用户名已被注册!", Toast.LENGTH_SHORT).show();
								}
								else if(!mc.matches()){
									// 邮箱格式错误
									RegistMail.setText("");
									Toast.makeText(LoginActivity.this, "请输入正确邮箱地址", Toast.LENGTH_SHORT).show();
								}
								else{
									// 保存用户名密码邮箱
									AccountEditor.putString(RegistUsername.getText().toString().trim(), 
											RegistPassword.getText().toString().trim());
									AccountEditor.putString(RegistUsername.getText().toString().trim()+"mail", 
											RegistMail.getText().toString().trim());
									AccountEditor.commit();
									
									Toast.makeText(getApplicationContext(), "注册成功!", Toast.LENGTH_SHORT).show();
									
									// 使对话框关闭
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
		
		forgetPassWord.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutInflater inflater = (LayoutInflater) LoginActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
				final View layout = inflater.inflate(R.layout.forgetpassword_dialog,  
                        (ViewGroup) findViewById(R.id.forgetpassworddialog));
				
				AlertDialog RegistDialog = new AlertDialog.Builder(LoginActivity.this).setView(layout)
						.setTitle("请输入带找回账号的账号与邮箱：")
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								// 使对话框无法关闭
								try { 
										Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing"); 
										field.setAccessible(true); 
										field.set(dialog, false);
									} 
								catch (Exception e) { 
										e.printStackTrace(); 
									}
								// 使对话框关闭
								try {
										Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
										field.setAccessible(true);
										field.set(dialog, true);
									} 
								catch (Exception e) {
										e.printStackTrace();
									}
							}
						}).setPositiveButton("确定", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
								// 使对话框无法关闭
								try { 
										Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing"); 
										field.setAccessible(true); 
										field.set(dialog, false);
									} 
								catch (Exception e) { 
										e.printStackTrace(); 
									}
								
								// 获取控件id
								EditText ForgetUsername = (EditText)layout.findViewById(R.id.oldusername);
								EditText ForgetMail = (EditText)layout.findViewById(R.id.oldmail);
								// 邮箱有效性验证
								Pattern pattern = Pattern
										.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
								Matcher mc = pattern.matcher(ForgetMail.getText().toString().trim());
								// 若账户密码邮箱有一项为空
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
									// 邮箱格式错误
									ForgetMail.setText("");
									Toast.makeText(LoginActivity.this, "请输入正确邮箱地址", Toast.LENGTH_SHORT).show();
								}
								else{
									String mailcheck = AccountInfo.getString(ForgetUsername.getText().toString()+"mail", "");
									if(mailcheck.trim().equals(ForgetMail.getText().toString())){
										AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
										builder.setMessage("密码:"+AccountInfo.getString(ForgetUsername.getText().
												toString(), "")).setIcon(android.R.drawable.ic_dialog_alert)
										       .setCancelable(false)
										       .setPositiveButton("确定", new DialogInterface.OnClickListener(){
															@Override
															public void onClick(DialogInterface arg0, int arg1) {
																// TODO Auto-generated method
															}
										       })
										       .setNegativeButton("取消", null);
										AlertDialog passwordHint = builder.create();
										passwordHint.show();
									}
									else{
										Toast.makeText(LoginActivity.this, "输入邮箱或密码不正确", Toast.LENGTH_SHORT).show();
									}
									
									// 使对话框关闭
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
			builder.setMessage("退出请按确定:").setIcon(android.R.drawable.ic_dialog_alert)
			       .setCancelable(false)
			       .setPositiveButton("确定", new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									// TODO Auto-generated method stub
									// add method to exit
									SysApplication.getInstance().exit();
								}
			       })
			       .setNegativeButton("取消", null);
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
		 // TODO Auto-generated method stub  
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
