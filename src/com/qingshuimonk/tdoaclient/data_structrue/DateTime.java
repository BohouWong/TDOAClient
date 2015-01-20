package com.qingshuimonk.tdoaclient.data_structrue;

/***
 * This is a class defines variables and functions corresponding to date time.
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
	private short Milisecond;		// getMilisecond is not available in client

	/***
	 * Constructor of DateTime
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
	 * Constructor of DateTime
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
	
	/***
	 * Set the Year in DateTime
	 * @param _Year
	 */
	public void setYear(short _Year){
		Year = _Year;
	}
	
	/***
	 * Set the Month in DateTime
	 * @param _Month
	 */
	public void setMonth(short _Month){
		Month = _Month;
	}
	
	/***
	 * Set the Day in DateTime
	 * @param _Day
	 */
	public void setDay(short _Day){
		Day = _Day;
	}
	
	/***
	 * Set the Hour in DateTime
	 * @param _Hour
	 */
	public void setHour(short _Hour){
		Hour = _Hour;
	}
	
	/***
	 * Set the Minute in DateTime
	 * @param _Minute
	 */
	public void setMinute(short _Minute){
		Minute = _Minute;
	}
	
	/***
	 * Set the Second in DateTime
	 * @param _Second
	 */
	public void setSecond(short _Second){
		Second = _Second;
	}
	
	/***
	 * Set the Milisecond in DateTime
	 * @param _Milisecond
	 */
	public void setMilisecond(short _Milisecond){
		Milisecond = _Milisecond;
	}
	
	/***
	 * Get the Year in DateTime
	 * @return Year
	 */
	public short getYear(){
		return Year;
	}
	
	/***
	 * Get the Month in DateTime
	 * @return Month
	 */
	public short getMonth(){
		return Month;
	}
	
	/***
	 * Get the Day in DateTime
	 * @return Day
	 */
	public short getDay(){
		return Day;
	}
	
	/***
	 * Get the Hour in DateTime
	 * @return Hour
	 */
	public short getHour(){
		return Hour;
	}
	
	/***
	 * Get the Hour in DateTime
	 * @return
	 */
	public short getMinute(){
		return Minute;
	}
	
	/***
	 * Get the Second in DateTime
	 * @return Second
	 */
	public short getSecond(){
		return Second;
	}
	
	/***
	 * Transform DateTime into seconds, used to calculate time difference
	 * @return differenceofTime
	 */
	public long secondofTime(){
		long differenceofTime = 0;
		return differenceofTime;  
	}		

}