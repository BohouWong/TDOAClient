package com.qingshuimonk.tdoaclient;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;

public class SettingActivity extends PreferenceActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.activity_setting);
		
		// add Activity to list
		SysApplication.getInstance().addActivity(this);
	}
	
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
		// Used to access activity-level global variable
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
