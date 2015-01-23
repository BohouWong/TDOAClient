package com.qingshuimonk.tdoaclient;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.qingshuimonk.tdoaclient.data_structrue.Tuner;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/***
 * 本类用于定义Receiver相关的ListView操作
 * 功能:		
 * 	1.填充每一个List的内容;
 *  2.每一次用户选择/取消接收机时更新用户选择；
 * 	3.返回用户已选中的接收机;
 * @author Huang Bohao
 * @version 1.0.0
 * @since 2014.11.11
 */
public class ReceiverAdapter extends ArrayAdapter<Tuner>{
	
	int resource;
	
	// 定义
	private byte ReceiverNum = 0;
	private ArrayList<Integer> ChooseResult = new ArrayList<Integer>();
	
	public ReceiverAdapter(Context context, int _resource, List<Tuner> items){
		super(context, _resource, items);
		resource = _resource;
	}
	
	public byte getReceiverNum(){
		return ReceiverNum;
	}
	
	public ArrayList<Integer> getChooseResult(){
		return ChooseResult;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		LinearLayout newView;
		
		Tuner item = getItem(position);
		final Integer ID = (int) item.getTunerID();
		
		final String itemID = item.getTunerID()+"";
		final String itemVpp = item.getAveVoltage()+"";
		double itemLongitude = item.getPosition().getLongitude();
		double itemLatitude = item.getPosition().getLatitude();
		double height = item.getPosition().getAltitude();
		String workmode = item.getWorkMode()+"";
		
		if(convertView == null){
			newView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater li;
			li = (LayoutInflater)getContext().getSystemService(inflater);
			li.inflate(resource, newView, true);
		}
		else{
			newView = (LinearLayout)convertView;
		}
		
		TextView IDView = (TextView)newView.findViewById(R.id.itemID);
		TextView vppView = (TextView)newView.findViewById(R.id.itemVpp);
		TextView longitudeView = (TextView)newView.findViewById(R.id.itemLongitude);
		TextView latitudeView = (TextView)newView.findViewById(R.id.itemLatitude);
		TextView heightView = (TextView)newView.findViewById(R.id.itemAltitude);
		TextView modeView = (TextView)newView.findViewById(R.id.itemMode);
		CheckBox Selected = (CheckBox)newView.findViewById(R.id.itemchoose);
		
		DecimalFormat df = new DecimalFormat();
		String style = "0.000000";			//定义要显示的数字的格式
        df.applyPattern(style);				// 将格式应用于格式化器
		
		IDView.setText("编号:"+itemID);
		vppView.setText("Vpp:"+itemVpp+"dB");
		longitudeView.setText("经度:"+df.format(itemLongitude)+"°");
		latitudeView.setText("纬度:"+df.format(itemLatitude)+"°");
		heightView.setText("高度:"+df.format(height)+"m");
		modeView.setText("模式:"+workmode);
		
		Selected.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					// 选中后向ChooseResult添加该接收机ID
					// 接收机选择数+1
					int i;
					i = Integer.parseInt(itemID);
					if(!ChooseResult.contains(ID)){
						ChooseResult.add((Integer)i);
						ReceiverNum++;
					}
				}
				else{
					// 取消后向ChooseResult除去该接收机ID
					// 接收机选择数-1
					int i;
					i = Integer.parseInt(itemID);
					if(ChooseResult.contains(ID)){
						ChooseResult.remove((Integer)i);
						ReceiverNum--;
					}
				}
			}
		});
		
		// 每次按照用户选中状态跟新每个List的选中状态
		// 避免因共用view造成的“一点多选”问题
		if(ChooseResult.contains(ID)){
			Selected.setChecked(true);
		}
		else{
			Selected.setChecked(false);
		}
		
		return newView;
	}
	
}
