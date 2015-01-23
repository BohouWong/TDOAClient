package com.qingshuimonk.tdoaclient.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ArrayBlockingQueue;

import com.qingshuimonk.tdoaclient.GlobalVariable;
import com.qingshuimonk.tdoaclient.data_structrue.DateTime;
import com.qingshuimonk.tdoaclient.data_structrue.LocationRegion;
import com.qingshuimonk.tdoaclient.data_structrue.Position;
import com.qingshuimonk.tdoaclient.data_structrue.Result;
import com.qingshuimonk.tdoaclient.data_structrue.Tuner;
import com.qingshuimonk.tdoaclient.data_structrue.TunerWorkParameter;
import com.qingshuimonk.tdoaclient.data_structrue.User;
import com.qingshuimonk.tdoaclient.data_structrue.Variance;


/***
 * 本类用于定义指令帧编码解码的相关操作方法
 * @author Huang Bohao
 * @version 1.0.0
 * @since 2014/12/06
 */
public class FrameFormer{
	
	// 定义指令类型
	public enum FRAME_TYPE{
		USER_INFO,						// 用户信息
		AVAILABLE_RECEIVER_MAP, 		// 可用接收机(用于百度地图显示)
		SET_PARAMETER,					// 接收机工作参数
		AVAILABLE_RECEIVER, 			// 可用接收机(用于用户选择)
		CHOSEN_RECEIVER,				// 接收机选择结果
		LOCATION_RESULT,				// 定位结果
		CORRELATION_RESULT, 			// 相关结果
		FREQUENCY_RESULT, 			// 频谱结果
		DATA_REQUEST, 					// 数据请求
		NO_NEW_RESULT					// 无新数据
	}
	public static int START_LENGTH = 4;						// 帧头长度
	public static int PRIMARY_VERSION_LENGTH = 2;				// 主版本号长度
	public static int SECONDARY_VERSION_LENGTH = 2;			// 次版本号长度
	public static int INSTRUCTION_LENGTH = 4;					// 指令序号长度
	public static int RESERVE_BIT_LENGTH = 8;					// 保留位长度
	public static int FRAME_LENGTH_LENGTH = 4;				// 帧长长度
	public static int CLIENT_ID_LENGTH = 6;					// 客户端MAC地址长度
	public static int SERVER_ID_LENGTH = 6;					// 服务器MAC地址长度
	public static int DATE_LENGTH = 14;						// 数据长度
	public static int FRAME_FUNCTION_LENGTH = 4;				// 帧功能码长度
	public static int FRAME_HEAD_LENGTH = 54;					// 帧头长度
	public static int CHECK_CODE_LENGTH = 1;					// 校验位长度
	public static int RECEIVER_ITEM_LENGTH = 34;				// 接收机数据每条目长度
	public static int RECEIVER_ITEM_LENGTH_SHORT = 17;		// 接收机数据(用于百度地图显示)每条目长度
	
	private ByteArrayMethods methods = new ByteArrayMethods();
	private byte extraInfo;
	
	/***
	 * 普通指令构造函数
	 */
	public FrameFormer(){
	}
	/***
	 * 数据请求指令构造函数
	 * @param _extraInfo : 数据请求构造指令序号
	 */
	public FrameFormer(byte _extraInfo){
		extraInfo = _extraInfo;
	}
	
	public byte[] FormTransmitFrame(GlobalVariable GV, FRAME_TYPE type){
		
		byte[] TransmitFrame = null;
		
		byte[] Start = {0x01, 0x32, (byte)0xDC, 0x52};	// magic number
		// 主版本号
		byte[] PrimaryVersionNumber = new byte[2];
		// 次版本号
		byte[] SecondaryVersionNumber = new byte[2];
		// 指令序号
		byte[] InstructNumber = new byte[4];
		// 保留位
		byte[] ReserveBit = new byte[8];
		// 帧长
		byte[] FrameLength = new byte[4];
		// 客户端MAC
		byte[] ClientID = new byte[6];
		// 服务器MAC
		byte[] ServerID = new byte[6];
		// 发送时间
		byte[] TransmitTime = new byte[14];
		// 帧功能码
		byte[] FrameFunction = new byte[4];
		// 构造位
		byte[] CheckCode = {0};
		
		// 产生指令时间
		byte[] SysYear = new byte[2], SysMilisecond = new byte[2];
		byte SysMonth, SysDay, SysHour, SysMinute, SysSecond;
		Calendar c = Calendar.getInstance();
		SysYear = methods.shortToByte((short) c.get(Calendar.YEAR));
		SysMonth = (byte) (c.get(Calendar.MONTH) + 1);
		SysDay = (byte) c.get(Calendar.DAY_OF_MONTH);
		SysHour = (byte) c.get(Calendar.HOUR_OF_DAY);
		SysMinute = (byte) c.get(Calendar.MINUTE);
		SysSecond = (byte) c.get(Calendar.SECOND);
		SysMilisecond = methods.shortToByte((short) c.get(Calendar.MILLISECOND));
		TransmitTime[0] = SysYear[0];
		TransmitTime[1] = SysYear[1];
		TransmitTime[3] = SysMonth;
		TransmitTime[5] = SysDay;
		TransmitTime[7] = SysHour;
		TransmitTime[9] = SysMinute;
		TransmitTime[11] = SysSecond;
	    TransmitTime[12] = SysMilisecond[0];
	    TransmitTime[13] = SysMilisecond[1];
	    
	    switch(type){
	    case USER_INFO:						// 指令类型：用户信息指令
	    	FrameFunction[0] = 0x00;
	    	FrameFunction[1] = 0x03;
	    	FrameFunction[2] = (byte)0x9F;
	    	FrameFunction[3] = 0x10;
	    	break;
	    case SET_PARAMETER:					// 指令类型：设置定位参数
	    	FrameFunction[0] = 0x00;
	    	FrameFunction[1] = 0x03;
	    	FrameFunction[2] = (byte)0x9F;
	    	FrameFunction[3] = 0x12;
	    	break;
	    case CHOSEN_RECEIVER:					// 指令类型：接收机选择结果
	    	FrameFunction[0] = 0x00;
	    	FrameFunction[1] = 0x03;
	    	FrameFunction[2] = (byte)0x9F;
	    	FrameFunction[3] = 0x14;
	    	break;
	    case DATA_REQUEST:					// 指令类型：数据请求
	    	FrameFunction[0] = 0x00;
	    	FrameFunction[1] = 0x03;
	    	FrameFunction[2] = (byte)0x9F;
	    	FrameFunction[3] = 0x18;
	    	break;
		default:
			break;
	    }
	    
	    // 构造帧体
	    byte[] FrameEntity = FrameEntity(GV, type);
	    
	    // 合成指令帧
	    TransmitFrame = methods.byteMerger(Start, PrimaryVersionNumber);
	    TransmitFrame = methods.byteMerger(TransmitFrame, SecondaryVersionNumber);
	    TransmitFrame = methods.byteMerger(TransmitFrame, InstructNumber);
	    TransmitFrame = methods.byteMerger(TransmitFrame, ReserveBit);
	    TransmitFrame = methods.byteMerger(TransmitFrame, FrameLength);
	    TransmitFrame = methods.byteMerger(TransmitFrame, ClientID);
	    TransmitFrame = methods.byteMerger(TransmitFrame, ServerID);
	    TransmitFrame = methods.byteMerger(TransmitFrame, TransmitTime);
	    TransmitFrame = methods.byteMerger(TransmitFrame, FrameFunction);
	    TransmitFrame = methods.byteMerger(TransmitFrame, FrameEntity);
	    TransmitFrame = methods.byteMerger(TransmitFrame, CheckCode);
	    
	    // 添加帧长
	    int length = TransmitFrame.length;
	    FrameLength = methods.intToByte(length);
	    for(int i = 0; i < 4; i++){
	    	TransmitFrame[20+i] = FrameLength[i];
	    }
	    
		return TransmitFrame;
	}
	
	/***
	 * 解析数据指令帧
	 * @param GV
	 * @param type
	 * @param TransmitFrame
	 * @return
	 */
	public boolean DecodeTransmitFrame(GlobalVariable GV, FRAME_TYPE type, byte[] TransmitFrame){
		byte[] function = new byte[4];
		byte[] entity = new byte[TransmitFrame.length - CHECK_CODE_LENGTH - FRAME_HEAD_LENGTH];
		// 获得帧功能码
		for(int i = 0; i < FRAME_FUNCTION_LENGTH ; i++){
			function[i] = TransmitFrame[FRAME_HEAD_LENGTH - FRAME_FUNCTION_LENGTH + i];
		}
		
		// 获得帧体
		for(int i = FRAME_HEAD_LENGTH; i < TransmitFrame.length - CHECK_CODE_LENGTH; i++){
			entity[i - FRAME_HEAD_LENGTH] = TransmitFrame[i];
		}
		
		switch(function[3]){
		case (byte)0x11:
			// 0x39F11, 百度地图接收机显示
			decodeEntity(GV, FRAME_TYPE.AVAILABLE_RECEIVER_MAP, entity);
			break;
		case (byte)0x13:
			// 0x39F11, 接收机选择
			decodeEntity(GV, FRAME_TYPE.AVAILABLE_RECEIVER, entity);
			break;
		case (byte)0x15:
			// 0x39F15, 定位结果
			decodeEntity(GV, FRAME_TYPE.LOCATION_RESULT, entity);
			break;
		}
		
		return true;
	}
	
	/***
	 * 构造不同指令类型的帧体
	 * @param GV
	 * @param type
	 * @return
	 */
	public byte[] FrameEntity(GlobalVariable GV, FRAME_TYPE type){
		User SysUser = GV.SysUser;
		byte[] Entity = null;
		
		if(type.equals(FRAME_TYPE.SET_PARAMETER)){
			// 构造参数设定指令帧
			TunerWorkParameter Parameter = SysUser.getWorkGroup().getParameter();
			LocationRegion Region = Parameter.getLocationRegion();
			DateTime Time = Parameter.getTrigTime();
			// 定位区域类型
			byte[] RegionMode = {Region.getRegionMode()};
			// 定位区域值
			byte[] RegionInput1 = methods.doubleToByte(Region.getRegionValue1());
			byte[] RegionInput2 = methods.doubleToByte(Region.getRegionValue2());
			byte[] RegionInput3 = methods.doubleToByte(Region.getRegionValue3());
			byte[] RegionInput4 = methods.doubleToByte(Region.getRegionValue4());
			// 定位算法
			byte[] Algorithm = {0x01};
			// 中心频率
			byte[] CenterFre = methods.longToByte(Parameter.getCenterFreq());
			// 带宽
			byte[] BandWidth = methods.intToByte(Parameter.getBandWidth());
			// 触发方式
			byte[] TrigMode = {Parameter.getTrigMode()};
			// 根据不同触方式构造帧体
			byte[] TrigTime = {0};
			byte[] TrigPower = {0};
			if(Parameter.getTrigMode() == 0){
				// 时间触发
				short year = (short)Time.getYear();
				short month = (short)Time.getMonth();
				short day = (short)Time.getDay();
				short hour = (short)Time.getHour();
				short minute = (short)Time.getMinute();
				TrigTime = methods.shortToByte(year);
				TrigTime = methods.byteMerger(TrigTime, methods.shortToByte(month));
				TrigTime = methods.byteMerger(TrigTime, methods.shortToByte(day));
				TrigTime = methods.byteMerger(TrigTime, methods.shortToByte(hour));
				TrigTime = methods.byteMerger(TrigTime, methods.shortToByte(minute));
				TrigTime = methods.byteMerger(TrigTime, methods.shortToByte((short)0));
				TrigTime = methods.byteMerger(TrigTime, methods.shortToByte((short)0));
			}
			if(Parameter.getTrigMode() == 1){
				// 能量触发
				TrigPower = methods.shortToByte(Parameter.getTrigPower());
			}
			// MGC
			// 根据指令帧，MGC需要左移一位
			byte[] MGC = {(byte) (Parameter.getMGC()*2)};
			// IQ包长度
			byte[] IQSize = {Parameter.getIQNum()};
			// 检测帧
			byte[] CheckFrame = new byte[2];
			
			Entity = methods.byteMerger(RegionMode, RegionInput1);
			Entity = methods.byteMerger(Entity, RegionInput2);
			Entity = methods.byteMerger(Entity, RegionInput3);
			Entity = methods.byteMerger(Entity, RegionInput4);
			Entity = methods.byteMerger(Entity, Algorithm);
			Entity = methods.byteMerger(Entity, CenterFre);
			Entity = methods.byteMerger(Entity, BandWidth);
			Entity = methods.byteMerger(Entity, TrigMode);
			if(Parameter.getTrigMode() == 0)
				Entity = methods.byteMerger(Entity, TrigTime);
			else
				Entity = methods.byteMerger(Entity, TrigPower);
			Entity = methods.byteMerger(Entity, MGC);
			Entity = methods.byteMerger(Entity, IQSize);
			Entity = methods.byteMerger(Entity, CheckFrame);
		}
		else if(type.equals(FRAME_TYPE.CHOSEN_RECEIVER)){
			// 构造接收机选择结果数据桢
			ArrayList<Tuner> TunerGroup = SysUser.getWorkGroup().getTunerGroup();
			int TunerNum = TunerGroup.size();
			Entity = new byte[1 + TunerNum];
			Entity[0] = (byte)TunerNum;
			int i = 1;
			for(Tuner tuner:TunerGroup){
				Entity[i] = tuner.getTunerID();
				i++;
			}
		}
		else if(type.equals(FRAME_TYPE.DATA_REQUEST)){
			// 构造数据请求指令帧
			switch(extraInfo){
			case 0x01: Entity = new byte[1];
						Entity[0] = 0x01;
						break;
			case 0x02: Entity = new byte[1];
						Entity[0] = 0x02;
						break;
			case 0x03: break;
			case 0x04: Entity = new byte[1];
						Entity[0] = 0x04;
						break;
			case 0x05: break;
			case 0x06: Entity = new byte[1];
						Entity[0] = 0x06;
						break;
			case 0x07: Entity = new byte[1];
						Entity[0] = 0x07;
						break;
			case 0x08: break;
			}
		}
		else if(type.equals(FRAME_TYPE.USER_INFO)){
			// 构造用户信息指令帧
			String UserName = SysUser.getUserName();
			String IPAddress = GV.Server_Address;
			String IPs[] = IPAddress.split("\\.");
			
			byte[] Name = UserName.getBytes();
			byte[] stop = {-1};
			byte[] IP = {(byte)Integer.parseInt(IPs[0]),(byte)Integer.parseInt(IPs[1])
					,(byte)Integer.parseInt(IPs[2]),(byte)Integer.parseInt(IPs[3])};
			
			Entity = methods.byteMerger(Name, stop);
			Entity = methods.byteMerger(Entity, IP);
		}
		
		return Entity;
	}
	
	/***
	 * 解析指令帧功能和数据
	 * @param GV
	 * @param type
	 * @param FrameEntity
	 * @return
	 */
	public boolean decodeEntity(GlobalVariable GV, FRAME_TYPE type, byte[] FrameEntity){
		if(type.equals(FRAME_TYPE.AVAILABLE_RECEIVER)){
			// 解析可用接收机指令帧
			int receiverNum = (int) FrameEntity[0];
			
			// 转换用数组
			byte[] ReceiverLongitudebytes = new byte[8]; 
			byte[] ReceiverLatitudebytes = new byte[8];
			byte[] ReceiverAltitudebytes = new byte[8];
			byte[] ReceiverVppbytes = new byte[8];
			
			GV.AvailableTuner.clear();
			
			for(int i = 0; i < receiverNum; i ++){
				byte ReceiverOrder = FrameEntity[1 + i*RECEIVER_ITEM_LENGTH];
				byte ReceiverWorkMode = FrameEntity[2 + i*RECEIVER_ITEM_LENGTH];
				for(int j = 0; j < 8; j++){
					// copy byte arrays
					ReceiverLongitudebytes[j] = FrameEntity[3+i*RECEIVER_ITEM_LENGTH + j];
					ReceiverLatitudebytes[j] = FrameEntity[3+i*RECEIVER_ITEM_LENGTH + 8 + j];
					ReceiverAltitudebytes[j] = FrameEntity[3+i*RECEIVER_ITEM_LENGTH + 16 + j];
					ReceiverVppbytes[j] = FrameEntity[3+i*RECEIVER_ITEM_LENGTH + 24 + j];
				}
				long l = methods.getLong(ReceiverLongitudebytes);
				double ReceiverLongitude = Double.longBitsToDouble(l);
				l = methods.getLong(ReceiverLatitudebytes);
				double ReceiverLatitude = Double.longBitsToDouble(l);
				l = methods.getLong(ReceiverAltitudebytes);
				double ReceiverAltitude = Double.longBitsToDouble(l);
				l = methods.getLong(ReceiverVppbytes);
				double ReceiverVpp = Double.longBitsToDouble(l);
				
				// 添加进ArrayList
				Position newPos = new Position(ReceiverLongitude, ReceiverLatitude, ReceiverAltitude);
				Tuner newTuner0 = new Tuner(ReceiverOrder, newPos, ReceiverWorkMode, ReceiverVpp);
				GV.AvailableTuner.add(GV.AvailableTuner.size(), newTuner0);
			}
		}
		else if(type.equals(FRAME_TYPE.AVAILABLE_RECEIVER_MAP)){
			// 解析可用接收机(用于百度地图显示)指令
			int receiverNum = (int) FrameEntity[0];

			// 转换用数组
			byte[] ReceiverLongitudebytes = new byte[8];
			byte[] ReceiverLatitudebytes = new byte[8];
			
			GV.AvailableTuner.clear();
			
			for(int i = 0; i < receiverNum; i ++){
				byte ReceiverOrder = FrameEntity[1 + i*RECEIVER_ITEM_LENGTH_SHORT];
				for(int j = 0; j < 8; j++){
					ReceiverLongitudebytes[j] = FrameEntity[2+i*RECEIVER_ITEM_LENGTH_SHORT + j];
					ReceiverLatitudebytes[j] = FrameEntity[2+i*RECEIVER_ITEM_LENGTH_SHORT + 8 + j];
				}
				long l = methods.getLong(ReceiverLongitudebytes);
				double ReceiverLongitude = Double.longBitsToDouble(l);
				l = methods.getLong(ReceiverLatitudebytes);
				double ReceiverLatitude = Double.longBitsToDouble(l);
				
				// 添加进ArrayList
				Position newPos = new Position(ReceiverLongitude, ReceiverLatitude, 0);
				Tuner newTuner0 = new Tuner(ReceiverOrder, newPos, (byte) 0, 0);
				GV.AvailableTuner.add(GV.AvailableTuner.size(), newTuner0);
			}
		}
		else if(type.equals(FRAME_TYPE.LOCATION_RESULT)){
			byte[] TargetLongitudebytes = new byte[8]; 
			byte[] TargetLatitudebytes = new byte[8];
			byte[] TargetAltitudebytes = new byte[8];
			byte[] TargetVariancebytes = new byte[8];
			for(int j = 0; j < 8; j++){
				TargetLongitudebytes[j] = FrameEntity[j];
				TargetLatitudebytes[j] = FrameEntity[j + 8];
				TargetAltitudebytes[j] = FrameEntity[j + 16];
				TargetVariancebytes[j] = FrameEntity[j + 24];
			}
			long l = methods.getLong(TargetLongitudebytes);
			double TargetLongitude = Double.longBitsToDouble(l);
			l = methods.getLong(TargetLatitudebytes);
			double TargetLatitude = Double.longBitsToDouble(l);
			l = methods.getLong(TargetAltitudebytes);
			double TargetAltitude = Double.longBitsToDouble(l);
			l = methods.getLong(TargetVariancebytes);
			double TargetVariance = Double.longBitsToDouble(l);
			short[] time = new short[7];
			byte[] time_temp = new byte[2];
			for(int i = 0; i < 14; i ++){
				time_temp[i%2] = FrameEntity[i + 32];
				if(i%2 == 1){
					time[i/2] = (short) methods.byteToUShort(time_temp);
				}
			}
			
			// 结果添加进result
			Position locationPosition = new Position(TargetLongitude, TargetLatitude, TargetAltitude);
			DateTime locationTime = new DateTime(time[0], time[1], time[2], time[3], time[4], time[5], time[6]);
			ArrayBlockingQueue<Double> varQueue = new ArrayBlockingQueue<Double>(1);
			varQueue.add(TargetVariance);
			Variance Var = new Variance(varQueue, locationTime);
			Result locationResult = new Result(null, locationPosition, Var, locationTime);
			locationResult.setDateTime(locationTime);
			locationResult.setPosition(locationPosition);
			locationResult.setVariance(Var);
			if(GV.SysUser.getWorkGroup().getResult() == null){
				GV.SysUser.getWorkGroup().setResult(locationResult);
			}
			else{
				GV.SysUser.getWorkGroup().setResult(null);
				GV.SysUser.getWorkGroup().setResult(locationResult);
			}
		}
		
		return true;
	}
}