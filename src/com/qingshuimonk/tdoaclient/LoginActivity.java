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
 * 本activity用于定义用户登录界面
 * 配套xml文件: activity_login.xml
 * 功能:		
 * 	1.引导用户输入账号密码并登录 ;
 *  2.提供密码找回，找好注册功能；
 * 	3.将用户登录成功的账号密码及IP地址发送给服务器;
 * @author Huang Bohao
 * @version 1.0.0
 * @since 2014.11.11
 */
public class LoginActivity extends Activity {
	
	// 控件
	EditText inputUserName;
	EditText inputPassWord;
	TextView forgetPassWord;
	TextView createNewAccount;
	Button Login;
	
	// 用户名和密码
	String UserName, PassWord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		// 将此activity添加到SysApplication类中
		SysApplication.getInstance().addActivity(this);

		// 获取GlobalVariable类的全局变量
		final GlobalVariable GV = (GlobalVariable)getApplicationContext(); 
		
		// 控件
		inputUserName = (EditText)findViewById(R.id.inputUserName);
		inputPassWord = (EditText)findViewById(R.id.inputPassWord);
		forgetPassWord = (TextView)findViewById(R.id.forgetPassWord);
		createNewAccount = (TextView)findViewById(R.id.createNewAccount);
		Login = (Button)findViewById(R.id.Login);
		
		// 登录按键监听
		OnClickListener loginCheckListener;
		
		// 创建action bar
		ActionBar LoginActionBar = this.getActionBar();
		LoginActionBar.setDisplayShowTitleEnabled(true);	// ActionBar只显示title
		LoginActionBar.setDisplayShowHomeEnabled(false);
		
		// 创建sharedpreference，用于保存用户名和密码
		final SharedPreferences AccountInfo = (SharedPreferences)this.getSharedPreferences("AccountInfo", MODE_PRIVATE);
		final SharedPreferences.Editor AccountEditor = AccountInfo.edit();
		
		// 产生一个虚拟的用户名和密码
		// 只用于调试
		AccountEditor.putString("22", "22");
		AccountEditor.putString("22mail", "22@22.com");
		AccountEditor.commit();
		
		// 登录按键监听程序
		loginCheckListener = new OnClickListener(){
			@Override
			public void onClick(View v) {
				try{
					UserName = inputUserName.getText().toString();
					PassWord = inputPassWord.getText().toString();
					// 在sharedpreferences中寻找对应账号
					String logincheck = AccountInfo.getString(UserName, "");
					
					// 检查用户名是否存在
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
						// 检查账号密码是否符合
						if(logincheck.trim().equals(PassWord)){
							// 创建 User class
							GV.SysUser.setUserName(UserName);
							GV.SysUser.setPassWord(PassWord);
							
							// TODO 自动判断有无网络连接
							if(GV.DEBUG_NETWORK_CONDITION){
								// Network连接，直接跳到下一个activity
								
								// 将账户密码IP地址信息发送到服务器
								final ProgressDialog SendMessage = new ProgressDialog(LoginActivity.this);
								SendMessage.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								SendMessage.setMessage("等待向服务器发送参数");
								SendMessage.setCancelable(false);
								SendMessage.show();
								// 配置handler
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
								
								// 调用Communicator工具类
								Communicator UserInfo_cator = new Communicator(sendhandler);
								UserInfo_cator.sendInstruction(GV, FRAME_TYPE.USER_INFO);
								
								// 跳转到下一个activity
								Intent _intent = new Intent(LoginActivity.this, RegionChooseActivity.class);
								startActivity(_intent);
							}
							else{
								// 无Network连接，创建虚拟定位结果后跳转到LocationParameterActivity
								LocationRegion Region = new LocationRegion((byte) 0,20.12345,20.12345,
										20.12345,20.12345);
								TunerWorkParameter Parameter = new TunerWorkParameter(Region);
								TunerWorkGroup Group = new TunerWorkGroup(Parameter);
								GV.SysUser.setWorkGroup(Group);
								Intent _intent = new Intent(LoginActivity.this, LocationParameterActivity.class);
								startActivity(_intent);
							}
							
							
						}
						// 若用户名密码不符合
						else{
							// 提示密码错误
							Dialog WrongPassword = new AlertDialog.Builder(LoginActivity.this).setIcon(R.drawable.ic_launcher)
									.setTitle("登陆错误：").setMessage("密码错误，请检查输入。")
									.setNegativeButton("取消", new DialogInterface.OnClickListener(){
										@Override
										public void onClick(DialogInterface dialog, int id) {
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
					// 处理异常
				}
			}
		};
		
		// 添加监听函数
		Login.setOnClickListener(loginCheckListener);
		
		// 注册账号监听函数
		createNewAccount.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				LayoutInflater inflater = (LayoutInflater) LoginActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
				final View layout = inflater.inflate(R.layout.regist_dialog,  
                        (ViewGroup) findViewById(R.id.registdialog));
				
				AlertDialog RegistDialog = new AlertDialog.Builder(LoginActivity.this).setView(layout)
						.setTitle("注册：")
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
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
		
		// 忘记密码监听函数
		forgetPassWord.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				LayoutInflater inflater = (LayoutInflater) LoginActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
				final View layout = inflater.inflate(R.layout.forgetpassword_dialog,  
                        (ViewGroup) findViewById(R.id.forgetpassworddialog));
				
				AlertDialog RegistDialog = new AlertDialog.Builder(LoginActivity.this).setView(layout)
						.setTitle("请输入带找回账号的账号与邮箱：")
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
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
																// TODO 用户获得密码后操作函数
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
									// 调用SysApplication类的exit方法，完成推出所有activity操作
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
