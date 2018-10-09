package com.sjung.sjungbok;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.sjung.sjungbok.R;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.view.KeyEvent;

public class SearchActivity extends Activity {
	
	

	EditText titleSearchString;
	EditText melodySearchString;
	EditText lyricSearchString;
	
	
	private DrawerLayout mDrawerLayout;
	private View clickedView;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	colorArrayAdapter colorAdapter ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_search2);
		getActionBar().setTitle("Sök");
		fixDrawerMenuStuff();
		
		titleSearchString   = (EditText)findViewById(R.id.titel);
		titleSearchString.setHorizontallyScrolling(false);
		titleSearchString.setMaxLines(Integer.MAX_VALUE);
		titleSearchString.setOnEditorActionListener(new TextView.OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
		        	actuallSearch();
		            return true;
		        }
		        return false;
		    }
		});
		melodySearchString   = (EditText)findViewById(R.id.melodi);
		melodySearchString.setHorizontallyScrolling(false);
		melodySearchString.setMaxLines(Integer.MAX_VALUE);
		melodySearchString.setOnEditorActionListener(new TextView.OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
		        	actuallSearch();
		            return true;
		        }
		        return false;
		    }
		});
		lyricSearchString   = (EditText)findViewById(R.id.text);
		lyricSearchString.setHorizontallyScrolling(false);
		lyricSearchString.setMaxLines(Integer.MAX_VALUE);
		lyricSearchString.setOnEditorActionListener(new TextView.OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
		        	actuallSearch();
		            return true;
		        }
		        return false;
		    }
		});
	}


	public void searchForSongs(View v){
		actuallSearch();
	}
	
	private void actuallSearch(){

		titleSearchString   = (EditText)findViewById(R.id.titel);
		melodySearchString   = (EditText)findViewById(R.id.melodi);
		lyricSearchString   = (EditText)findViewById(R.id.text);
		if(titleSearchString.getText().length()==0&&melodySearchString.getText().length()==0&&lyricSearchString.getText().length()==0){
			
			Toast toast = Toast.makeText(getApplicationContext(), "Skriv in ett värde", Toast.LENGTH_SHORT);
			toast.show();
		}
		else if(titleSearchString.getText().length()!=0&&melodySearchString.getText().length()!=0){
			Toast toast = Toast.makeText(getApplicationContext(), "Fyll bara i ett av fälten", Toast.LENGTH_SHORT);
			toast.show();
		}
		else if(titleSearchString.getText().length()!=0&&lyricSearchString.getText().length()!=0){
			Toast toast = Toast.makeText(getApplicationContext(), "Fyll bara i ett av fälten", Toast.LENGTH_SHORT);
			toast.show();
		}
		else if(melodySearchString.getText().length()!=0&&lyricSearchString.getText().length()!=0){
			Toast toast = Toast.makeText(getApplicationContext(), "Fyll bara i ett av fälten", Toast.LENGTH_SHORT);
			toast.show();
		}
		else{

			Intent intent = new Intent(this, MainActivity.class);
			if(titleSearchString.getText().length()!=0){
				intent.putExtra("title", titleSearchString.getText().toString().toLowerCase().trim());
			}
			if(melodySearchString.getText().length()!=0){
				intent.putExtra("melody", melodySearchString.getText().toString().toLowerCase().trim());
			}
			if(lyricSearchString.getText().length()!=0){
				intent.putExtra("lyrics", lyricSearchString.getText().toString().toLowerCase().trim());
			}
			intent.putExtra("position", -1);
			intent.putExtra("menuTitle", "Sökresultat");
			startActivity(intent);
			
			
		}
	}

	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return true;
		//return super.onOptionsItemSelected(item);

	}




	private void fixDrawerMenuStuff(){
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// Getting reference to the ActionBarDrawerToggle
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {


			public void onDrawerClosed(View view) {

			}

		
			public void onDrawerOpened(View drawerView) {

			}

		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		colorArrayAdapter colorAdapter = new colorArrayAdapter(getBaseContext(), 
				getResources().getStringArray(R.array.menus),1);

	
		mDrawerList.setAdapter(colorAdapter);

		getActionBar().setHomeButtonEnabled(true);

		getActionBar().setDisplayHomeAsUpEnabled(true); 
		//samma lösning här som borde byggas om.
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(final AdapterView<?> parent, View view,
					int position, long id) {
				if(position==0){
					view.setBackgroundColor(Color.parseColor("#33B5E5"));
					clickedView=view;
					mDrawerLayout.closeDrawers();
					final Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {

							
							Intent intent=new Intent(SearchActivity.this,MainActivity.class);	

							clickedView.setBackgroundColor(Color.parseColor("#F280A1"));
							
							startActivity(intent);
							


						}
					}, 200);

				}
				else if(position==1){
					view.setBackgroundColor(Color.parseColor("#33B5E5"));
					clickedView=view;
					mDrawerLayout.closeDrawers();
					final Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {

							clickedView.setBackgroundColor(Color.parseColor("#E16990"));

						}
					}, 200);
				}
				else if(position==2){
					view.setBackgroundColor(Color.parseColor("#33B5E5"));
					clickedView=view;
					mDrawerLayout.closeDrawers();
					final Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {

							Intent intent=new Intent(SearchActivity.this,MainActivity.class);	
							intent.putExtra("openFavoriteList", true);
							intent.putExtra("menuTitle", "Favoriter");
							intent.putExtra("position", 2);
							clickedView.setBackgroundColor(Color.parseColor("#F280A1"));
							startActivity(intent);
							
							
							
						}
					}, 200);
				}
				else if(position==3){
					view.setBackgroundColor(Color.parseColor("#33B5E5"));
					clickedView=view;
					mDrawerLayout.closeDrawers();
					final Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							
								Intent intent=new Intent(SearchActivity.this,MainActivity.class);	
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
