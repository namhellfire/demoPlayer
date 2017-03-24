package com.app.letsbigo.MyService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.app.letsbigo.API.ConstantAPI;
import com.app.letsbigo.Activitys.Splash_screen;
import com.app.letsbigo.Model.ListAPI;
import com.app.letsbigo.Model.PreferenceShare;
import com.app.letsbigo.R;
import com.app.letsbigo.Utils.UtilConnect;

import java.io.IOException;
import java.util.ArrayList;

public class MyService extends Service {
    private final String TAG = "MyService";
    private final int TIME_LOOP = 1000 * 60 * 60;
    private Handler handler;
    private NotificationManager notificationManager;
    private Notification myNotification;
    private ListAPIAsync listAPIAsync;
    private ArrayList<ListAPI> listAPIs;
    PreferenceShare preferenceShare;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d(TAG, "onBind");
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "on create");
        preferenceShare = new PreferenceShare(getApplicationContext());
        handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, TIME_LOOP);
                if (listAPIAsync == null) {
                    listAPIAsync = new ListAPIAsync(getApplicationContext());
                }
                if (listAPIAsync.isCancelled()) {
                    listAPIAsync = null;
                    listAPIAsync = new ListAPIAsync(getApplicationContext());
                    listAPIAsync.execute(ConstantAPI.API_LISTAPI);
                } else {
                    listAPIAsync.cancel(true);
                    listAPIAsync = null;
                    listAPIAsync = new ListAPIAsync(getApplicationContext());
                    listAPIAsync.execute(ConstantAPI.API_LISTAPI);
                }
            }
        };

        handler.postDelayed(runnable, TIME_LOOP);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "on onStartCommand");

        return super.onStartCommand(intent, flags, startId);

    }

    public void showNotify() {
        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        Intent notificationIntent = new Intent(getApplicationContext(), Splash_screen.class);
        notificationIntent.putExtra("NotificationMessage", "newvideo");
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        notification.setLatestEventInfo(getApplicationContext(), notificationTitle, notificationMessage, pendingNotificationIntent);

        //set
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.drawable.com_facebook_button_icon);
        builder.setContentText(getResources().getString(R.string.have_new_video));
        builder.setContentTitle(getResources().getString(R.string.app_name));
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);

        Notification notification = builder.build();
        nm.notify((int) System.currentTimeMillis(), notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "on destroy");
    }

    public void checkAPI() {
        boolean haveChange = false;
        for (int i = 0; i < listAPIs.size(); i++) {
            String url = listAPIs.get(i).getUri();
            String md5 = listAPIs.get(i).getMd5();
            if (!md5.equalsIgnoreCase(preferenceShare.getPreferenceStringValue(listAPIs.get(i).getUri()))) {
                preferenceShare.setPreferenceStringValue(url, md5);
                switch (url) {
                    case ConstantAPI.API_LIST_ALL:
                        haveChange = true;
                        break;
                    case ConstantAPI.API_LIST_NEW:
                        haveChange = true;
                        break;
                    case ConstantAPI.API_LIST_TOP:
                        haveChange = true;
                        break;
                    default:
                        break;
                }
            }
        }
        if (haveChange) {
            showNotify();
        }


    }


    class ListAPIAsync extends AsyncTask<String, String, String> {

        Context context;

        public ListAPIAsync(Context context) {
            this.context = context;
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
                checkAPI();
            }


        }
    }


}
