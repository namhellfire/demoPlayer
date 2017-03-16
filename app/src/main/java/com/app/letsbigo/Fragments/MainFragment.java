package com.app.letsbigo.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.app.letsbigo.API.ListManager;
import com.app.letsbigo.Adapter.OfflineAdapter;
import com.app.letsbigo.Adapter.SpacesItemDecoration;
import com.app.letsbigo.MainActivity;
import com.app.letsbigo.Model.Profile;
import com.app.letsbigo.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final String TAG = "MainFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView mListview;
    public static String ApiUrl;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GridLayoutManager gridLayoutManager;
    private OfflineAdapter offlineAdapter;
    private ArrayList<Profile> arrayList;
    private ArrayList<Profile> profileOffAll;

    private ActionBar actionBar;
    private boolean isLoading;

    private android.os.Handler handler;
    private int page = 1;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;


    public MainFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String api, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"oncreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        arrayList = new ArrayList<>();
        arrayList = getArguments().getParcelableArrayList(ListManager.DATA_CONTENT);
        isLoading = false;
        handler = new android.os.Handler();
        if (MainActivity.CURRENT_TAG.equalsIgnoreCase(MainActivity.TAG_HOME_FRAGMENT)){
            SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            Gson gson = new Gson();
            String json = appSharedPrefs.getString(ListManager.LIST_ALL, "");

            Type type = new TypeToken<ArrayList<Profile>>() {
            }.getType();
            profileOffAll = gson.fromJson(json, type);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG,"page : "+page);
        View v = inflater.inflate(R.layout.fragment_top, container, false);
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Landscape
            gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        } else {
            // Portrait
            gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        }
        mListview = (RecyclerView) v.findViewById(R.id.lvOffline);
        mListview.addItemDecoration(new SpacesItemDecoration(10));
        mListview.setLayoutManager(gridLayoutManager);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });


        offlineAdapter = new OfflineAdapter(arrayList, getActivity());
        mListview.setItemAnimator(new SlideInDownAnimator(new OvershootInterpolator(1f)));
        AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(offlineAdapter);
        mListview.setAdapter(alphaInAnimationAdapter);

        mListview.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
//                Log.d(TAG, "onscroll currentFirstVisibleItem : " + currentFirstVisibleItem + " currentLastVisibleItem : " + currentLastVisibleItem);

                if (currentLastVisibleItem == (offlineAdapter.getItemCount() - 1)) {

                    if (!isLoading && MainActivity.CURRENT_TAG.equalsIgnoreCase(MainActivity.TAG_HOME_FRAGMENT)) {
                        onLoadMore();
                    }

                    mListview.setPadding(0, 0, 0, 150);
                    mListview.requestLayout();
                } else if (currentLastVisibleItem == (offlineAdapter.getItemCount() - 3)) {
                    mListview.setPadding(0, 0, 0, 0);
                    mListview.requestLayout();
                }

                this.mLastFirstVisibleItem = currentFirstVisibleItem;
            }
        });

        return v;
    }

    public void refreshList() {
        MainActivity ac = (MainActivity) getActivity();
        ac.reLoadPage();
    }

    public void onLoadMore() {
        Log.d(TAG, "On Load More");
        isLoading = true;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int size = 0;
                ArrayList<Profile> arrayList = new ArrayList<Profile>();
                if ((page + 1) * 50 > profileOffAll.size()) {
                    size = profileOffAll.size();
                } else {
                    size = (page + 1) * 50;
                }

                for (int i = page * 50; i < size; i++) {
                    arrayList.add(profileOffAll.get(i));
                }
                offlineAdapter.addItems(arrayList);
//                offlineAdapter.notifyDataSetChanged();

                page++;
                isLoading = false;
            }
        }, 100);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
