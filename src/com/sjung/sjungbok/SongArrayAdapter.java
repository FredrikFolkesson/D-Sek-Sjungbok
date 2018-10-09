package com.sjung.sjungbok;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sjung.sjungbok.R;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class SongArrayAdapter extends ArrayAdapter<Song> implements SectionIndexer{

    ArrayList<String> myElements;
    HashMap<String, Integer> alphaIndexer;

    String[] sections;



	public SongArrayAdapter(Context context, ArrayList<Song> songs,int sortingType) {
		super(context,R.layout.item_song,songs);

        alphaIndexer = new HashMap<String, Integer>();
        int size = songs.size();
        String element;
        for (int i = size - 1; i >= 0; i--) {
            if(sortingType==0) {
                element = songs.get(i).getTitle();
            }
            else {
                element = songs.get(i).getMelody();
            }
            alphaIndexer.put(element.substring(0, 1), i);
            //We store the first letter of the word, and its index.
            //The Hashmap will replace the value for identical keys are putted in
        }

        Set<String> keys = alphaIndexer.keySet(); // set of letters ...sets
        // cannot be sorted...

        Iterator<String> it = keys.iterator();
        ArrayList<String> keyList = new ArrayList<String>(); // list can be
        // sorted

        while (it.hasNext()) {
            String key = it.next();
            keyList.add(key);
        }

        Collections.sort(keyList);

        sections = new String[keyList.size()]; // simple conversion to an
        // array of object
        keyList.toArray(sections);


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
	
	

//}

//class MyIndexerAdapter<T> extends ArrayAdapter<T> implements SectionIndexer {





    @Override
    public int getPositionForSection(int section) {
        // Log.v("getPositionForSection", ""+section);
        String letter = sections[section];

        return alphaIndexer.get(letter);
    }

    @Override
    public int getSectionForPosition(int position) {

        // you will notice it will be never called (right?)
        Log.v("getSectionForPosition", "called");
        return 0;
    }

    @Override
    public Object[] getSections() {

        return sections; // to string will be called each object, to display
        // the letter
    }
}