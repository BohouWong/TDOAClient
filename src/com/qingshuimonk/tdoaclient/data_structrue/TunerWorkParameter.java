package com.qingshuimonk.tdoaclient.data_structrue;

/***
 * This is a class defines variables and functions of TunerWorkParameter
 * @author Huang Bohao
 * @version 1.0
 * @since 2014.11.10
 */

public class TunerWorkParameter{
	private long CenterFreq;			// Center Frequency
	private int BandWidth; 				// Bandwidth
	private byte TrigMode;				// Trigger mode
	private DateTime TrigTime; 			// Trigger time
	private short TrigPower;			// Trigger power
	private byte IQNum;					// IQ data in each package, units in kB
	private byte MGC;					// Manual gain control
	private LocationRegion Region;		// Location Region
	
	/***
	 * Constructor of TunerWorkParameter
	 * @param _CenterFreq
	 * @param _BandWidth
	 * @param _TrigMode: 	0:Time trigger mode;	1:Power trigger mode
	 * @param _TrigTime
	 * @param _TrigPower
	 * @param _IQNum
	 * @param _MGC
	 * @param _Region
	 */
	public TunerWorkParameter(long _CenterFreq, int _BandWidth, byte _TrigMode, DateTime _TrigTime, 
			short _TrigPower, byte _IQNum, byte _MGC, LocationRegion _Region){
		CenterFreq = _CenterFreq;
		BandWidth = _BandWidth;
		TrigMode = _TrigMode;
		TrigTime = _TrigTime;
		TrigPower = _TrigPower;
		IQNum = _IQNum;
		MGC = _MGC;
		Region = _Region;
	}
	/***
	 * Constructor of TunerWorkParameter
	 * @param _Region
	 */
	public TunerWorkParameter(LocationRegion _Region){
		Region = _Region;
	}
	
	/***
	 * Set CenterFreq
	 * @param _CenterFreq
	 */
	public void setCenterFreq(long _CenterFreq){
		CenterFreq = _CenterFreq;
	}
	
	/***
	 * Set BandWidth
	 * @param _BandWidth
	 */
	public void setBandWidth(int _BandWidth){
		BandWidth = _BandWidth;
	}
	
	/***
	 * Set TrigMode
	 * @param _TrigMode
	 */
	public void setTrigMode(byte _TrigMode){
		TrigMode = _TrigMode;
	}
	
	/***
	 * Set Time
	 * @param _Time
	 */
	public void setTrigTime(DateTime _TrigTime){
		TrigTime = _TrigTime;
	}
	
	/***
	 * Set TrigPower
	 * @param _TrigPower
	 */
	public void setTrigPower(short _TrigPower){
		TrigPower = _TrigPower;
	}
	
	/***
	 * Set IQNum
	 * @param _IQNum
	 */
	public void setIQNum(byte _IQNum){
		IQNum = _IQNum;
	}
	
	/***
	 * Set MGC
	 * @param MGC
	 */
	public void setMGC(byte _MGC){
		MGC = _MGC;
	}
	
	/***
	 * Set Region
	 * @param _Region
	 */
	public void setLocationRegion(LocationRegion _Region){
		Region = _Region;
	}
	
	/***
	 * Get CenterFreq
	 * @return CenterFreq;
	 */
	public long getCenterFreq(){
		return CenterFreq;
	}
	
	/***
	 * Get BandWidth
	 * @return BandWidth
	 */
	public int getBandWidth(){
		return BandWidth;
	}
	
	/***
	 * Get TrigMode
	 * @return TrigMode
	 */
	public byte getTrigMode(){
		return TrigMode;
	}
	
	/***
	 * Get TrigTime
	 * @return TrigTime
	 */
	public DateTime getTrigTime(){
		return TrigTime;
	}
	
	/***
	 * Get TrigPower
	 * @return TrigPower
	 */
	public short getTrigPower(){
		return TrigPower;
	}
	
	/***
	 * Get IQNum
	 * @return IQNum
	 */
	public byte getIQNum(){
		return IQNum;
	}
	
	/***
	 * Get MGC
	 * @return MGC
	 */
	public byte getMGC(){
		return MGC;
	}
	
	/***
	 * Get Region
	 * @return Region
	 */
	public LocationRegion getLocationRegion(){
		return Region;
	}

}