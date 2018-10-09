package com.sjung.sjungbok;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class CategoryActivity extends Activity {
	private DrawerLayout mDrawerLayout;
	private View clickedView;
	private ListView mDrawerList;
	private ListView categoryList;
	private ActionBarDrawerToggle mDrawerToggle;
	colorArrayAdapter colorAdapter ;
	private ArrayList<String>categories;

	@Override
	protected void onCreate(Bundle savedInstanceState) {


		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
		getActionBar().setTitle("Kategorier");
		fixDrawerMenuStuff();
		Intent intent = getIntent();
		categories=intent.getStringArrayListExtra("categories");

		Collections.sort(categories);
		// lägg (ingen kategori) längst bak
//		String temp=categories.get(0);
//		categories.remove(0);
//		categories.add(temp);

		categoryList = (ListView) findViewById(R.id.CategoryView);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				this, 
				android.R.layout.simple_list_item_1,
				categories );

		categoryList.setAdapter(arrayAdapter); 
		categoryList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(view.getContext(), MainActivity.class);
				intent.putExtra("menuTitle", categories.get(position));
				intent.putExtra("category", categories.get(position));
				intent.putExtra("position", -1);
				startActivity(intent);
			}
		}); 

	}
	protected void onResume()
	{
		super.onResume();	}
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


	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		menu.findItem(R.id.action_melodySort).setVisible(false);
		menu.findItem(R.id.action_showCategories).setVisible(false);
		menu.findItem(R.id.action_titelSort).setVisible(false);
		menu.findItem(R.id.action_dateSort).setVisible(false);
        menu.findItem(R.id.action_downlodSongs).setVisible(false);
        menu.findItem(R.id.action_logIn).setVisible(false);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	private void fixDrawerMenuStuff(){
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerList = (ListView) findViewById(R.id.left_drawer);


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
				getResources().getStringArray(R.array.menus),-1);

		mDrawerList.setAdapter(colorAdapter);


		getActionBar().setHomeButtonEnabled(true);

		getActionBar().setDisplayHomeAsUpEnabled(true); 
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			
			//tillfällig lösning, appen borde skrivas om med fragments..
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

							Intent intent=new Intent(CategoryActivity.this,MainActivity.class);	

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
							Intent intent=new Intent(CategoryActivity.this,SearchActivity.class);
							clickedView.setBackgroundColor(Color.parseColor("#F280A1"));
							startActivity(intent); 
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
							Intent intent=new Intent(CategoryActivity.this,MainActivity.class);	
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
							Intent intent=new Intent(CategoryActivity.this,MainActivity.class);	
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
