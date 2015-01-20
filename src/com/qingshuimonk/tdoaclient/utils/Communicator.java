package com.qingshuimonk.tdoaclient.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.qingshuimonk.tdoaclient.GlobalVariable;
import com.qingshuimonk.tdoaclient.utils.FrameFormer.FRAME_TYPE;

import android.os.Handler;
import android.os.Message;

/***
 * This is a file used to send and receive data between client and server 
 * @author Huang Bohao
 * @version 1.0.0
 * @since 2015/01/10
 */
public class Communicator{
	private Handler communicatorHandler;
	
	public Communicator(Handler handler){
		this.communicatorHandler = handler;
	}
	
	public void sendInstruction(final GlobalVariable GV, final FRAME_TYPE type){
		final Thread sendData = new Thread(){
			public void run(){
				try {
					DatagramSocket socket = new  DatagramSocket (GV.Server_Port);
					InetAddress serverAddress = InetAddress.getByName(GV.Server_Address);
					// communication class
					FrameFormer former = new FrameFormer();
					byte[] transmitFrame = former.FormTransmitFrame(GV, type);
					// 发送帧
					DatagramPacket  packet = new DatagramPacket (transmitFrame, transmitFrame.length, serverAddress, GV.Server_Port);
					socket.send(packet);
					socket.close();
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				Message msg = new Message();
		        msg.what = 0;
		        communicatorHandler.sendMessage(msg);
			}
		};
		sendData.start();
	}
	
	public void sendDataRequest(final GlobalVariable GV, final FRAME_TYPE type, final byte extraInfo){
		final Thread sendData = new Thread(){
			public void run(){
				try {
					DatagramSocket socket = new  DatagramSocket (GV.Local_Port);
					InetAddress serverAddress = InetAddress.getByName(GV.Server_Address);
					// communication class
					FrameFormer former = new FrameFormer(extraInfo);
					
					byte[] transmitFrame = former.FormTransmitFrame(GV, type);
					// 发送帧
					DatagramPacket  packet = new DatagramPacket (transmitFrame, transmitFrame.length, serverAddress, GV.Server_Port);
					socket.send(packet);
					socket.close();
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				Message msg = new Message();
		        msg.what = 0;
		        communicatorHandler.sendMessage(msg);
			}
		};
		sendData.start();
	}
	
	public void receiveData(final GlobalVariable GV, final FRAME_TYPE type){
		if(type.equals(FRAME_TYPE.LOCATION_RESULT)){
			// 开启线程
			final Thread receiveReceiverData = new Thread() {
				public void run() {
					boolean receiveflag = false;
					// 网络通信
					while (true) {
						try {
							DatagramSocket socket = new DatagramSocket(GV.Local_Port);
							byte data[] = new byte[1024];
							DatagramPacket packet = new DatagramPacket(data, data.length);
							socket.receive(packet);
							// decode frame
							FrameFormer former = new FrameFormer();
							former.DecodeTransmitFrame(GV, type, data);
							//AvailableTunerGroup = receiveritem;
							receiveflag = true;
							socket.close();
							//break;
						} catch (SocketException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Message msg = new Message();
						try {
							if (receiveflag)
								msg.what = 1;
						} catch (Exception e) {
						}
						communicatorHandler.sendMessage(msg);
					}
				}
			};
			receiveReceiverData.start();
		}
		else{
			// 开启线程
			final Thread receiveReceiverData = new Thread() {
				public void run() {
					boolean receiveflag = false;
					// 网络通信
					while (true) {
						try {
							DatagramSocket socket = new DatagramSocket(GV.Local_Port);
							byte data[] = new byte[1024];
							DatagramPacket packet = new DatagramPacket(data, data.length);
							socket.receive(packet);
							// decode frame
							FrameFormer former = new FrameFormer();
							former.DecodeTransmitFrame(GV, type, data);
							//AvailableTunerGroup = receiveritem;
							receiveflag = true;
							socket.close();
							break;
						} catch (SocketException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					Message msg = new Message();
					try {
						if (receiveflag)
							msg.what = 1;
					} catch (Exception e) {
					}
					communicatorHandler.sendMessage(msg);
				}
			};
			receiveReceiverData.start();
		}
	}
}