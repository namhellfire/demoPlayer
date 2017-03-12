package com.app.letsbigo.Activitys;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.letsbigo.API.ConstantAPI;
import com.app.letsbigo.API.ListManager;
import com.app.letsbigo.MainActivity;
import com.app.letsbigo.Model.ListAPI;
import com.app.letsbigo.Model.PreferenceShare;
import com.app.letsbigo.Model.Profile;
import com.app.letsbigo.Model.ProfileOffline;
import com.app.letsbigo.R;
import com.app.letsbigo.Utils.Util;
import com.app.letsbigo.Utils.UtilConnect;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Splash_screen extends AppCompatActivity {

    private final int SPLEEP = 2000;
    private ImageView imgSplashscreen;
    private ArrayList<ProfileOffline> profileOfflines;
    SharedPreferences appSharedPrefs;
    ArrayList<ListAPI> listAPIs;
    ArrayList<Profile> profiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Removes title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Removes notification bar

        setContentView(R.layout.activity_splash_screen);

        PreferenceShare share = new PreferenceShare(this);
        share.setPreferenceBooleanValue(MainActivity.FIRST_RUN, true);

        profileOfflines = new ArrayList<>();

        if (Util.isNetworkAvailable(this)) {
            ListAPIAsync apiAsync = new ListAPIAsync(this);
            apiAsync.execute(ConstantAPI.API_LISTAPI);

            ListAllAsync allAsync = new ListAllAsync(this);
            allAsync.execute(ConstantAPI.API_LIST_ALL);
        } else {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            Splash_screen.this.startActivity(intent);
            Splash_screen.this.finish();
        }

    }


    class ListAPIAsync extends AsyncTask<String, String, String> {

        Activity activity;

        public ListAPIAsync(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                return UtilConnect.getAPI(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            listAPIs = new ArrayList<>();
            if (s != null) {
                listAPIs = UtilConnect.ParseListAPI(s);
            }

        }
    }


    class ListAllAsync extends AsyncTask<String, Object, String> {

        Activity activity;

        public ListAllAsync(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
//                profiles = new ArrayList<>();
                String s = UtilConnect.getAPI(strings[0]);
                profiles = UtilConnect.ParseJsonProfile(new JSONArray(s));
                return s;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                ArrayList<ProfileOffline> profileOfflines = new ArrayList<>();
                JSONArray array = null;
                try {
                    array = new JSONArray(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                profileOfflines = UtilConnect.ParseJsonOffline(array);
                if (appSharedPrefs == null) {
                    appSharedPrefs = PreferenceManager
                            .getDefaultSharedPreferences(activity);
                }
                SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                Gson gson = new Gson();
                String json = gson.toJson(profileOfflines);
                prefsEditor.putString(ListManager.LIST_ALL, json);
                prefsEditor.commit();
                ArrayList<Profile> newProfile = new ArrayList<>();
                for (int i = 0; i < 50; i++) {
                    newProfile.add(profiles.get(i));
                }
                Collections.shuffle(newProfile);
                Intent intent = new Intent(activity, MainActivity.class);
                intent.putParcelableArrayListExtra(ListManager.LIST_API, listAPIs);
                intent.putParcelableArrayListExtra(ListManager.LIST_ALL, newProfile);
                Splash_screen.this.startActivity(intent);
                Splash_screen.this.finish();

            }

        }
    }

}

