package com.sjung.sjungbok;

import java.util.ArrayList;
import java.util.HashSet;

public class SongListWrapper {
	public static ArrayList<Song> songList;
	public static HashSet<String> categories;
	public SongListWrapper(ArrayList<Song> songList){
		this.songList=songList;
	}


}
