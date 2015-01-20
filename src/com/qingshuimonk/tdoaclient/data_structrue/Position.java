package com.qingshuimonk.tdoaclient.data_structrue;

/***
 * This is a class defines variables and functions of Position
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
	 * Constructor of Position
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
	 * Constructor of Position, used when no datetime is available
	 * @param _Longitude
	 * @param _Latitude
	 * @param _Altitude
	 */
	public Position(double _Longitude, double _Latitude, double _Altitude){
		Longitude = _Longitude;
		Latitude = _Latitude;
		Altitude = _Altitude;
	}
	
	/***
	 * Set the Longitude of Position
	 * @param _Longitude
	 */
	public void setLongitude(double _Longitude){
		Longitude = _Longitude;
	}
	
	/***
	 * Set the Latitude of Positon
	 * @param _Latitude
	 */
	public void setLatitude(double _Latitude){
		Latitude = _Latitude;
	}
	
	/***
	 * Set the Altitude of Position
	 * @param _Altitude
	 */
	public void setAltitude(double _Altitude){
		Altitude = _Altitude;
	}
	
	/***
	 * Set the Time of Position
	 * If Position is used to be the result of target's position,
	 * Time represents to the time when server completes calculation.
	 * If Position is used in other ways like the location of server,
	 * Time represents to the time when client receives its position. 
	 * @param _Time
	 */
	public void setDateTime(DateTime _Time){
		Time = _Time;
	}
	
	/***
	 * Get the Longitude of Position
	 * @return Longitude
	 */
	public double getLongitude(){
		return Longitude;
	}
	
	/***
	 * Get the Latitude of Position
	 * @return Latitude
	 */
	public double getLatitude(){
		return Latitude;
	}
	
	/***
	 * Get the Altitude of Position
	 * @return Altitude
	 */
	public double getAltitude(){
		return Altitude;
	}
	
	/***
	 * Get the Time of Position
	 * @return
	 */
	public DateTime getTime(){
		return Time;
	}

}