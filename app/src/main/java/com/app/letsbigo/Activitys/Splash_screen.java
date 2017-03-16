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

public class Splash_screen extends AppCompatActivity {

    private final int SPLEEP = 2000;
    private ImageView imgSplashscreen;
    private ArrayList<ProfileOffline> profileOfflines;
    private ArrayList<Profile> listProfiles;
    SharedPreferences appSharedPrefs;
    PreferenceShare preferenceShare;
    ArrayList<ListAPI> listAPIs;
    boolean haveChange = false;
    ListAllAsync allAsync;
    ListAPIAsync apiAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onNewIntent(getIntent());

        getSupportActionBar().hide();

        allAsync = new ListAllAsync(this);
        apiAsync = new ListAPIAsync(this);

//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Removes title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Removes notification bar

        setContentView(R.layout.activity_splash_screen);

        preferenceShare = new PreferenceShare(this);
        preferenceShare.setPreferenceBooleanValue(MainActivity.FIRST_RUN, true);

        profileOfflines = new ArrayList<>();

        if (Util.isNetworkAvailable(this)) {
            apiAsync.execute(ConstantAPI.API_LISTAPI);
        } else {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            Splash_screen.this.startActivity(intent);
            Splash_screen.this.finish();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("NotificationMessage")) {
                String msg = extras.getString("NotificationMessage");

//                CURRENT_TAG = TAG_NEW_FRAGMENT;
//                navItemSelected = 1;
//                ApiUrl = ConstantAPI.API_LIST_NEW;

//                Intent intent1 = new Intent(this, MainActivity.class);
//                intent.putParcelableArrayListExtra(ListManager.LIST_API, listAPIs);
//                Splash_screen.this.startActivity(intent1);
//                Splash_screen.this.finish();

                ConstantAPI.isService = true;

            }
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
                if (preferenceShare.getPreferenceBooleanValue(ListManager.HAVE_DATEA, false)) {
                    for (int i = 0; i < listAPIs.size(); i++) {
                        String url = listAPIs.get(i).getUri();
                        String md5 = listAPIs.get(i).getMd5();
                        if (!md5.equalsIgnoreCase(preferenceShare.getPreferenceStringValue(listAPIs.get(i).getUri()))) {
                            preferenceShare.setPreferenceStringValue(url, md5);
                            switch (url) {
                                case ConstantAPI.API_LIST_ALL:
                                    haveChange = true;
                                    if (allAsync != null) {
                                        allAsync.execute(ConstantAPI.API_LIST_ALL);
                                    }
                                    break;
                                case ConstantAPI.API_LIST_NEW:

                                    break;
                                case ConstantAPI.API_LIST_TOP:
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    if (haveChange) {

                    } else {
//                        SharedPreferences appSharedPrefs = PreferenceManager
//                                .getDefaultSharedPreferences(activity);
//                        Gson gson = new Gson();
//                        String json = appSharedPrefs.getString(ListManager.LIST_ALL, "");
//
//                        Type type = new TypeToken<ArrayList<Profile>>() {
//                        }.getType();
//                        profileOfflines = gson.fromJson(json, type);
//
//                        ArrayList<Profile> newProfile = new ArrayList<>();
//                        for (int i = 0; i < 50; i++) {
//                            newProfile.add(listProfiles.get(i));
//                        }
//                        Collections.shuffle(newProfile);
                        Intent intent = new Intent(activity, MainActivity.class);
//                        intent.putParcelableArrayListExtra(ListManager.LIST_API, listAPIs);
//                        intent.putParcelableArrayListExtra(ListManager.LIST_ALL, newProfile);
                        Splash_screen.this.startActivity(intent);
                        Splash_screen.this.finish();

                    }
                } else {
                    if (allAsync != null) {
                        allAsync.execute(ConstantAPI.API_LIST_ALL);
                    }
                }


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
                return s;
            } catch (IOException e) {
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
                JSONArray array = null;
                try {
                    array = new JSONArray(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                preferenceShare.setPreferenceBooleanValue(ListManager.HAVE_DATEA, true);

                listProfiles = UtilConnect.ParseJsonProfile(array);
                if (appSharedPrefs == null) {
                    appSharedPrefs = PreferenceManager
                            .getDefaultSharedPreferences(activity);
                }
                SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                Gson gson = new Gson();
                String jsonAll = gson.toJson(listProfiles);
                String jsonAPI = gson.toJson(listAPIs);
                prefsEditor.putString(ListManager.LIST_ALL, jsonAll);
                prefsEditor.putString(ListManager.LIST_API, jsonAPI);
                prefsEditor.commit();

                Intent intent = new Intent(activity, MainActivity.class);
//                intent.putParcelableArrayListExtra(ListManager.LIST_API, listAPIs);
                Splash_screen.this.startActivity(intent);
                Splash_screen.this.finish();

            }

        }
    }

}

