package com.sjung.sjungbok;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class Searcher  extends AsyncTask<Void, ArrayList<Song>, ArrayList<Song>>{
	ProgressDialog progressDialog;
	Context context;
	MainActivity mainActivity;
	ArrayList<Song> allSongsList;
	boolean titleSearch=false;
	boolean lyricsSearch=false;
	ArrayList<String> searchString;
	public Searcher(Context context, MainActivity mainActivity,ArrayList<Song> allSongList,boolean titleSearch,boolean lyricsSearch, ArrayList<String> searchString){
		this.context=context;
		this.mainActivity=mainActivity;
		this.allSongsList=allSongList;
		this.titleSearch=titleSearch;
		this.lyricsSearch=lyricsSearch;
		this.searchString=searchString;
	}



	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		progressDialog = ProgressDialog.show(context, "Söker","", true);          
	}; 
	@Override
	protected void onPostExecute(ArrayList<Song> songList) {
		progressDialog.dismiss();
		mainActivity.populateListView(songList);
	}

	@Override
	protected ArrayList<Song> doInBackground(Void... params) {

		ArrayList<SongAndMatching> songWithMatching = new ArrayList<SongAndMatching>();
		ArrayList<SongAndMatching> songWithMatchingFullMatch = new ArrayList<SongAndMatching>();



		//bara lägga in de i sökresultaten om de matchar alla orden? (kanske bara lägga in de som matchar alla orden om det finns någon sån match? annars ta del matcher?)
		int score=0;
		int perfectScoreMatch=0;
		boolean fullMatch=false;
		for(int i=0;i<allSongsList.size();i++){
			if(titleSearch){
				score=Search.SimpleSearchFuzzy(searchString, allSongsList.get(i).titleAsArrayList());
			}
			else if(lyricsSearch){
				score=Search.SimpleSearch(searchString, allSongsList.get(i).textAsArrayList());
			}
			else{
				score=Search.SimpleSearchFuzzy(searchString, allSongsList.get(i).melodyAsArrayList());
			}


			if(score==allSongsList.get(i).titleAsArrayList().size()&&titleSearch&&allSongsList.get(i).titleAsArrayList().size()==searchString.size()){
				perfectScoreMatch=score;
			}
			if(score==searchString.size()){
				songWithMatchingFullMatch.add(new SongAndMatching(score,allSongsList.get(i)));

			}
			if(score!=0){
				songWithMatching.add(new SongAndMatching(score,allSongsList.get(i)));
			}
		}

		if(songWithMatchingFullMatch.size()==1){
			if(perfectScoreMatch==songWithMatchingFullMatch.get(0).song.titleAsArrayList().size()&&titleSearch&&songWithMatchingFullMatch.get(0).song.titleAsArrayList().size()==searchString.size()){

				Intent intent = new Intent(context, SongPane.class);
				intent.putExtra("Song", songWithMatchingFullMatch.get(0).song);
				MainActivity.index=allSongsList.indexOf(songWithMatchingFullMatch.get(0).song);
				intent.putExtra("index",MainActivity.index);
				StaticBoolean.addedFavorite=false;
				context.startActivity(intent);
			}
		}
		if(songWithMatchingFullMatch.size()!=0){
			songWithMatching=songWithMatchingFullMatch;
		}

		Collections.sort(songWithMatching, new Comparator<SongAndMatching>() {
			@Override
			public int compare(SongAndMatching  a, SongAndMatching  b)
			{

				if(a.matching==b.matching){
					//inte a.song.compareTo(b.song) här eftersom vi sen vänder på allt innan vi visar upp det(därför måste denna vara felvänd först)
					return b.song.compareTo(a.song);
				}
				else if(a.matching<b.matching){
					return -1;
				}
				else {
					return 1;
				}


			}
		});
		ArrayList<Song> activeSongList= new ArrayList<Song>();
		for(int i=0;i<songWithMatching.size();i++){

			activeSongList.add(songWithMatching.get(i).song);
		}


		Collections.reverse(activeSongList);
		return activeSongList;



	}
}