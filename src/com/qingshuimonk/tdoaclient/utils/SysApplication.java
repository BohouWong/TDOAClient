package com.qingshuimonk.tdoaclient.utils;

import java.util.LinkedList; 
import java.util.List; 
import android.app.Activity; 
import android.app.Application; 
 
/***
 * 本类用于一次退出所有activity功能
 * 功能:		
 * 	1.将每一个登录后的activity加入ArrayList;
 *  2.一次退出所有activity功能；
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
    // 添加Activity  
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