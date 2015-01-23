package com.qingshuimonk.tdoaclient.utils;

import java.util.LinkedList; 
import java.util.List; 
import android.app.Activity; 
import android.app.Application; 
 
/***
 * ��������һ���˳�����activity����
 * ����:		
 * 	1.��ÿһ����¼���activity����ArrayList;
 *  2.һ���˳�����activity���ܣ�
 * @author Huang Bohao
 * @version 1.0.0
 * @since 2014.11.11
 */
public class SysApplication extends Application { 
    private List<Activity> mList = new LinkedList<Activity>(); 
    private static SysApplication instance; 
 
    private SysApplication() {   
    } 
    public synchronized static SysApplication getInstance() { 
        if (null == instance) { 
            instance = new SysApplication(); 
        } 
        return instance; 
    } 
    // ���Activity  
    public void addActivity(Activity activity) { 
        mList.add(activity); 
    } 
 
    public void exit() { 
        try { 
            for (Activity activity : mList) { 
                if (activity != null) 
                    activity.finish(); 
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            System.exit(0); 
        } 
    } 
    public void onLowMemory() { 
        super.onLowMemory();     
        System.gc(); 
    }  
}