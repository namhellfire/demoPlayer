package com.app.letsbigo.Activitys;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.OvershootInterpolator;

import com.app.letsbigo.API.ListManager;
import com.app.letsbigo.Adapter.OfflineAdapter;
import com.app.letsbigo.Adapter.SpacesItemDecoration;
import com.app.letsbigo.Model.Profile;
import com.app.letsbigo.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;

public class SearchResultsActivity extends AppCompatActivity {

    private final String TAG = "SearchResultsActivity";

    private RecyclerView recyclerView;
    protected LinearLayoutManager linearLayoutManager;
    private OfflineAdapter offlineAdapter;
    private GridLayoutManager gridLayoutManager;

    private ArrayList<Profile> listProfile;
    private ArrayList<Profile> listSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        final ActionBar actionBar = getSupportActionBar();
        Drawable dr = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            dr = getDrawable(R.drawable.side_nav_bar);
        }
        actionBar.setBackgroundDrawable(dr);
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.lvOffline);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            int mLastFirstVisibleItem = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                final int currentFirstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();
                final int currentLastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();

                if (currentFirstVisibleItem > this.mLastFirstVisibleItem) {
//                    getActivity().getSupportActionBar().hide();
//                    actionBar.hide();
                } else if (currentFirstVisibleItem < this.mLastFirstVisibleItem) {
//                    getActivity().getSupportActionBar().show();
//                    actionBar.show();
                }

                if (currentLastVisibleItem == (offlineAdapter.getItemCount()-1)){
                    recyclerView.setPadding(0,0,0,150);
                    recyclerView.requestLayout();
                }else if (currentLastVisibleItem == (offlineAdapter.getItemCount()-3)){
                    recyclerView.setPadding(0,0,0,0);
                    recyclerView.requestLayout();
                }

                this.mLastFirstVisibleItem = currentFirstVisibleItem;

            }
        });

        listProfile = new ArrayList<>();


        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(ListManager.LIST_ALL, "");

        Type type = new TypeToken<ArrayList<Profile>>() {
        }.getType();
        listProfile = gson.fromJson(json, type);

        MobileAds.initialize(getApplicationContext(), getString(R.string.app_id));

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(getString(R.string.device_test))
                .build();
        mAdView.loadAd(adRequest);

        handleIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (intent == null || intent.getStringExtra(SearchManager.QUERY) == null || intent.getStringExtra(SearchManager.QUERY).trim().isEmpty()){
            return;
        }

        String query = intent.getStringExtra(SearchManager.QUERY);
        Log.d(TAG, "query search = " + query);
        listSearch = new ArrayList<>();

        for (int i = 0; i < listProfile.size(); i++) {
            Profile profile = listProfile.get(i);
            String name = profile.getName();
            String desc = profile.getStatus();
            String view = profile.getView();
            String thumbnail = profile.getThumbnail();
            String url = profile.getUrl();
            if (name.contains(query) || desc.contains(query)) {
                listSearch.add(profile);
            }
        }

        if (listSearch != null && listSearch.size() > 0) {
            offlineAdapter = new OfflineAdapter(listSearch, this);
            gridLayoutManager = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setItemAnimator(new SlideInDownAnimator(new OvershootInterpolator(1f)));
            AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(offlineAdapter);
            recyclerView.addItemDecoration(new SpacesItemDecoration(10));
            recyclerView.setAdapter(alphaInAnimationAdapter);
        } else {

        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d("back", "backsss");
                onBackPressed();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
