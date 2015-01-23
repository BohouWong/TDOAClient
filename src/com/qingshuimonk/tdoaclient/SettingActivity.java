package com.qingshuimonk.tdoaclient;

import com.qingshuimonk.tdoaclient.R;
import com.qingshuimonk.tdoaclient.utils.SysApplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;

/**
 * ��activity���ڶ������ý���
 * ����xml�ļ�: activity_setting.xml
 * ����:		
 * 	1.�����û�����udp���Ӳ���;
 * @author Huang Bohao
 * @version 1.0.0
 * @since 2014.11.23
 */
public class SettingActivity extends PreferenceActivity{

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.activity_setting);
		
		// ����activity��ӵ�SysApplication����
		SysApplication.getInstance().addActivity(this);
	}
	
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
		// ��ȡGlobalVariable���ȫ�ֱ���
		final GlobalVariable GV = (GlobalVariable)getApplicationContext(); 
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this); 
		
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {  
        	GV.Server_Address = sp.getString("SettingServerAddress", GV.Server_Address);
        	GV.Server_Port = Integer.parseInt(sp.getString("SettingServerPort", GV.Server_Port+""));
        	GV.Local_Port = Integer.parseInt(sp.getString("SettingLocalPort", GV.Server_Port+""));
        }
        finish();
        return false;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

}
