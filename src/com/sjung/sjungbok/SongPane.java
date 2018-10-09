package com.sjung.sjungbok;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.sjung.sjungbok.R;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SongPane extends Activity {
    TextView textView;


    private DrawerLayout mDrawerLayout;
    private View clickedView;
    // ListView represents Navigation Drawer
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    colorArrayAdapter colorAdapter;
    ArrayList<Song> allSongsList;
    Song song;
    int index;
    static MediaPlayer mediaPlayer;
    MediaPlayerListener listener;
    MenuItem musicIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_pane2);
        Intent intent = getIntent();
        textView = (TextView) findViewById(R.id.songTextView);
        song = (Song) intent.getParcelableExtra("Song");
        index = intent.getIntExtra("index", -2);

        //System.out.println(song.dateString);
        textView.setText(Html.fromHtml(song.songToString()));
        textView.scrollTo(0, textView.getTop());
        getActionBar().setTitle(song.getTitle());

        fixDrawerMenuStuff();

        HistoryWriter historyWriter = new HistoryWriter(this, index, false);
        historyWriter.execute();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    }

    private void populateSongListArrayFromFile() {

        allSongsList = new ArrayList<Song>();
        ArrayList<String> titleList = new ArrayList<String>();
        ArrayList<String> melodyList = new ArrayList<String>();
        ArrayList<String> lyricList = new ArrayList<String>();
        ArrayList<Boolean> favoriteList = new ArrayList<Boolean>();
        ArrayList<String> categoryList = new ArrayList<String>();
        ArrayList<Long> dateList = new ArrayList<Long>();
        ArrayList<String> midFileList = new ArrayList<String>();
        ArrayList<Boolean> forAllList = new ArrayList<Boolean>();

        StringBuilder sb = new StringBuilder();

        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(this.getFilesDir() + File.separator + "Songs.txt")), "UTF-8"));
            String line = reader.readLine();

            while (line != null) {


                if (line.equals("<title>")) {
                    line = reader.readLine();
                    titleList.add(line);
                    reader.readLine();
                } else if (line.equals("<melody>")) {
                    melodyList.add(reader.readLine());
                    reader.readLine();
                } else if (line.equals("<lyrics>")) {
                    line = reader.readLine();

                    sb = new StringBuilder();
                    while (!line.equals("</lyrics>")) {

                        sb.append(line + "\n");
                        line = reader.readLine();
                    }
                    lyricList.add(sb.toString());

                } else if (line.equals("<favorite>")) {

                    favoriteList.add(Boolean.parseBoolean(reader.readLine()));
                    reader.readLine();
                } else if (line.equals("<category>")) {
                    categoryList.add(reader.readLine());
                    reader.readLine();
                } else if (line.equals("<date>")) {
                    line = reader.readLine();
                    dateList.add(Long.parseLong(line));
                    reader.readLine();
                } else if (line.equals("<midfile>")) {
                    line = reader.readLine();
                    midFileList.add(line);
                    reader.readLine();
                }
                else if (line.equals("<forall>")) {
                    line = reader.readLine();
                    forAllList.add(Boolean.valueOf(line));
                    reader.readLine();
                }

                line = reader.readLine();
            }
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("fel här?");
        }
        for (int i = 0; i < titleList.size(); i++) {
            allSongsList.add(new Song(titleList.get(i), melodyList.get(i), lyricList.get(i), favoriteList.get(i), categoryList.get(i), dateList.get(i), midFileList.get(i),forAllList.get(i)));
        }


    }

    private void writeAllSongsToFile(ArrayList<Song> allSongsList) {

        allSongsList.addAll(MainActivity.snuskigaVisor);
        Collections.sort(allSongsList, new Comparator<Song>() {
            @Override
            public int compare(Song a, Song b) {
                return a.compareTo(b);


            }
        });


        BufferedWriter bufferedWriter;
        try {

            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.getFilesDir() + File.separator + "Songs.txt"), "UTF-8"));

            for (int i = 0; i < allSongsList.size(); i++)
                bufferedWriter.write(allSongsList.get(i).writeToFileFormat());
            bufferedWriter.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }


    private void playSong(MenuItem icon) {


        try {
            System.out.println(this.getFilesDir() + File.separator + song.getMidFile());
            if (song.getMidFile().contains("__downloaded")) {
                mediaPlayer = MediaPlayer.create(this, Uri.parse(this.getFilesDir() + File.separator + song.getMidFile()));
            } else {
                AssetFileDescriptor afd = getAssets().openFd(song.getMidFile());
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mediaPlayer.prepare();
            }


        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mediaPlayer.start();
        listener = new MediaPlayerListener(musicIcon, this);
        mediaPlayer.setOnCompletionListener(listener);


    }

    private void stopSong() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();

            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            stopSong();
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.song_pane, menu);
        return true;
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_addToFavorites) {
            //			sortTitle();

            addToFavorites();
        }
        if (item.getItemId() == R.id.musik) {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {

                    item.setIcon(getResources().getDrawable(R.drawable.greynot));
                    stopSong();
                } else {
                    musicIcon = item.setIcon(getResources().getDrawable(R.drawable.rosanot));
                    playSong(musicIcon);

                    //vi måste sätta listenern efter vi startat sången..


                }
            } else {
                System.out.println("kommer vi hit?");
                musicIcon = item.setIcon(getResources().getDrawable(R.drawable.rosanot));
                playSong(musicIcon);
            }


        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return true;
        //return super.onOptionsItemSelected(item);

    }

    private void addToFavorites() {
        if (!song.isFavorite()) {
            Toast.makeText(getBaseContext(), "Sången \"" + song.getTitle() + "\" har lagts till i favoriter", Toast.LENGTH_LONG).show();


            if (SongListWrapper.songList != null) {
                allSongsList = SongListWrapper.songList;

            } else {
                populateSongListArrayFromFile();
                SongListWrapper.songList = allSongsList;
            }


            Song song1 = allSongsList.get(index);
            song1.makeFavorite();
            song.makeFavorite();
            writeAllSongsToFile(allSongsList);


            StaticBoolean.addedFavorite = true;
        } else {
            Toast.makeText(getBaseContext(), "Sången \"" + song.getTitle() + "\" finns redan i favoriter", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Called whenever we call invalidateOptionsMenu()
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.musik).setVisible(song.hasMidFile() && !drawerOpen);
        //		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        menu.findItem(R.id.action_addToFavorites).setVisible(!drawerOpen);

        return super.onPrepareOptionsMenu(menu);
    }


    private void fixDrawerMenuStuff() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Getting reference to the ActionBarDrawerToggle
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open,
                R.string.drawer_close) {

            /** Called when drawer is closed */
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();

            }

            /** Called when a drawer is opened */
            public void onDrawerOpened(View drawerView) {


                invalidateOptionsMenu();
            }

        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        colorArrayAdapter colorAdapter = new colorArrayAdapter(getBaseContext(),
                getResources().getStringArray(R.array.menus), -1);


        mDrawerList.setAdapter(colorAdapter);

        getActionBar().setHomeButtonEnabled(true);


        getActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, View view,
                                    int position, long id) {
                stopSong();
                if (position == 0) {
                    view.setBackgroundColor(Color.parseColor("#33B5E5"));
                    clickedView = view;
                    mDrawerLayout.closeDrawers();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = new Intent(SongPane.this, MainActivity.class);
                            //							intent.putExtra("menuTitle", "Sånger");
                            clickedView.setBackgroundColor(Color.parseColor("#F280A1"));
                            startActivity(intent);


                        }
                    }, 200);

                } else if (position == 1) {
                    view.setBackgroundColor(Color.parseColor("#33B5E5"));
                    clickedView = view;
                    mDrawerLayout.closeDrawers();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SongPane.this, SearchActivity.class);
                            clickedView.setBackgroundColor(Color.parseColor("#F280A1"));
                            startActivity(intent);
                        }
                    }, 200);
                } else if (position == 2) {
                    view.setBackgroundColor(Color.parseColor("#33B5E5"));
                    clickedView = view;
                    mDrawerLayout.closeDrawers();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = new Intent(SongPane.this, MainActivity.class);
                            intent.putExtra("openFavoriteList", true);
                            intent.putExtra("menuTitle", "Favoriter");
                            intent.putExtra("position", 2);
                            clickedView.setBackgroundColor(Color.parseColor("#F280A1"));
                            startActivity(intent);


                        }
                    }, 200);
                } else if (position == 3) {
                    view.setBackgroundColor(Color.parseColor("#33B5E5"));
                    clickedView = view;
                    mDrawerLayout.closeDrawers();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = new Intent(SongPane.this, MainActivity.class);
                            intent.putExtra("menuTitle", "Historik");
                            intent.putExtra("showHistoryList", true);
                            intent.putExtra("position", 3);
                            clickedView.setBackgroundColor(Color.parseColor("#F280A1"));
                            startActivity(intent);


                        }
                    }, 200);
                }


            }
        });
    }

}
