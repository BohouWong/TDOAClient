package com.qingshuimonk.tdoaclient.data_structrue;

/***
 * 本类用于定义时间数据相关的变量和方法
 * @author Huang Bohao
 * @version 1.1
 * @since 2014.11.10
 */
public class DateTime{
	
	private short Year;
	private short Month;
	private short Day;
	private short Hour;
	private short Minute;
	private short Second;
	@SuppressWarnings("unused")
	private short Milisecond;		// getMilisecond is not available in client

	/***
	 * 构造函数
	 * @param _Year			
	 * @param _Month
	 * @param _Day
	 * @param _Hour
	 * @param _Minute
	 * @param _Second
	 * @param _Milisecond
	 */
	public DateTime(short _Year, short _Month, short _Day, short _Hour, short _Minute, short _Second, short _Milisecond){
		Year = _Year;
		Month = _Month;
		Day = _Day;
		Hour = _Hour;
		Minute = _Minute;
		Second = _Second;
		Milisecond = _Milisecond;
	}
	/***
	 * 构造函数
	 * @param _Year
	 * @param _Month
	 * @param _Day
	 * @param _Hour
	 * @param _Minute
	 */
	public DateTime(short _Year, short _Month, short _Day, short _Hour, short _Minute){
		Year = _Year;
		Month = _Month;
		Day = _Day;
		Hour = _Hour;
		Minute = _Minute;
	}
	
	// setters
	public void setYear(short _Year){
		Year = _Year;
	}
	public void setMonth(short _Month){
		Month = _Month;
	}
	public void setDay(short _Day){
		Day = _Day;
	}
	public void setHour(short _Hour){
		Hour = _Hour;
	}
	public void setMinute(short _Minute){
		Minute = _Minute;
	}
	public void setSecond(short _Second){
		Second = _Second;
	}
	public void setMilisecond(short _Milisecond){
		Milisecond = _Milisecond;
	}
	
	// getters
	public short getYear(){
		return Year;
	}
	public short getMonth(){
		return Month;
	}
	public short getDay(){
		return Day;
	}
	public short getHour(){
		return Hour;
	}
	public short getMinute(){
		return Minute;
	}
	public short getSecond(){
		return Second;
	}
	
	/***
	 * FIXME
	 * 添加计算时间差的方法
	 */

}