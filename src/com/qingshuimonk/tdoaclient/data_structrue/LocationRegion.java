package com.qingshuimonk.tdoaclient.data_structrue;

/***
 * This is a class defines variables and functions of LocationRegion
 * @author Huang Bohao
 * @version 1.0
 * @since 2014.11.10
 */

public class LocationRegion{
	private byte RegionMode;
	private double RegionValue1;
	private double RegionValue2;
	private double RegionValue3;
	private double RegionValue4;
	
	/***
	 * Constructor of LocationRegion
	 * @param _RegionMode		1:Circular region;			0:Rectangular region
	 * @param _RegionValue1		1:Longitude of first point;	0:Longitude of the center
	 * @param _RegionValue2		1:Latitude of first point;	0:Latitude of the center
	 * @param _RegionValue3		1:Radius of the circle;		0:Longitude of second point
	 * @param _RegionValue4		1:Default;					0:Latitude of second point
	 */
	public LocationRegion(byte _RegionMode, double _RegionValue1, double _RegionValue2, double _RegionValue3, double _RegionValue4){
		RegionMode = _RegionMode;
		RegionValue1 = _RegionValue1;
		RegionValue2 = _RegionValue2;
		RegionValue3 = _RegionValue3;
		RegionValue4 = _RegionValue4;
	}
	
	/***
	 * Set RegionMode of LocationRegion
	 * @param _RegionMode
	 */
	public void setRegionMode(byte _RegionMode){
		RegionMode = _RegionMode;
	}
	
	/***
	 * Set RegionValue1 of LocationRegion
	 * @param _RegionValue1
	 */
	public void setRegionValue1(byte _RegionValue1){
		RegionValue1 = _RegionValue1;
	}
	
	/***
	 * Set RegionValue2 of LocationRegion
	 * @param _RegionValue2
	 */
	public void setRegionValue2(byte _RegionValue2){
		RegionValue2 = _RegionValue2;
	}
	
	/***
	 * Set RegionValue3 of LocationRegion
	 * @param _RegionValue3
	 */
	public void setRegionValue3(byte _RegionValue3){
		RegionValue3 = _RegionValue3;
	}
	
	/***
	 * Set RegionValue4 of LocationRegion
	 * @param _RegionValue4
	 */
	public void setRegionValue4(byte _RegionValue4){
		RegionValue4 = _RegionValue4;
	}
	
	/***
	 * Get RegionMode of LocationRegion
	 * @return RegionMode
	 */
	public byte getRegionMode(){
		return RegionMode;
	}
	
	/***
	 * Get RegionValue1 of LocationRegion
	 * @return RegionValue1
	 */
	public double getRegionValue1(){
		return RegionValue1;
	}
	
	/***
	 * Get RegionValue2 of LocationRegion
	 * @return RegionValue2
	 */
	public double getRegionValue2(){
		return RegionValue2;
	}
	
	/***
	 * Get RegionValue3 of LocationRegion
	 * @return RegionValue3
	 */
	public double getRegionValue3(){
		return RegionValue3;
	}
	
	/***
	 * Get RegionValue4 of LocationRegion
	 * @return RegionValue4
	 */
	public double getRegionValue4(){
		return RegionValue4;
	}

}