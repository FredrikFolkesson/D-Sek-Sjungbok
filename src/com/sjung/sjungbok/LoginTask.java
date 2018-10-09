package com.sjung.sjungbok;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.MenuItem;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;


public class LoginTask extends AsyncTask<Void, String, Boolean> {
    public AsyncTaskCompleteListener listener =null;
    ProgressDialog progressDialog;
    Context context;
    int songListSizeBefore = 0;
    String username;
    String password;
    MenuItem item;

    public LoginTask(Context context,String username, String password,MenuItem item, AsyncTaskCompleteListener listener) {
        this.context = context;
        this.username=username;
        this.password=password;
        this.item=item;
        this.listener=listener;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context, "Försöker logga in", "", true);
    }




    @Override
    protected Boolean doInBackground(Void... params) {
        System.out.println("ska köra i bakgreunden");
        Connection.Response res = null;
        try {
            res = Jsoup.connect("http://www.dsek.se/navigation/login.php")
                    .data("login", username, "password", password)
                    .method(Connection.Method.POST)
                    .execute();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        System.out.println("nu är vi här");
        String result = res.body();
        if(result.contains("Fel lösenord.")){
            System.out.println("det gick fel :(");
            return false;
        }
        else{
            System.out.println("WHEHEJ");
            return true;
        }

    }

    protected void onPostExecute(Boolean loggedIn) {
        progressDialog.dismiss();
        if(loggedIn) {
            Toast.makeText(context, "Inloggad", Toast.LENGTH_LONG).show();
            item.setTitle("Logga ut");
            SharedPreferences settings = context.getSharedPreferences("SETTINGS", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("LoggedIn", true);
            editor.commit();
            resetHistory();
            listener.onTaskComplete(true);

        }
        else{
            Toast.makeText(context, "Inloggningen misslyckades", Toast.LENGTH_LONG).show();
        }
    }
    private void resetHistory(){
        BufferedWriter bufferedWriter;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(context.getFilesDir() + File.separator + "History.txt"), "UTF-8"));
            bufferedWriter.write("");
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
