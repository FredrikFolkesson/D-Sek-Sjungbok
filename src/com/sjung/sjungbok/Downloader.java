package com.sjung.sjungbok;




import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;




//används inte just nu då det tar för lång tid att ladda ner på mobilen.
public class Downloader extends AsyncTask<Void, String, String>{
	ProgressDialog progressDialog;
	Context context;
	HashSet existingSongs;
	private AsyncTaskCompleteListener<String> callback;
	public Downloader(HashSet existingSongs,Context context,AsyncTaskCompleteListener<String> callback){
		this.existingSongs=existingSongs;
		this.context=context;
		this.callback=callback;
	}


	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		progressDialog = ProgressDialog.show(context, "Hämtar Musiken","", true);          
	}; 


	private Response login(){
		Connection.Response res = null;
		try {
			res = Jsoup.connect("http://www.dsek.se/navigation/login.php")
					.data("login", "dat11ffo", "password", "x")
					.method(Method.POST)
					.execute();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return res;
	}
	private Document getDocument(String url,Map<String, String> cookies ){
		Document doc=null;
		try {		

			Connection connection = Jsoup.connect(url);
			for (Map.Entry<String, String> cookie : cookies.entrySet()) {
				connection.cookie(cookie.getKey(), cookie.getValue());     
			}
			doc = connection.get();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
	private String getCorrectSwedishLetters(String text){
		text=text.replace("&aring;", "å");
		text=text.replace("&Aring;", "Å");
		text=text.replace("&auml;", "ä");
		text=text.replace("&Auml;", "Ä");
		text=text.replace("&ouml;", "ö");
		text=text.replace("&Ouml;", "Ö");
		text=text.replace("&quot;", "\"");
		text=text.replaceAll("&#8221;", "\"");
		text=text.replaceAll("&#180;", "'");
		text=text.replaceAll("&#8217;", "’");
		text=text.replace("&#8230;", "...");
		text=text.replace("&#8220;", "\"");
		text=text.replace("&#8221;", "\"");
		text=text.replace("&#65533;", "e");
		
		
		
		return text;
	}
	private String getMelody(Document doc){
		Elements melodier=doc.getElementsContainingOwnText("Melodi");
		Element melodi=melodier.get(0);
		Element parent = melodi.parent();
		Element child=parent.child(1);
		String melodiString="";

		child.html(child.html().replaceAll("(?i)<br[^>]*>", "br2n"));
		if(child.text().contains("Direktlänk till ljudfilen")){
			melodiString = child.text().substring(0, child.text().indexOf("Direktlänk till ljudfilen."));	
			melodiString=getCorrectSwedishLetters(melodiString);
		}
		else{
			melodiString = child.text();
			melodiString=getCorrectSwedishLetters(melodiString);
		}
		
		
		return melodiString;
	}
	private String getLyric(Document doc){
		Element lyrics=doc.getElementById("lyrics");
		lyrics.html(lyrics.html().replaceAll("(?i)<br[^>]*>", "br2n"));
		String lyricText=lyrics.text().replaceAll("br2n", "\n");

		String[] lyricsLines=lyricText.split("\n");
		lyricText="";
		for(int j=0;j<lyricsLines.length;j++){
			lyricText=lyricText+lyricsLines[j].trim()+"\n";
		}
		lyricText=getCorrectSwedishLetters(lyricText);
		return lyricText;
	}
	
	
	@Override
	protected String doInBackground(Void... params) {



		ArrayList<String> songLinkList= new ArrayList<String>();
		ArrayList<String> titles= new ArrayList<String>();
		ArrayList<String> melodies= new ArrayList<String>();
		ArrayList<String> lyrics= new ArrayList<String>();

		Document doc = null;
		Connection.Response res = login();
		doc=getDocument("http://www.dsek.se/arkiv/sanger/index.php?showAll",res.cookies());



		Element tableWithSongs=doc.getElementById("innerMainTable");
		Elements links = tableWithSongs.select("a[href]");


		Iterator<Element> itr = links.iterator();
		while(itr.hasNext()){
			Element element = itr.next();
			
			
			if(!existingSongs.contains(getCorrectSwedishLetters(element.text()))){
			songLinkList.add(element.attr("abs:href"));
			titles.add(getCorrectSwedishLetters(element.html()).trim());
			}
		}
		

		for(int i=0; i<songLinkList.size();i++){
			doc=getDocument(songLinkList.get(i),res.cookies());
			String melody=getMelody(doc).trim();
			melodies.add(melody);
			String lyric=getLyric(doc).trim();
			lyrics.add(lyric);


		}
		if(titles.size()!=melodies.size()||titles.size()!=lyrics.size()){
			System.out.println("something went wrong (debugg check, to be removed)");
		}
		String result="";
		for(int i=0;i<titles.size();i++){
			result=result+"<title>\n"+titles.get(i)+"\n</title>"+"\n";
			result=result+"<melody>\n"+melodies.get(i)+"\n</melody>"+"\n";
			result=result+"<lyrics>\n"+lyrics.get(i)+"\n</lyrics>"+"\n";
		}



		return result;
	}
	@Override
	protected void onPostExecute(String result) {
		
		//borde vi kanske skriva ner vårt resultat till fil redan här? Då kan vi göra allt som ska göras med nedladdning på samam ställe.
		//läs in hela listan, lägg till de nya och sortera det och skriv det sen till disk?
		progressDialog.dismiss();
		callback.onTaskComplete(result);
	}

}
