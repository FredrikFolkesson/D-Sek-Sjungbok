//package com.sjung.sjungbok;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.io.StringReader;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashSet;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.widget.TextView;
//
//
//public class DownloadListener implements AsyncTaskCompleteListener<String> {
//    Downloader downloader;
//    HashSet existingSongs;
//    ArrayList<String> titleList;
//    ArrayList<Song> songs;
//    Context context;
//
//    DownloadListener(ArrayList<Song> songs, HashSet existingSongs, Context context) {
//        this.songs = songs;
//        this.existingSongs = existingSongs;
//        this.context = context;
//        downloader = new Downloader(existingSongs, context, this);
//        downloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//    }
//
//    @Override
//    public void onTaskComplete(String result) {
//        //läs in hela listan, lägg till de nya och sortera det och skriv det sen till disk?
//
//
//        addSongObjectsToArrayListFromString(songs, result);
//
//        Collections.sort(songs, new Comparator<Song>() {
//            @Override
//            public int compare(Song a, Song b) {
//                return a.compareTo(b);
////				if(a.matching==b.matching){
////					//inte a.song.compareTo(b.song) här eftersom vi sen vänder på allt innan vi visar upp det(därför måste denna vara felvänd först)
////					return b.song.compareTo(a.song);
//////					return 0;
////				}
////				else if(a.matching<b.matching){
////					return -1;
////				}
////				else {
////					return 1;
////				}
//
//
//            }
//        });
//
//
//        BufferedWriter bufferedWriter;
//        try {
//            //bufferedWriter = new BufferedWriter(new FileWriter(new File(context.getFilesDir()+File.separator+"Songs.txt"),true));
////			bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(context.getFilesDir()+File.separator+"Songs.txt",true),"UTF-8"));
//            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(context.getFilesDir() + File.separator + "Songs.txt"), "UTF-8"));
////			bufferedWriter.write(result);
//            for (int i = 0; i < songs.size(); i++)
//                bufferedWriter.write(songs.get(i).writeToFileFormat());
//            bufferedWriter.close();
//        } catch (IOException e) {
//
//            e.printStackTrace();
//        }
//
//
//    }
//
//    private void addSongObjectsToArrayListFromString(ArrayList<Song> songList, String songsInString) {
//
//        ArrayList<String> titleList = new ArrayList<String>();
//        ArrayList<String> melodyList = new ArrayList<String>();
//        ArrayList<String> lyricList = new ArrayList<String>();
//        ArrayList<Boolean> favoriteList = new ArrayList<Boolean>();
//        ArrayList<String> categoryList = new ArrayList<String>();
//        ArrayList<String> dateList = new ArrayList<String>();
//        ArrayList<String> midFileList = new ArrayList<String>();
//        StringBuilder sb = new StringBuilder();
//
//        try {
//
//            BufferedReader reader = new BufferedReader(new StringReader(songsInString));
//            String line = reader.readLine();
//
//            while (line != null) {
//
//
//                if (line.equals("<title>")) {
//                    line = reader.readLine();
//                    titleList.add(line);
//                    existingSongs.add(line);
//                    reader.readLine();
//                } else if (line.equals("<melody>")) {
//                    melodyList.add(reader.readLine());
//                    reader.readLine();
//                } else if (line.equals("<lyrics>")) {
//                    line = reader.readLine();
//
//                    sb = new StringBuilder();
//                    while (!line.equals("</lyrics>")) {
//
//                        sb.append(line + "\n");
//                        line = reader.readLine();
//                    }
//                    lyricList.add(sb.toString());
//
//                } else if (line.equals("<favorite>")) {
//                    favoriteList.add(Boolean.parseBoolean(reader.readLine()));
//                    reader.readLine();
//                } else if (line.equals("<category>")) {
//                    categoryList.add(reader.readLine());
//                    reader.readLine();
//                } else if (line.equals("<date>")) {
//                    line = reader.readLine();
//                    dateList.add(line);
//                    reader.readLine();
//                } else if (line.equals("<midfile>")) {
//                    line = reader.readLine();
//                    midFileList.add(line);
//                    reader.readLine();
//                }
//                line = reader.readLine();
//            }
//            reader.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("fel här?");
//        }
//        for (int i = 0; i < titleList.size(); i++) {
//            songList.add(new Song(titleList.get(i), melodyList.get(i), lyricList.get(i), favoriteList.get(i), categoryList.get(i), dateList.get(i), midFileList.get(i)));
//
//        }
//
//
//    }
//
//}
