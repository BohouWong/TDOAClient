package com.qingshuimonk.tdoaclient.data_structrue;

/***
 * 本类用于定义用户数据相关的变量和方法
 * @author Huang Bohao
 * @version 1.0
 * @since 2014.11.10
 */

public class User{
	private String UserName;			
	private String PassWord;			
	private TunerWorkGroup WorkGroup;
	
	/***
	 * 构造函数
	 * @param _UserName
	 * @param _PassWord
	 */
	public User(String _UserName, String _PassWord){
		UserName = _UserName;
		PassWord = _PassWord;
	}
	/***
	 * 构造函数
	 */
	public User(){
		UserName = "";
		PassWord = "";
	}
	
	// setters
	public void setUserName(String _UserName){
		UserName = _UserName;
	}
	public void setPassWord(String _PassWord){
		PassWord = _PassWord;
	}
	public void setWorkGroup(TunerWorkGroup _WorkGroup){
		WorkGroup = _WorkGroup;
	}
	
	// getters
	public String getUserName(){
		return UserName;
	}
	public String getPassWord(){
		return PassWord;
	}
	public TunerWorkGroup getWorkGroup(){
		return WorkGroup;
	}

}