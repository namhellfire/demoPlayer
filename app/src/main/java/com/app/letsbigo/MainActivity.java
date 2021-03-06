package com.app.letsbigo;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.letsbigo.API.ConstantAPI;
import com.app.letsbigo.API.ListManager;
import com.app.letsbigo.Activitys.SearchResultsActivity;
import com.app.letsbigo.AsyncTask.AsyncListOnline;
import com.app.letsbigo.AsyncTask.AsyncOffline;
import com.app.letsbigo.Custom.DialogSendMail;
import com.app.letsbigo.Fragments.MainFragment;
import com.app.letsbigo.Fragments.Online_fragment;
import com.app.letsbigo.Model.ListAPI;
import com.app.letsbigo.Model.PreferenceShare;
import com.app.letsbigo.Model.Profile;
import com.app.letsbigo.MyService.MyService;
import com.app.letsbigo.Utils.Util;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static com.app.letsbigo.R.menu.main;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        Online_fragment.OnFragmentInteractionListener,
        MainFragment.OnFragmentInteractionListener, View.OnClickListener {

    private final String TAG = "MainActivity";

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private AVLoadingIndicatorView avi;
    private RelativeLayout loading;
    private ArrayList<Profile> dataContent = new ArrayList<>();
    private ArrayList<Profile> listDataAll = new ArrayList<>();
    private AsyncOffline asyncOffline;
    private AsyncListOnline asyncListOnline;
    private RelativeLayout layout_no_network;
    private Button btnRetry;

    private FragmentTransaction fragmentTransaction;


    public static final String TAG_TOP_FRAGMENT = "TopFragment";
    public static final String TAG_NEW_FRAGMENT = "NewFragment";
    public static final String TAG_ONLINE_FRAGMENT = "OnlineFragment";
    public static final String TAG_HOME_FRAGMENT = "HomeFragment";

    public static final String FIRST_RUN = "FIRST_RUN";
    public static String CURRENT_TAG = TAG_HOME_FRAGMENT;
    public static int navItemSelected = 0;
    public static String ApiUrl = ConstantAPI.API_LIST_ALL;
    public static boolean isOnline = false;

    private String[] ActivityTitle;
    private Handler mHandler;
    private Intent mIntent;

    private DialogSendMail dialogSendMail;
    private ShareDialog shareDialog;
    private CallbackManager callbackManager;

    private Boolean doubleBackToExitPressedOnce = false;

    private PreferenceShare share;

    public static ArrayList<ListAPI> listAPIs = new ArrayList<>();


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ------------------ sdk facebook ------------
        FacebookSdk.sdkInitialize(getApplicationContext());


//        try {
//            PackageManager manager = this.getPackageManager();
//            PackageInfo info = null;
//            info = manager.getPackageInfo(
//                    this.getPackageName(), 0);
//            String version = info.versionName;
//            Log.d(TAG,"version app : "+ version);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }

//        onNewIntent(getIntent());


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Drawable dr = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dr = getDrawable(R.drawable.side_nav_bar);
        }
        toolbar.setBackground(dr);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.green));
        } else {

        }

        share = new PreferenceShare(this);

        MobileAds.initialize(getApplicationContext(), getString(R.string.app_id));

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(getString(R.string.device_test))
                .build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }
        });


        mIntent = getIntent();
//        listAPIs = mIntent.getParcelableArrayListExtra(ListManager.LIST_API);

        ActivityTitle = getResources().getStringArray(R.array.nav_item_activity_title);
        mHandler = new Handler();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        layout_no_network = (RelativeLayout) findViewById(R.id.layout_no_network);
        layout_no_network.setVisibility(View.GONE);

        btnRetry = (Button) findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(this);

//        loading = (RelativeLayout) findViewById(R.id.loading);

        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        avi.hide();

        if (asyncOffline == null) {
            asyncOffline = new AsyncOffline(this, avi);
        }

        if (asyncListOnline == null) {
            asyncListOnline = new AsyncListOnline(this, avi);
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        if (share.getPreferenceBooleanValue(FIRST_RUN)) {
            if (ConstantAPI.isService) {
                CURRENT_TAG = TAG_NEW_FRAGMENT;
                navItemSelected = 1;
                ApiUrl = ConstantAPI.API_LIST_NEW;
            } else {
                CURRENT_TAG = TAG_HOME_FRAGMENT;
                navItemSelected = 0;
                ApiUrl = ConstantAPI.API_LIST_ALL;
            }


//            loadDataContent();

            LoadListOnline();

            share.setPreferenceBooleanValue(FIRST_RUN, false);

            Intent intent = new Intent(MainActivity.this, MyService.class);
            startService(intent);

        } else {

        }


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Log.d(TAG, "onNewIntent");
            if (extras.containsKey("NotificationMessage")) {
//                String msg = extras.getString("NotificationMessage");
//                Log.d(TAG, "onNewIntent " + msg);
//
//                CURRENT_TAG = TAG_NEW_FRAGMENT;
//                navItemSelected = 1;
//                ApiUrl = ConstantAPI.API_LIST_NEW;
//
//                AsyncOffline asyncOffline_service = new AsyncOffline(this, avi);
//                asyncOffline_service.execute(MainActivity.ApiUrl);

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ondestroy");
        if (!asyncOffline.isCancelled() && asyncOffline != null) {
            asyncOffline.cancel(false);
        }
        if (!asyncListOnline.isCancelled() && asyncListOnline != null) {
            asyncOffline.cancel(false);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (isOnline) {
            isOnline = false;
//            loadDataContent();
            LoadListOnline();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(main, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
                if (intent != null) {
                    intent.putExtra(SearchManager.QUERY, query);
                    startActivity(intent);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.d(TAG, "search view focus : " + b);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d(TAG, "search view close : ");
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orrentation = newConfig.orientation;
        switch (orrentation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                Log.d(TAG, "orrentation : " + orrentation);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                Log.d(TAG, "orrentation : " + orrentation);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.nav_home:
                Log.d(TAG, "back");
                return true;
            case R.id.action_search:
                return true;
            case R.id.action_reload:
                reLoadPage();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void reLoadPage() {
        if (!isOnline && CURRENT_TAG.equalsIgnoreCase(TAG_ONLINE_FRAGMENT)) {
            LoadListOnline();
        } else {
            loadDataContent();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        if (!Util.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            return true;
        }

        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        int id = item.getItemId();

        if (id == R.id.nav_home) {
            navItemSelected = 0;
            CURRENT_TAG = TAG_HOME_FRAGMENT;

        } else if (id == R.id.nav_new) {
            navItemSelected = 1;
            CURRENT_TAG = TAG_NEW_FRAGMENT;

        } else if (id == R.id.nav_top) {
            navItemSelected = 2;
            CURRENT_TAG = TAG_TOP_FRAGMENT;

        } else if (id == R.id.nav_online) {
            navItemSelected = 3;
            CURRENT_TAG = TAG_ONLINE_FRAGMENT;

        } else if (id == R.id.nav_facebook) {
            Intent intent = Util.getOpenFacebookIntent(getApplicationContext());
            if (intent != null) {
                startActivity(intent);
            }
            return true;
        } else if (id == R.id.nav_youtube) {
            Intent intent = Util.getOpenYoutubeIntent(getApplicationContext());
            if (intent != null) {
                startActivity(intent);
            }
            return true;
        } else if (id == R.id.nav_share) {

            takeScreenshot();

            return true;
        } else if (id == R.id.nav_rate) {
            Intent intent = Util.getOpenRateIntent(getApplicationContext());
            if (intent != null) {
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                }
            }
            return true;

        } else if (id == R.id.nav_contact) {
            dialogSendMail = new DialogSendMail(this);
            if (dialogSendMail != null && !dialogSendMail.isShowing()) {
                dialogSendMail.show();
            }
            return true;
        }

        if (!CURRENT_TAG.equalsIgnoreCase(TAG_ONLINE_FRAGMENT)) {
            isOnline = false;
        }
        ApiUrl = setAPiUrl(navItemSelected);
        Log.d(TAG, "item select navibar " + CURRENT_TAG + " navibar selected " + navItemSelected);
        if (CURRENT_TAG.equalsIgnoreCase(TAG_ONLINE_FRAGMENT)) {
            LoadListOnline();
        } else {
            loadDataContent();
        }

        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void loadPageFragment(final ArrayList<Profile> arrayList) {

        if (!Util.isNetworkAvailable(this)) {
            layout_no_network.setVisibility(View.VISIBLE);
            return;
        }

        layout_no_network.setVisibility(View.GONE);


        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            Log.d(TAG, "fragment not null");
//            return;
        }
        dataContent = arrayList;
        Runnable mPendingRunable = new Runnable() {
            @Override
            public void run() {
                Fragment fragment = getPageFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(ListManager.DATA_CONTENT, dataContent);
                fragment.setArguments(bundle);
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
//                fragmentTransaction.commitAllowingStateLoss();
                fragmentTransaction.commit();
            }
        };

        if (mPendingRunable != null) {
            mHandler.post(mPendingRunable);
        }

        selectNavMenu();
        setTitleToolbar();

    }

    public void loadPageOnlineFragment(final ArrayList<ListAPI> listAPIs) {

        if (!Util.isNetworkAvailable(this)) {
            layout_no_network.setVisibility(View.VISIBLE);
            return;
        }

        layout_no_network.setVisibility(View.GONE);


        Runnable mPendingRunable = new Runnable() {
            @Override
            public void run() {
                Online_fragment onlineFragment = new Online_fragment();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(ListManager.LIST_API, listAPIs);
                onlineFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, onlineFragment, CURRENT_TAG);
                fragmentTransaction.commit();
            }
        };

        if (mPendingRunable != null) {
            mHandler.post(mPendingRunable);
        }

        selectNavMenu();
        setTitleToolbar();

    }


    public Fragment getPageFragment() {
        return new MainFragment();
    }

    public void LoadListOnline() {

        navItemSelected = 3;
        CURRENT_TAG = TAG_ONLINE_FRAGMENT;

        if (!asyncOffline.isCancelled() && asyncOffline != null) {
            asyncOffline.cancel(true);
        }
        if (asyncListOnline.isCancelled()) {
            asyncListOnline = null;
            asyncListOnline = new AsyncListOnline(this, avi);
            asyncListOnline.execute(ConstantAPI.API_LISTAPI);
        } else {
            asyncListOnline.cancel(true);
            asyncListOnline = null;
            asyncListOnline = new AsyncListOnline(this, avi);
            asyncListOnline.execute(ConstantAPI.API_LISTAPI);
        }
    }

    public void loadDataContent() {
        Log.d(TAG, "link API : " + ApiUrl);

        if (CURRENT_TAG.equalsIgnoreCase(TAG_HOME_FRAGMENT)) {
            loadDataHome();
            return;
        }

        if (asyncListOnline != null) {
            if (!asyncListOnline.isCancelled()) {
                asyncOffline.cancel(true);
            }
        }

        if (asyncOffline != null) {
            if (asyncOffline.isCancelled()) {
                asyncOffline = null;
                asyncOffline = new AsyncOffline(this, avi);
                asyncOffline.execute(MainActivity.ApiUrl);
            } else {
                asyncOffline.cancel(true);
                asyncOffline = null;
                asyncOffline = new AsyncOffline(this, avi);
                asyncOffline.execute(MainActivity.ApiUrl);
            }
        } else {
            asyncListOnline = new AsyncListOnline(this, avi);
            asyncOffline.execute(MainActivity.ApiUrl);
        }

    }

    public void loadDataHome() {


        if (listDataAll == null || listDataAll.size() <= 0) {
            SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(this.getApplicationContext());
            Gson gson = new Gson();
            String jsonAll = appSharedPrefs.getString(ListManager.LIST_ALL, "");
            String jsonAPI = appSharedPrefs.getString(ListManager.LIST_API, "");

            Type typeAll = new TypeToken<ArrayList<Profile>>() {
            }.getType();
            listDataAll = gson.fromJson(jsonAll, typeAll);

            Type typeAPI = new TypeToken<ArrayList<ListAPI>>() {
            }.getType();
            listAPIs = gson.fromJson(jsonAPI, typeAPI);

        }

        ArrayList<Profile> newProfile = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            newProfile.add(listDataAll.get(i));
        }
        Collections.shuffle(newProfile);

//            ArrayList<Profile> profiles = new ArrayList<>();
//            profiles = this.getIntent().getParcelableArrayListExtra(ListManager.LIST_ALL);
        loadPageFragment(newProfile);

    }

    public void selectNavMenu() {
        navigationView.getMenu().getItem(navItemSelected).setChecked(true);
    }

    public void setTitleToolbar() {
        getSupportActionBar().setTitle(ActivityTitle[navItemSelected]);
//        getActionBar().setTitle(ActivityTitle[navItemSelected]);
    }

    public String setAPiUrl(int type) {
        switch (type) {
            case 0:
                return ConstantAPI.API_LIST_ALL;
            case 1:
                return ConstantAPI.API_LIST_NEW;
            case 2:
                return ConstantAPI.API_LIST_TOP;
            case 3:
                return ConstantAPI.API_LISTAPI;
            default:
                return ConstantAPI.API_LIST_TOP;
        }
    }


    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
//            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    View v1 = getWindow().getDecorView().getRootView();
                    v1.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                    v1.setDrawingCacheEnabled(false);

                    SharePhoto photo = new SharePhoto.Builder()
                            .setBitmap(bitmap)
                            .build();
                    SharePhotoContent content = new SharePhotoContent.Builder()
                            .addPhoto(photo)
                            .build();
                    if (shareDialog != null) {
//                shareDialog.canShow(content, ShareDialog.Mode.AUTOMATIC);
                        shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
                    }
                }
            }, 300);
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnRetry:
                reLoadPage();
                break;
        }
    }
}
