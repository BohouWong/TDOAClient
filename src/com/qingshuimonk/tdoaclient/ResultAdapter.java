package com.qingshuimonk.tdoaclient;

import java.util.List;
import com.qingshuimonk.tdoaclient.data_structrue.DateTime;
import com.qingshuimonk.tdoaclient.data_structrue.Position;
import com.qingshuimonk.tdoaclient.data_structrue.Result;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/***
 * FIXME
 * @author Huang Bohao
 * ��activity����ResultActivity��LocationResultActivity���������
 */
public class ResultAdapter extends ArrayAdapter<Result>{
    
    int resource;
    
    public ResultAdapter(Context context, int _resource, List<Result> items){
        super(context, _resource, items);
        resource = _resource;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LinearLayout newView;
        
        Result item = getItem(position);
        
        Position Pos = item.getPosition();
    	final DateTime Time = item.getTime();
        
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
        
        TextView itemResultTime = (TextView)newView.findViewById(R.id.itemResultTime);
        TextView itemResultLongitude = (TextView)newView.findViewById(R.id.itemResultLongitude);
        TextView itemResultLatitude = (TextView)newView.findViewById(R.id.itemResultLatitude);
        TextView itemResultAltitude = (TextView)newView.findViewById(R.id.itemResultAltitude);
        TextView itemResultCorrelation = (TextView)newView.findViewById(R.id.itemResultCorrelation);
        TextView itemResultVariance = (TextView)newView.findViewById(R.id.itemResultVariance);
        
        itemResultTime.setText("ʱ��:"+Time.getMonth()+"/"+Time.getDay()+" "+Time.getHour()+":"+Time.getMinute()+":"
        		+Time.getSecond());
        itemResultLongitude.setText("����:"+Pos.getLongitude()+"��");
        itemResultLatitude.setText("γ��:"+Pos.getLatitude()+"��");
        itemResultAltitude.setText("�߶�:"+Pos.getAltitude()+"");
        
        itemResultCorrelation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
				builder.setMessage("�Ƿ���ʾ������ݣ�")
				       .setCancelable(false)
				       .setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
									@Override
									public void onClick(DialogInterface dialog, int which) {
										Intent mIntent = new Intent(v.getContext(),CorrelationActivity.class);
										Bundle mBundle = new Bundle();
										mBundle.putSerializable("correlation", 4);
										mIntent.putExtras(mBundle);
										v.getContext().startActivity(mIntent);
									}
				       })
				       .setNegativeButton("ȡ��", null);
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
        
        itemResultVariance.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
				builder.setMessage("�Ƿ���ʾ�������ݣ�")
				       .setCancelable(false)
				       .setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
									@Override
									public void onClick(DialogInterface dialog, int which) {
										Intent mIntent = new Intent(v.getContext(),CorrelationActivity.class);
										Bundle mBundle = new Bundle();
										mBundle.putSerializable("variance", 4);
										mIntent.putExtras(mBundle);
										v.getContext().startActivity(mIntent);
									}
				       })
				       .setNegativeButton("ȡ��", null);
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
        
        return newView;
    }
    
}