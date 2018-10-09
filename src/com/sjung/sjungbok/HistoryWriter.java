package com.sjung.sjungbok;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;



import android.content.Context;

import android.os.AsyncTask;

public class HistoryWriter extends AsyncTask<Void, Void, Void>{
	Context context;
	ArrayList<String> songHistory;
	int currentIndex;
	BufferedWriter bufferedWriter;
	boolean remove;
	public HistoryWriter(Context context,int currentIndex, boolean remove){
		this.context=context;
		this.currentIndex=currentIndex;
		songHistory = new ArrayList<String>();
		this.remove=remove;
	}



	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();

	}; 
	@Override
	protected void onPostExecute(Void v) {

	}

	
	//ser till så att vi har 10 senaste låtarna i historiken
	@Override
	protected Void doInBackground(Void... params) {



		if(checkIfHistoryFileExists()){
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(context.getFilesDir()+File.separator+"History.txt"));
				String line=reader.readLine();

				while(line!=null){
					if(!line.equals("")){
						songHistory.add(line);
					}

					line=reader.readLine();
				}				
				reader.close();
			} catch (Exception e) {		
				e.printStackTrace();
			}


			if(!remove){
				if(songHistory.contains(Integer.toString(currentIndex))){
					songHistory.remove(Integer.toString(currentIndex));
					songHistory.add(0, Integer.toString(currentIndex));
				}
				else{
					if(songHistory.size()<10){
						songHistory.add(0, Integer.toString(currentIndex));
					}
					else{
						songHistory.remove(9);
						songHistory.add(0, Integer.toString(currentIndex));

					}
				}

			}
			else{

				songHistory.remove(Integer.toString(currentIndex));
			}

		}
		else{
			songHistory.add(0, Integer.toString(currentIndex));
		}


		
		
		
		try {
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(context.getFilesDir()+File.separator+"History.txt"),"UTF-8"));
			for(int i=0;i<songHistory.size();i++){
				bufferedWriter.write(songHistory.get(i)+"\n");
			}

			bufferedWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}





		return null;
	}
	private boolean checkIfHistoryFileExists(){
		File historyFile = new File(context.getFilesDir()+File.separator+"History.txt");
		return historyFile.exists();

	}

}