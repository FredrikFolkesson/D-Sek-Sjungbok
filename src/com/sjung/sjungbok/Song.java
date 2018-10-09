package com.sjung.sjungbok;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {
	
//någon variabel för när sången senast blev uppdaterad 
	private String name;
	private String melody;
	private String text;
	private boolean favorite;
	String category;
    private long lastChanged;

	String midFile;
    private boolean forAll;
	
	public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {

		@Override
		public Song createFromParcel(Parcel source) {
			// Must read values in the same order as they were placed in
			String name = source.readString();
			String melody = source.readString();
			String text = source.readString();
			
			boolean favorite = source.readByte() != 0;
			String category=source.readString();
            long lastChanged = source.readLong();
			//String dateString =source.readString();
			String midFile=source.readString();
            boolean forAll = source.readByte() !=0;
			Song song = new Song(name, melody,text,favorite,category,lastChanged,midFile,forAll);
			return song;
		}

		@Override
		public Song[] newArray(int size) {
			return new Song[size];
		}

	};
	@Override
	public int describeContents() {
		
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(melody);
		dest.writeString(text);
		dest.writeByte((byte) (favorite ? 1 : 0)); 
		dest.writeString(category);
		dest.writeLong(lastChanged);
		dest.writeString(midFile);
        dest.writeByte((byte) (forAll ? 1 : 0));
    }
	
	
	
	
	
	
	
	
	
	
	public Song(String name,String melody,String text, boolean favorite, String category,long lastChanged,String midFile,boolean forAll){
		this.name=name;
		this.melody=melody;
		this.text=text;
		this.favorite=favorite;
		this.category=category;
		this.lastChanged=lastChanged;
		this.midFile=midFile;
        this.forAll=forAll;
		
	}
	public String writeToFileFormat(){
		String result ="";
		result="<title>\n"+name+"\n</title>\n";
		result=result+"<melody>\n"+melody+"\n</melody>\n";
		result=result+"<lyrics>\n"+text.trim()+"\n</lyrics>\n";
		result=result+"<favorite>\n"+favorite+"\n</favorite>\n";
		result=result+"<category>\n"+category+"\n</category>\n";
		result=result+"<date>\n"+lastChanged+"\n</date>\n";
		result=result+"<midfile>\n"+midFile+"\n</midfile>\n";
        result=result+"<forall>\n"+forAll+"\n</forall>\n";
		return result;
	}
	public String songToString(){
		String result ="";
		result="<h4>"+replaceBackslashN(name)+"</h4>";
		result=result+"<i>"+replaceBackslashN(melody)+"</i><br><br>";
		result=result+replaceBackslashN(text)+"<br>";
		return result;
	}
	public String replaceBackslashN(String s){
		return s.replace("\n", "<br>");
	}
	public String toString(){
		String toString ="";
		toString="<b>"+name+"</b><br>";
		toString=toString+"<i>"+melody+"</i>";
		
		
		return toString;
	}
	public ArrayList<String> titleAsArrayList(){
		return getArrayListBySeperatingTextBySpace(name);
		
	}
	public ArrayList<String> melodyAsArrayList(){
		return getArrayListBySeperatingTextBySpace(melody);
		
	}
	public ArrayList<String> textAsArrayList(){
		return getArrayListBySeperatingTextBySpace(text);
		
	}
	public HashSet<String> textAsHashSet(){
			
		
		return new HashSet<String>(getArrayListBySeperatingTextBySpace(text));
	}
	private ArrayList<String> getArrayListBySeperatingTextBySpace(String text){
        text=text.replaceAll("[.,;:!\"?#&()]","");
        String array[] = text.split("\\s");
		ArrayList<String> result= new ArrayList<String>();
		for(int i=0;i<array.length;i++){
			//result.add(array[i].toLowerCase().replaceAll("[.,;:!\"?#&()]",""));
            result.add(array[i].toLowerCase());
		}
		return result;
		
	}
	public int compareTo(Song song) {
		
		if(Character.isLetter(this.name.charAt(0))&&Character.isLetter(song.name.charAt(0))){
			return this.name.compareTo(song.name);
		}
		else if(Character.isLetter(this.name.charAt(0))){
			return -1;
		}
		else if(Character.isLetter(song.name.charAt(0))){
			return 1;
		}
		else{
			return this.name.compareTo(song.name);
		}
		
		
		
	}
	public int compareToMelody(Song song){

		
		
		if(Character.isLetter(this.melody.charAt(0))&&Character.isLetter(song.melody.charAt(0))){
			//return this.melody.compareTo(song.melody);
			if(this.melody.compareTo(song.melody)==0){
				return this.name.compareTo(song.name);
			}
			else{
				return this.melody.compareTo(song.melody);
			}
		}
		else if(Character.isLetter(this.melody.charAt(0))){
			return -1;
		}
		else if(Character.isLetter(song.melody.charAt(0))){
			return 1;
		}
		else{
//			return this.melody.compareTo(song.melody);
			if(this.melody.compareTo(song.melody)==0){
				return this.name.compareTo(song.name);
			}
			else{
				return this.melody.compareTo(song.melody);
			}
		}
	}
	public int compareToDate(Song otherSong){
		if(lastChanged<otherSong.lastChanged){
            return -1;
        }
        else if (lastChanged==otherSong.lastChanged){
            return 0;
        }
        return 1;
	}
    public long getLastChanged(){
        return lastChanged;
    }
	public String getTitle(){
		return name;
	}
	
	public String getMelody(){
		return melody;
	}
	public boolean isFavorite(){
		return favorite;
	}
	public void makeFavorite(){
		favorite=true;
	}
	public void unFavorite(){
		favorite=false;
	}
	public boolean equals(Song other){
		return this.name.equals(other.name);
		
	}
    public boolean forAll(){
        return forAll;
    }
	public String getText(){
		return this.text;
	}
	public String getCategory(){
		return this.category;
	}
    public boolean equals(Object obj) {
//    	System.out.println("Equals körs");
        if (!(obj instanceof Song))
             return false;
         if (obj == this)
             return true;

         Song other = (Song) obj;
         if(this.text.equals(other.text)){
        	 return true;
         }
         return false;
     }
    public boolean hasMidFile(){
    	if(!midFile.equals("")){
    		if(!midFile.equals("null")){
    			return true;
    		}
    	}
    	return false;
    }
    public String getMidFile(){
    	return this.midFile;
    }
	
	
}
