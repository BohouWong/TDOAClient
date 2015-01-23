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
 * �������ڶ���ָ��֡����������ز�������
 * @author Huang Bohao
 * @version 1.0.0
 * @since 2014/12/06
 */
public class FrameFormer{
	
	// ����ָ������
	public enum FRAME_TYPE{
		USER_INFO,						// �û���Ϣ
		AVAILABLE_RECEIVER_MAP, 		// ���ý��ջ�(���ڰٶȵ�ͼ��ʾ)
		SET_PARAMETER,					// ���ջ���������
		AVAILABLE_RECEIVER, 			// ���ý��ջ�(�����û�ѡ��)
		CHOSEN_RECEIVER,				// ���ջ�ѡ����
		LOCATION_RESULT,				// ��λ���
		CORRELATION_RESULT, 			// ��ؽ��
		FREQUENCY_RESULT, 			// Ƶ�׽��
		DATA_REQUEST, 					// ��������
		NO_NEW_RESULT					// ��������
	}
	public static int START_LENGTH = 4;						// ֡ͷ����
	public static int PRIMARY_VERSION_LENGTH = 2;				// ���汾�ų���
	public static int SECONDARY_VERSION_LENGTH = 2;			// �ΰ汾�ų���
	public static int INSTRUCTION_LENGTH = 4;					// ָ����ų���
	public static int RESERVE_BIT_LENGTH = 8;					// ����λ����
	public static int FRAME_LENGTH_LENGTH = 4;				// ֡������
	public static int CLIENT_ID_LENGTH = 6;					// �ͻ���MAC��ַ����
	public static int SERVER_ID_LENGTH = 6;					// ������MAC��ַ����
	public static int DATE_LENGTH = 14;						// ���ݳ���
	public static int FRAME_FUNCTION_LENGTH = 4;				// ֡�����볤��
	public static int FRAME_HEAD_LENGTH = 54;					// ֡ͷ����
	public static int CHECK_CODE_LENGTH = 1;					// У��λ����
	public static int RECEIVER_ITEM_LENGTH = 34;				// ���ջ�����ÿ��Ŀ����
	public static int RECEIVER_ITEM_LENGTH_SHORT = 17;		// ���ջ�����(���ڰٶȵ�ͼ��ʾ)ÿ��Ŀ����
	
	private ByteArrayMethods methods = new ByteArrayMethods();
	private byte extraInfo;
	
	/***
	 * ��ָͨ��캯��
	 */
	public FrameFormer(){
	}
	/***
	 * ��������ָ��캯��
	 * @param _extraInfo : ����������ָ�����
	 */
	public FrameFormer(byte _extraInfo){
		extraInfo = _extraInfo;
	}
	
	public byte[] FormTransmitFrame(GlobalVariable GV, FRAME_TYPE type){
		
		byte[] TransmitFrame = null;
		
		byte[] Start = {0x01, 0x32, (byte)0xDC, 0x52};	// magic number
		// ���汾��
		byte[] PrimaryVersionNumber = new byte[2];
		// �ΰ汾��
		byte[] SecondaryVersionNumber = new byte[2];
		// ָ�����
		byte[] InstructNumber = new byte[4];
		// ����λ
		byte[] ReserveBit = new byte[8];
		// ֡��
		byte[] FrameLength = new byte[4];
		// �ͻ���MAC
		byte[] ClientID = new byte[6];
		// ������MAC
		byte[] ServerID = new byte[6];
		// ����ʱ��
		byte[] TransmitTime = new byte[14];
		// ֡������
		byte[] FrameFunction = new byte[4];
		// ����λ
		byte[] CheckCode = {0};
		
		// ����ָ��ʱ��
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
	    case USER_INFO:						// ָ�����ͣ��û���Ϣָ��
	    	FrameFunction[0] = 0x00;
	    	FrameFunction[1] = 0x03;
	    	FrameFunction[2] = (byte)0x9F;
	    	FrameFunction[3] = 0x10;
	    	break;
	    case SET_PARAMETER:					// ָ�����ͣ����ö�λ����
	    	FrameFunction[0] = 0x00;
	    	FrameFunction[1] = 0x03;
	    	FrameFunction[2] = (byte)0x9F;
	    	FrameFunction[3] = 0x12;
	    	break;
	    case CHOSEN_RECEIVER:					// ָ�����ͣ����ջ�ѡ����
	    	FrameFunction[0] = 0x00;
	    	FrameFunction[1] = 0x03;
	    	FrameFunction[2] = (byte)0x9F;
	    	FrameFunction[3] = 0x14;
	    	break;
	    case DATA_REQUEST:					// ָ�����ͣ���������
	    	FrameFunction[0] = 0x00;
	    	FrameFunction[1] = 0x03;
	    	FrameFunction[2] = (byte)0x9F;
	    	FrameFunction[3] = 0x18;
	    	break;
		default:
			break;
	    }
	    
	    // ����֡��
	    byte[] FrameEntity = FrameEntity(GV, type);
	    
	    // �ϳ�ָ��֡
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
	    
	    // ���֡��
	    int length = TransmitFrame.length;
	    FrameLength = methods.intToByte(length);
	    for(int i = 0; i < 4; i++){
	    	TransmitFrame[20+i] = FrameLength[i];
	    }
	    
		return TransmitFrame;
	}
	
	/***
	 * ��������ָ��֡
	 * @param GV
	 * @param type
	 * @param TransmitFrame
	 * @return
	 */
	public boolean DecodeTransmitFrame(GlobalVariable GV, FRAME_TYPE type, byte[] TransmitFrame){
		byte[] function = new byte[4];
		byte[] entity = new byte[TransmitFrame.length - CHECK_CODE_LENGTH - FRAME_HEAD_LENGTH];
		// ���֡������
		for(int i = 0; i < FRAME_FUNCTION_LENGTH ; i++){
			function[i] = TransmitFrame[FRAME_HEAD_LENGTH - FRAME_FUNCTION_LENGTH + i];
		}
		
		// ���֡��
		for(int i = FRAME_HEAD_LENGTH; i < TransmitFrame.length - CHECK_CODE_LENGTH; i++){
			entity[i - FRAME_HEAD_LENGTH] = TransmitFrame[i];
		}
		
		switch(function[3]){
		case (byte)0x11:
			// 0x39F11, �ٶȵ�ͼ���ջ���ʾ
			decodeEntity(GV, FRAME_TYPE.AVAILABLE_RECEIVER_MAP, entity);
			break;
		case (byte)0x13:
			// 0x39F11, ���ջ�ѡ��
			decodeEntity(GV, FRAME_TYPE.AVAILABLE_RECEIVER, entity);
			break;
		case (byte)0x15:
			// 0x39F15, ��λ���
			decodeEntity(GV, FRAME_TYPE.LOCATION_RESULT, entity);
			break;
		}
		
		return true;
	}
	
	/***
	 * ���첻ָͬ�����͵�֡��
	 * @param GV
	 * @param type
	 * @return
	 */
	public byte[] FrameEntity(GlobalVariable GV, FRAME_TYPE type){
		User SysUser = GV.SysUser;
		byte[] Entity = null;
		
		if(type.equals(FRAME_TYPE.SET_PARAMETER)){
			// ��������趨ָ��֡
			TunerWorkParameter Parameter = SysUser.getWorkGroup().getParameter();
			LocationRegion Region = Parameter.getLocationRegion();
			DateTime Time = Parameter.getTrigTime();
			// ��λ��������
			byte[] RegionMode = {Region.getRegionMode()};
			// ��λ����ֵ
			byte[] RegionInput1 = methods.doubleToByte(Region.getRegionValue1());
			byte[] RegionInput2 = methods.doubleToByte(Region.getRegionValue2());
			byte[] RegionInput3 = methods.doubleToByte(Region.getRegionValue3());
			byte[] RegionInput4 = methods.doubleToByte(Region.getRegionValue4());
			// ��λ�㷨
			byte[] Algorithm = {0x01};
			// ����Ƶ��
			byte[] CenterFre = methods.longToByte(Parameter.getCenterFreq());
			// ����
			byte[] BandWidth = methods.intToByte(Parameter.getBandWidth());
			// ������ʽ
			byte[] TrigMode = {Parameter.getTrigMode()};
			// ���ݲ�ͬ����ʽ����֡��
			byte[] TrigTime = {0};
			byte[] TrigPower = {0};
			if(Parameter.getTrigMode() == 0){
				// ʱ�䴥��
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
				// ��������
				TrigPower = methods.shortToByte(Parameter.getTrigPower());
			}
			// MGC
			// ����ָ��֡��MGC��Ҫ����һλ
			byte[] MGC = {(byte) (Parameter.getMGC()*2)};
			// IQ������
			byte[] IQSize = {Parameter.getIQNum()};
			// ���֡
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
			// ������ջ�ѡ����������
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
			// ������������ָ��֡
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
			// �����û���Ϣָ��֡
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
	 * ����ָ��֡���ܺ�����
	 * @param GV
	 * @param type
	 * @param FrameEntity
	 * @return
	 */
	public boolean decodeEntity(GlobalVariable GV, FRAME_TYPE type, byte[] FrameEntity){
		if(type.equals(FRAME_TYPE.AVAILABLE_RECEIVER)){
			// �������ý��ջ�ָ��֡
			int receiverNum = (int) FrameEntity[0];
			
			// ת��������
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
				
				// ��ӽ�ArrayList
				Position newPos = new Position(ReceiverLongitude, ReceiverLatitude, ReceiverAltitude);
				Tuner newTuner0 = new Tuner(ReceiverOrder, newPos, ReceiverWorkMode, ReceiverVpp);
				GV.AvailableTuner.add(GV.AvailableTuner.size(), newTuner0);
			}
		}
		else if(type.equals(FRAME_TYPE.AVAILABLE_RECEIVER_MAP)){
			// �������ý��ջ�(���ڰٶȵ�ͼ��ʾ)ָ��
			int receiverNum = (int) FrameEntity[0];

			// ת��������
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
				
				// ��ӽ�ArrayList
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
			
			// �����ӽ�result
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