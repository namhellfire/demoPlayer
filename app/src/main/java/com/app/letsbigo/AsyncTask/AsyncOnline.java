package com.app.letsbigo.AsyncTask;

import android.os.AsyncTask;

import com.app.letsbigo.Model.ProfileOnline;

import java.util.ArrayList;

/**
 * Created by nguyennam on 2/18/17.
 */

public class AsyncOnline extends AsyncTask<String, String, ArrayList<ProfileOnline>> {

    public AsyncOnline(){

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<ProfileOnline> doInBackground(String... strings) {
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<ProfileOnline> profileOnlines) {
        super.onPostExecute(profileOnlines);
    }
}
