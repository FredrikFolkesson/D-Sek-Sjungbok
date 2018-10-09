package com.sjung.sjungbok;


import java.util.ArrayList;


import android.content.Context;
import android.graphics.Typeface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.TextView;

public class SimplerSongArrayAdapter extends ArrayAdapter<Song>{




    public SimplerSongArrayAdapter(Context context, ArrayList<Song> songs) {
        super(context,R.layout.item_song,songs);


    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Song song= getItem(position);
        if(convertView ==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_song,parent,false);
        }
        TextView textView1 = (TextView) convertView.findViewById(R.id.tvSongTitle);
        TextView textView2 = (TextView) convertView.findViewById(R.id.tvSongMelody);
        textView2.setTypeface(null, Typeface.ITALIC);
        textView1.setText(song.getTitle());
        textView2.setText(song.getMelody());




        return convertView;
    }



}

