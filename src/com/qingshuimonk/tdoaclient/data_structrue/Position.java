package com.qingshuimonk.tdoaclient.data_structrue;

/***
 * 本类用于定义位置数据相关的变量和方法
 * @author Huang Bohao
 * @version 1.0
 * @since 2014.11.10
 */
public class Position{
	private double Longitude;
	private double Latitude;
	private double Altitude;
	private DateTime Time;	
	
	/***
	 * 构造函数
	 * @param _Longitude
	 * @param _Latitude
	 * @param _Altitude
	 * @param _Time
	 */
	public Position(double _Longitude, double _Latitude, double _Altitude, DateTime _Time){
		Longitude = _Longitude;
		Latitude = _Latitude;
		Altitude = _Altitude;
		Time = _Time;
	}
	/***
	 * 构造函数，无有效时间
	 * @param _Longitude
	 * @param _Latitude
	 * @param _Altitude
	 */
	public Position(double _Longitude, double _Latitude, double _Altitude){
		Longitude = _Longitude;
		Latitude = _Latitude;
		Altitude = _Altitude;
	}
	
	// setters
	public void setLongitude(double _Longitude){
		Longitude = _Longitude;
	}
	public void setLatitude(double _Latitude){
		Latitude = _Latitude;
	}
	public void setAltitude(double _Altitude){
		Altitude = _Altitude;
	}
	public void setDateTime(DateTime _Time){
		Time = _Time;
	}
	
	// getters
	public double getLongitude(){
		return Longitude;
	}
	public double getLatitude(){
		return Latitude;
	}
	public double getAltitude(){
		return Altitude;
	}
	public DateTime getTime(){
		return Time;
	}

}