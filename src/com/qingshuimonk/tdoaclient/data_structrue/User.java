package com.qingshuimonk.tdoaclient.data_structrue;

/***
 * This is a class defines variables and functions of User
 * @author Huang Bohao
 * @version 1.0
 * @since 2014.11.10
 */

public class User{
	private String UserName;			
	private String PassWord;			
	private TunerWorkGroup WorkGroup;
	
	/***
	 * Constructor of User, used after application is launched
	 * @param _UserName
	 * @param _PassWord
	 */
	public User(String _UserName, String _PassWord){
		UserName = _UserName;
		PassWord = _PassWord;
	}
	/***
	 * Constructor of User, used for initiate
	 */
	public User(){
		UserName = "";
		PassWord = "";
	}
	
	/***
	 * Set UserName
	 * @param _UserName
	 */
	public void setUserName(String _UserName){
		UserName = _UserName;
	}
	
	/***
	 * Set PassWord
	 * @param _PassWord
	 */
	public void setPassWord(String _PassWord){
		PassWord = _PassWord;
	}
	
	/***
	 * Set WorkGroup
	 * @param _WorkGroup
	 */
	public void setWorkGroup(TunerWorkGroup _WorkGroup){
		WorkGroup = _WorkGroup;
	}
	
	/***
	 * Get UserName
	 * @return UserName
	 */
	public String getUserName(){
		return UserName;
	}
	
	/***
	 * Get PassWord
	 * @return PassWord
	 */
	public String getPassWord(){
		return PassWord;
	}
	
	/***
	 * Get WorkGroup
	 * @return WorkGroup
	 */
	public TunerWorkGroup getWorkGroup(){
		return WorkGroup;
	}

}