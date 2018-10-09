package com.sjung.sjungbok;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements AsyncTaskCompleteListener<Boolean>{

    ListView listView;
    HashSet<String> existingSongs;

    ArrayList<Song> activeSongList;
    static ArrayList<Song> allSongsList;
    static ArrayList<Song> snuskigaVisor;

    boolean hasSearched;
    boolean isInFavoriteList = false;
    boolean showHistory = false;
    ArrayAdapter<Song> adapter;
    Parcelable state;
    String menuTitle;
    int position;
    private AlertDialog.Builder builder;
    static int index;
    private Context currentContext;
    private HashSet<String> categories;
    private String categoryToShow;
    private int currentSort = 0;


    private DrawerLayout mDrawerLayout;
    private View clickedView;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    colorArrayAdapter colorAdapter;

    private Menu menu;

    private int versionOfApp = -1;
    private int versionOfAppSaved = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(false);

        setContentView(R.layout.activity_main2);

        //vi använder MainActivityn för att visa många av de olika vyerna, borde nog göra om med fragments
        Intent intent = getIntent();
        isInFavoriteList = intent.getBooleanExtra("openFavoriteList", false);
        showHistory = intent.getBooleanExtra("showHistoryList", false);
        String temp = intent.getStringExtra("menuTitle");
        if (temp != null) {
            menuTitle = temp;
        } else {
            menuTitle = "Sånger";
        }
        getActionBar().setTitle(menuTitle);
        position = intent.getIntExtra("position", 0);
        fixDrawerMenuStuff();


        categories = new HashSet<String>();
        existingSongs = new HashSet<String>();
        hasSearched = false;


        SharedPreferences settings = getSharedPreferences("SETTINGS", 0);
        versionOfAppSaved = settings.getInt("VERSION", -1);

        boolean firstStart = settings.getBoolean("FIRSTSTART", true);
        if (firstStart) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("FIRSTSTART", false);
            editor.commit();
//            Toast.makeText(getBaseContext(), "Sången \""+longClickedSong.getTitle()+"\" bortagen från favoriterna", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Nu finns möjlighet att logga in på ditt Datatekniksektionen konto i appen, genom att göra det får du tillgång till låtar som kan vara stötande. Om du inte är medlem eller vill se sådanna sånger så är det bara att inte logga in.")
                    .setTitle("");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }


        try {
            PackageInfo pInfo;
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionOfApp = pInfo.versionCode;
        } catch (NameNotFoundException e) {

            e.printStackTrace();
        }

        if (versionOfApp > versionOfAppSaved) {
            //det har kommit en ny version av appen.
            copyInitialSongFile();
            settings = getSharedPreferences("SETTINGS", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("VERSION", versionOfApp);
            editor.commit();
            versionOfAppSaved = versionOfApp;
        }


        if (!ifSongFileExists()) {
            copyInitialSongFile();

        }


        if (SongListWrapper.songList != null) {
            System.out.println("populera SongListWrappern? 2");
            allSongsList = SongListWrapper.songList;
            activeSongList = new ArrayList<Song>();
            activeSongList.addAll(allSongsList);
            categories = SongListWrapper.categories;

        } else {
            System.out.println("populera SongListWrappern?");
            populateSongListArrayAndHashSetFromFile();
            SongListWrapper.songList = allSongsList;
            SongListWrapper.categories = categories;
            System.out.println("size: " + SongListWrapper.songList.size());
        }


    }

    protected void onResume() {
        super.onResume();


        currentContext = this;

        listView = (ListView) findViewById(R.id.SongView);
        listView.setFastScrollEnabled(true);

        Intent intent = getIntent();

        String title = intent.getStringExtra("title");
        String melody = intent.getStringExtra("melody");
        String lyric = intent.getStringExtra("lyrics");
        categoryToShow = intent.getStringExtra("category");


        if (StaticBoolean.addedFavorite) {
            StaticBoolean.addedFavorite = false;
            allSongsList.get(index).makeFavorite();
        }


        if (isInFavoriteList) {
            showFavorites();

        } else if (showHistory) {
            showHistory();


        } else if (title != null || melody != null || lyric != null) {
            //genomför en sökning

            if (!hasSearched) {
                String searchString;
                boolean titleSearch = false;
                boolean lyricsSearch = false;
                if (title != null) {
                    searchString = title;
                    titleSearch = true;
                } else if (melody != null) {
                    searchString = melody;
                } else {
                    searchString = lyric;
                    lyricsSearch = true;
                }

                String[] array = searchString.split("\\s");
                ArrayList<String> searchStringSplitted = new ArrayList<String>(Arrays.asList(array));
                searchSongs(searchStringSplitted, titleSearch, lyricsSearch);
                hasSearched = true;
            }


        } else if (categoryToShow != null) {
            activeSongList = new ArrayList<Song>();
            Song temp;
            for (int i = 0; i < allSongsList.size(); i++) {
                temp = allSongsList.get(i);
                if (temp.getCategory().equals(categoryToShow)) {
                    activeSongList.add(temp);
                }
            }
            populateListView(activeSongList);
        } else {
            populateListView(activeSongList);

        }
    }


    private boolean ifSongFileExists() {
        File songFile = new File(this.getFilesDir() + File.separator + "Songs.txt");
        return songFile.exists();

    }

    private void copyInitialSongFile() {
        System.out.println("kopierar startfilen");

        BufferedWriter bufferedWriter;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.songlist), "UTF-8"));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.getFilesDir() + File.separator + "Songs.txt"), "UTF-8"));
            String line = reader.readLine();
            while (line != null) {
                bufferedWriter.write(line + "\n");
                line = reader.readLine();
            }

            bufferedWriter.close();
            reader.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public void searchSongs(View v) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);


    }

    public void sortTitle(View v) {
        sortTitle();
    }

    private void sortTitle() {
        Collections.sort(activeSongList, new Comparator<Song>() {

            public int compare(Song a, Song b) {
                return a.compareTo(b);
            }
        });
        populateListView(activeSongList);
    }

    public void showFavorites(View v) {
        showFavorites();
    }

    private void showFavorites() {
        activeSongList = new ArrayList<Song>();
        for (int i = 0; i < allSongsList.size(); i++) {
            if (allSongsList.get(i).isFavorite()) {
                activeSongList.add(allSongsList.get(i));
            }
        }
        if (activeSongList.size() == 0) {
            showHelpNoFavorites();
        }
        isInFavoriteList = true;
        populateListView(activeSongList);
    }

    private void showHistory() {
        activeSongList = new ArrayList<Song>();
        ArrayList<String> historyListIndex = new ArrayList<String>();
        if (checkIfHistoryFileExists()) {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(getFilesDir() + File.separator + "History.txt"));
                String line = reader.readLine();
                while (line != null) {
                    if (!line.equals("")) {
                        historyListIndex.add(line);
                    }
                    line = reader.readLine();
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = 0; i < historyListIndex.size(); i++) {
                System.out.println(historyListIndex.get(i));
                activeSongList.add(allSongsList.get(Integer.parseInt(historyListIndex.get(i))));
            }
        }

        populateListView(activeSongList);

    }

    private boolean checkIfHistoryFileExists() {
        File historyFile = new File(getFilesDir() + File.separator + "History.txt");
        return historyFile.exists();

    }


    public void sortMelody(View v) {
        sortMelody();
    }

    private void sortMelody() {
        Collections.sort(activeSongList, new Comparator<Song>() {

            public int compare(Song a, Song b) {
                return a.compareToMelody(b);
            }
        });
        populateListView(activeSongList);
    }

    private void sortDate() {
        Collections.sort(activeSongList, new Comparator<Song>() {

            public int compare(Song a, Song b) {
                //tvärtom här för vi vill ha senaste överst
                int compareToResult = b.compareToDate(a);
                if (compareToResult == 0) {
                    return b.compareTo(a);
                }
                return b.compareToDate(a);
            }
        });
        populateListView(activeSongList);
    }

    public void searchSongs(ArrayList<String> searchString, boolean titleSearch, boolean lyricsSearch) {

        Searcher searcher = new Searcher(this, this, allSongsList, titleSearch, lyricsSearch, searchString);
        searcher.execute();


    }

    private void populateSongListArrayAndHashSetFromFile() {
        activeSongList = new ArrayList<Song>();
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
                    existingSongs.add(line);
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
                    line = reader.readLine();
                    categories.add(line);
                    categoryList.add(line);
                    reader.readLine();
                } else if (line.equals("<date>")) {
                    line = reader.readLine();
                    dateList.add(Long.parseLong(line));
                    reader.readLine();
                } else if (line.equals("<midfile>")) {
                    line = reader.readLine();
                    midFileList.add(line);
                    reader.readLine();
                } else if (line.equals("<forall>")) {
                    line = reader.readLine();
                    forAllList.add(Boolean.valueOf(line));
                    reader.readLine();
                }

                line = reader.readLine();

            }
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        SharedPreferences settings = getSharedPreferences("SETTINGS", 0);
        boolean loggedIn = settings.getBoolean("LoggedIn", false);
        snuskigaVisor = new ArrayList<Song>();

        for (int i = 0; i < titleList.size(); i++) {
            boolean forAll = forAllList.get(i);
            if (!loggedIn && !forAll) {
                snuskigaVisor.add(new Song(titleList.get(i), melodyList.get(i), lyricList.get(i), favoriteList.get(i), categoryList.get(i), dateList.get(i), midFileList.get(i), forAll));
            } else {
                activeSongList.add(new Song(titleList.get(i), melodyList.get(i), lyricList.get(i), favoriteList.get(i), categoryList.get(i), dateList.get(i), midFileList.get(i),forAll));
                allSongsList.add(new Song(titleList.get(i), melodyList.get(i), lyricList.get(i), favoriteList.get(i), categoryList.get(i), dateList.get(i), midFileList.get(i),forAll));
            }

        }



    }

    public void populateListView(ArrayList<Song> songList) {

        toggleNOSongMessage(songList);
        activeSongList = new ArrayList<Song>(songList);


        adapter = new SimplerSongArrayAdapter(this, activeSongList);

        SongArrayAdapter adapter2 = new SongArrayAdapter(this, activeSongList, currentSort);


//        if(currentSort==2){
        listView.setAdapter(adapter);
//        else{
//            listView.setAdapter(adapter2);
//        }
        listView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song clickedSong = activeSongList.get(position);
                Intent intent = new Intent(view.getContext(), SongPane.class);
                intent.putExtra("Song", clickedSong);


                // vi behöver rätt index
                index = allSongsList.indexOf(activeSongList.get(position));

                intent.putExtra("index", index);
                StaticBoolean.addedFavorite = false;
                state = listView.onSaveInstanceState();
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id) {


                Song longClickedSong = activeSongList.get(position);

                if (isInFavoriteList) {
                    longClickedSong.unFavorite();
                    Toast.makeText(getBaseContext(), "Sången \"" + longClickedSong.getTitle() + "\" bortagen från favoriterna", Toast.LENGTH_LONG).show();
                    activeSongList.remove(position);
                    adapter.notifyDataSetChanged();
                    toggleNOSongMessage(activeSongList);
                } else if (showHistory) {
                    showHistoryOptionsDialog(position);
                } else {
                    if (!longClickedSong.isFavorite()) {
                        longClickedSong.makeFavorite();
                        Toast.makeText(getBaseContext(), "Sången \"" + longClickedSong.getTitle() + "\" har lagts till i favoriter", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getBaseContext(), "Sången \"" + longClickedSong.getTitle() + "\" finns redan i favoriter", Toast.LENGTH_LONG).show();
                    }

                }
                writeAllSongsToFile();

                return true;
            }
        });


        if (state != null) {
            listView.onRestoreInstanceState(state);
        }

    }


    //används inte just nu
    private void downloadSongs() {
        if (isNetworkAvailable()) {
            JSONDownloader downloader = new JSONDownloader(this);
            downloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            Toast toast = Toast.makeText(this, "Aktivera Internet för att hämta sånger", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private void writeAllSongsToFile() {
        allSongsList.addAll(snuskigaVisor);

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

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_titelSort) {
            currentSort = 0;
            sortTitle();
        } else if (item.getItemId() == R.id.action_melodySort) {
            currentSort = 1;
            sortMelody();
        } else if (item.getItemId() == R.id.action_dateSort) {
            currentSort = 2;
            sortDate();
        } else if (item.getItemId() == R.id.action_showCategories) {
            Intent intent = new Intent(MainActivity.this, CategoryActivity.class);

            ArrayList<String> categoriesAsArrayList = new ArrayList<String>();
            for (String s : categories) {
                categoriesAsArrayList.add(s);
            }
            Collections.sort(categoriesAsArrayList);

            intent.putStringArrayListExtra("categories", categoriesAsArrayList);


            startActivity(intent);
        } else if (item.getItemId() == R.id.action_downlodSongs) {
            downloadSongs();
        } else if (item.getItemId() == R.id.action_logIn) {
            if (item.getTitle().equals("Logga in")) {
                if (isNetworkAvailable()) {
                    tryLogin(item);
                } else {
                    Toast toast = Toast.makeText(this, "Aktivera Internet för att logga in", Toast.LENGTH_SHORT);
                    toast.show();
                }


            } else {
                logout();
                item.setTitle("Logga in");
                resetHistory();
                populateSongListArrayAndHashSetFromFile();
                populateListView(activeSongList);
                SongListWrapper.songList=allSongsList;

            }
        } else if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return true;


    }
    private void resetHistory(){
        BufferedWriter bufferedWriter;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getFilesDir() + File.separator + "History.txt"), "UTF-8"));
            bufferedWriter.write("");
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void tryLogin(final MenuItem item) {
        final MainActivity temp = this;
        LayoutInflater li = LayoutInflater.from(this);
        final View promptsView = li.inflate(R.layout.prompts, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(promptsView);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                EditText usernameEditText = (EditText) promptsView.findViewById(R.id.username);
                EditText passwordEditText = (EditText) promptsView.findViewById(R.id.password);
                LoginTask loginTask = new LoginTask(currentContext, usernameEditText.getText().toString(), passwordEditText.getText().toString(), item,temp);
                loginTask.execute();
            }
        });
        builder.show();

    }

    private void logout() {
        SharedPreferences settings = getSharedPreferences("SETTINGS", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("LoggedIn", false);
        editor.commit();
        Toast.makeText(this, "Utloggad", Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);


        System.out.println(menuTitle);
        menu.findItem(R.id.action_titelSort).setVisible((menuTitle.equals("Sånger") || menuTitle.equals("Favoriter") || menuTitle.equals("Sökresultat")) && !drawerOpen);
        menu.findItem(R.id.action_melodySort).setVisible((menuTitle.equals("Sånger") || menuTitle.equals("Favoriter") || menuTitle.equals("Sökresultat")) && !drawerOpen);
        menu.findItem(R.id.action_dateSort).setVisible((menuTitle.equals("Sånger") || menuTitle.equals("Favoriter") || menuTitle.equals("Sökresultat")) && !drawerOpen);
        menu.findItem(R.id.action_showCategories).setVisible(!drawerOpen && menuTitle.equals("Sånger"));
        menu.findItem(R.id.action_downlodSongs).setVisible(menuTitle.equals("Sånger") && !drawerOpen);
        menu.findItem(R.id.action_logIn).setVisible(menuTitle.equals("Sånger") && !drawerOpen);


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        SharedPreferences settings = getSharedPreferences("SETTINGS", 0);
        boolean loggedIn = settings.getBoolean("LoggedIn", false);
        if (loggedIn) {
            MenuItem loggedInMenuItem = menu.findItem(R.id.action_logIn);
            loggedInMenuItem.setTitle("Logga ut");
        }
        return true;
    }

    private void fixDrawerMenuStuff() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open,
                R.string.drawer_close) {


            public void onDrawerClosed(View view) {
                getActionBar().setTitle(menuTitle);
                invalidateOptionsMenu();

            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(menuTitle);


                invalidateOptionsMenu();
            }

        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        colorArrayAdapter colorAdapter = new colorArrayAdapter(getBaseContext(),
                getResources().getStringArray(R.array.menus), position);


        mDrawerList.setAdapter(colorAdapter);

        getActionBar().setHomeButtonEnabled(true);


        getActionBar().setDisplayHomeAsUpEnabled(true);

        //borde byggas om med menyn som ett fragment
        mDrawerList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == 0) {
                    view.setBackgroundColor(Color.parseColor("#33B5E5"));
                    clickedView = view;
                    mDrawerLayout.closeDrawers();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (!menuTitle.equals("Sånger")) {
                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                intent.putExtra("menuTitle", "Sånger");
                                clickedView.setBackgroundColor(Color.parseColor("#F280A1"));
                                startActivity(intent);
                            } else {
                                clickedView.setBackgroundColor(Color.parseColor("#E16990"));
                            }


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
                            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
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
                            if (!menuTitle.equals("Favoriter")) {
                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                intent.putExtra("openFavoriteList", true);
                                intent.putExtra("menuTitle", "Favoriter");
                                intent.putExtra("position", 2);
                                clickedView.setBackgroundColor(Color.parseColor("#F280A1"));
                                startActivity(intent);
                            } else {

                                clickedView.setBackgroundColor(Color.parseColor("#E16990"));
                            }

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
                            if (!menuTitle.equals("Historik")) {
                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                intent.putExtra("menuTitle", "Historik");
                                intent.putExtra("showHistoryList", true);
                                intent.putExtra("position", 3);
                                clickedView.setBackgroundColor(Color.parseColor("#F280A1"));
                                startActivity(intent);
                            } else {
                                clickedView.setBackgroundColor(Color.parseColor("#E16990"));
                            }
                        }
                    }, 200);
                }


            }
        });
    }

    public void showHelpNoFavorites() {
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Här var det tomt.\n\nDu kan lägga till sånger från sånglistan genom att \"Long click:a\" på en sång i sånglistan.\n\nEller genom att gå in på en sång och sen klicka på menyknappen längst uppe till höger och välja \"lägg till i favoriter\"")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void showHistoryOptionsDialog(final int position) {
        builder = new AlertDialog.Builder(this);
        String[] choices = new String[2];
        choices[0] = "Lägg till i favoriter";
        choices[1] = "Ta bort från historiken";
        builder.setItems(choices, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Song tempSong = activeSongList.get(position);
                    if (!tempSong.isFavorite()) {
                        tempSong.makeFavorite();
                        Toast.makeText(getBaseContext(), "Sången \"" + tempSong.getTitle() + "\" har lagts till i favoriter", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getBaseContext(), "Sången \"" + tempSong.getTitle() + "\" finns redan i favoriter", Toast.LENGTH_LONG).show();
                    }
                    writeAllSongsToFile();

                } else {
                    HistoryWriter historyWriter = new HistoryWriter(currentContext, allSongsList.indexOf(activeSongList.get(position)), true);
                    historyWriter.execute();
                    activeSongList.remove(position);
                    adapter.notifyDataSetChanged();
                    toggleNOSongMessage(activeSongList);
                }
            }
        });

        builder.show();
    }

    void toggleNOSongMessage(ArrayList<Song> songList) {
        TextView noSongTV = (TextView) findViewById(R.id.no_listed_song_tv);
        if (songList == null || songList.size() == 0) {
            if (isInFavoriteList) {
                noSongTV.setText("Inga favoriter existerar..");
            } else if (showHistory) {
                noSongTV.setText("Ingen historik existerar..");
            } else {
                noSongTV.setText("Inga sånger matchade din sökning");
            }
            noSongTV.setVisibility(View.VISIBLE);
        } else {
            noSongTV.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onTaskComplete(Boolean result) {
        populateSongListArrayAndHashSetFromFile();
        populateListView(activeSongList);
        SongListWrapper.songList=allSongsList;
    }
}
