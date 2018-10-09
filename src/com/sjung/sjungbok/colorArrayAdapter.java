package com.sjung.sjungbok;

import com.sjung.sjungbok.R;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class colorArrayAdapter extends ArrayAdapter<String>{  
	private final Context context;
	private final String[] values; 
	private int positionofSelf;
	private int blueOne=-1;




	public colorArrayAdapter(Context context, String[] values, int positionOfSelf) {
		super(context, R.layout.drawer_list_item, values);
		this.context = context;
		this.values = values;
		this.positionofSelf=positionOfSelf;
	}


	@Override  
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = super.getView(position, convertView, parent);  
		if(position==positionofSelf){
			view.setBackgroundColor(Color.parseColor("#E16990")); 
		}
		else {
			view.setBackgroundColor(Color.parseColor("#F280A1"));
		}

		return view;  
	}}  