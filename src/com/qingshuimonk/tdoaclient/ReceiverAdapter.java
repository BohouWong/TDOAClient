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

public class ReceiverAdapter extends ArrayAdapter<Tuner>{
	
	int resource;
	
	// ����
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
		String style = "0.000000";			//����Ҫ��ʾ�����ֵĸ�ʽ
        df.applyPattern(style);				// ����ʽӦ���ڸ�ʽ����
		
		IDView.setText("���:"+itemID);
		vppView.setText("Vpp:"+itemVpp+"dB");
		longitudeView.setText("����:"+df.format(itemLongitude)+"��");
		latitudeView.setText("γ��:"+df.format(itemLatitude)+"��");
		heightView.setText("�߶�:"+df.format(height)+"m");
		modeView.setText("ģʽ:"+workmode);
		
		Selected.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					int i;
					i = Integer.parseInt(itemID);
					if(!ChooseResult.contains(ID)){
						ChooseResult.add((Integer)i);
						ReceiverNum++;
					}
				}
				else{
					int i;
					i = Integer.parseInt(itemID);
					if(ChooseResult.contains(ID)){
						ChooseResult.remove((Integer)i);
						ReceiverNum--;
					}
				}
			}
		});
		
		if(ChooseResult.contains(ID)){
			Selected.setChecked(true);
		}
		else{
			Selected.setChecked(false);
		}
		
		return newView;
	}
	
}
