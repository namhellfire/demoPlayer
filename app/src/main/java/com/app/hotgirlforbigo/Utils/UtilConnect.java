package com.app.hotgirlforbigo.Utils;

import android.util.Log;

import com.app.hotgirlforbigo.API.ConstantAPI;
import com.app.hotgirlforbigo.API.DataResponse;
import com.app.hotgirlforbigo.Model.CountryApi;
import com.app.hotgirlforbigo.Model.ListAPI;
import com.app.hotgirlforbigo.Model.Profile;
import com.app.hotgirlforbigo.Model.ProfileOffline;
import com.app.hotgirlforbigo.Model.ProfileOnline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by nguyennam on 2/11/17.
 */

public class UtilConnect {

    private static final String TAG = "UtilConnect";

    public static DataResponse connectAPI(String link, HashMap<String, String> data) {
        try {
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setConnectTimeout(ConstantAPI.TIME_OUT);
            conn.setDoOutput(true);


            //get data from hashmap
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : data.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            Log.d("SERVER", "get DATA " + result.toString());

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(result.toString());

            writer.flush();
            writer.close();
            os.close();
            Log.d("SERVER", "post data ");


            InputStream inputStream = conn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append((line));
            }
            bufferedReader.close();
            inputStream.close();
            conn.disconnect();
            Log.d("SERVER", "get sbbbb " + sb.toString());

            DataResponse dataRes = new DataResponse();

            JSONObject jsonObject = new JSONObject(sb.toString());
            try {
                String status = jsonObject.getString("status");
                dataRes.setStatusCode(Integer.parseInt(status));

            } catch (JSONException e) {
                dataRes.setStatusCode(-1); //-1 with error from server
            }

            try {
                String message = jsonObject.getString("message");
                dataRes.setMessage(message);
            } catch (JSONException e) {
                dataRes.setMessage("You have some problem. Please check again");
            }


            try {
                String response = jsonObject.getString("response");
                dataRes.setResponse(response);
            } catch (JSONException e) {
                dataRes.setResponse("");
            }

            //check method
//            if (link.equalsIgnoreCase(ConstantAPI.API_LOGOUT)){
//                dataRes.setMethod(ConstantAPI.METHOD_LOGOUT);
//            }else {
//                dataRes.setMethod("");
//            }
            return dataRes;

        } catch (Exception e) {
            Log.d("CALL", "get error code " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static String getAPI(String link) throws IOException {

        URL url = new URL(link);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(ConstantAPI.TIME_OUT);

        StringBuilder builder = new StringBuilder();

        InputStream content = new BufferedInputStream(urlConnection.getInputStream());
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(content));
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        Log.e("TAG", "result : " + builder.toString());
        urlConnection.disconnect();
        //saveData(context, builder.toString());
        return builder.toString();
    }

    public static String getAPI_v1(String link) throws IOException {

        URL url = new URL(link);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(ConstantAPI.TIME_OUT);

        StringBuilder builder = new StringBuilder();

        InputStream content = new BufferedInputStream(urlConnection.getInputStream());
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(content));
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        Log.e("TAG", "result : " + builder.toString());
        urlConnection.disconnect();

        try {
            JSONArray jsonArray = new JSONArray(builder.toString());
            if (jsonArray.length() > 0) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                if (jsonObject.getString("live_url") != null) {
                    ParseJsonOnline(jsonArray);
                } else {
                    ParseJsonOffline(jsonArray);
                }
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    public static ArrayList<ProfileOnline> ParseJsonOnline(JSONArray array) {

        ArrayList<ProfileOnline> onlineArrayList = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject object = array.getJSONObject(i);

                ProfileOnline profileOnline = new ProfileOnline();
                profileOnline.setBig_img(object.getString(ProfileOnline.BIG_IMG));
                profileOnline.setMedium_img(object.getString(ProfileOnline.MEDIUM_IMG));
                profileOnline.setSmall_img(object.getString(ProfileOnline.SMALL_IMG));
                profileOnline.setUser_count(object.getInt(ProfileOnline.USER_COUNT));
                profileOnline.setCountry(object.getString(ProfileOnline.COUNTRY));
                profileOnline.setNick_name(object.getString(ProfileOnline.NICK_NAME));
                profileOnline.setRoom_topic(object.getString(ProfileOnline.ROOM_TOPIC));
                profileOnline.setStatus(object.getString(ProfileOnline.STATUS));
                profileOnline.setLive_url(object.getString(ProfileOnline.LIVE_URL));

                onlineArrayList.add(profileOnline);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (onlineArrayList.size() > 0 && onlineArrayList != null) {
            return onlineArrayList;
        }

        return null;
    }


    public static ArrayList<ProfileOffline> ParseJsonOffline(JSONArray array) {

        ArrayList<ProfileOffline> offlineArrayList = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject object = array.getJSONObject(i);

                ProfileOffline profileOffline = new ProfileOffline();
                profileOffline.setId(object.getInt(ProfileOffline.ID));
                profileOffline.setName(object.getString(ProfileOffline.NAME));
                profileOffline.setAuthor(object.getString(ProfileOffline.AUTHOR));
                profileOffline.setUrl(object.getString(ProfileOffline.URL));
                profileOffline.setDescription(object.getString(ProfileOffline.DESCRIPTION));
                profileOffline.setThumbnail(object.getString(ProfileOffline.THUMBNAIL));
                profileOffline.setView(object.getString(ProfileOffline.VIEW));
                profileOffline.setLike(object.getInt(ProfileOffline.LIKE));
                profileOffline.setDislike(object.getInt(ProfileOffline.DISLIKE));
                profileOffline.setComment(object.getInt(ProfileOffline.COMMENT));
                profileOffline.setPublish(object.getInt(ProfileOffline.PUBLISH));
                profileOffline.setTime(object.getString(ProfileOffline.TIME));
                profileOffline.setLength(object.getString(ProfileOffline.LENGTH));
                profileOffline.setType(object.getInt(ProfileOffline.TYPE));
                profileOffline.setTag(object.getString(ProfileOffline.TAG));

                offlineArrayList.add(profileOffline);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (offlineArrayList != null && offlineArrayList.size() > 0) {
            return offlineArrayList;
        }
        return null;
    }

    public static ArrayList<Profile> ParseJsonProfile(JSONArray array) {
        ArrayList<Profile> profileArrayList = new ArrayList<>();
        try {
            int length = array.getJSONObject(0).length();
            if (length >= 15) {
                profileArrayList = parseProfileOffline(array);
            } else {
                profileArrayList = parseProfileOnline(array);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (profileArrayList.size() > 0 && profileArrayList != null) {
            return profileArrayList;
        }
        return null;
    }

    public static ArrayList<Profile> parseProfileOffline(JSONArray arr) {
        ArrayList<Profile> profileArrayList = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject object = arr.getJSONObject(i);
                Profile profile = new Profile();
                profile.setName(object.getString(ProfileOffline.NAME));
                profile.setStatus(object.getString(ProfileOffline.DESCRIPTION));
                profile.setThumbnail(object.getString(ProfileOffline.THUMBNAIL));
                profile.setUrl(object.getString(ProfileOffline.URL));
                profile.setView(object.getString(ProfileOffline.VIEW));

                profileArrayList.add(profile);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (profileArrayList.size() > 0 && profileArrayList != null) {
            return profileArrayList;
        }
        return null;
    }

    public static ArrayList<Profile> parseProfileOnline(JSONArray arr) {
        ArrayList<Profile> profileArrayList = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject object = arr.getJSONObject(i);
                Profile profile = new Profile();
                profile.setName(object.getString(ProfileOnline.NICK_NAME));
                profile.setStatus(object.getString(ProfileOnline.STATUS));
                profile.setThumbnail(object.getString(ProfileOnline.BIG_IMG));
                profile.setUrl(object.getString(ProfileOnline.LIVE_URL));
                profile.setView(object.getString(ProfileOnline.USER_COUNT));

                profileArrayList.add(profile);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (profileArrayList.size() > 0 && profileArrayList != null) {
            return profileArrayList;
        }
        return null;
    }


    public static ArrayList<ListAPI> ParseListAPI(String value) {
        ArrayList<ListAPI> listAPIs = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(value);
            Iterator nsads = jsonObject.keys();
            while (nsads.hasNext()) {
                String url = (String) nsads.next();
                ListAPI listAPI = new ListAPI();
                listAPI.setUri(url);
                listAPI.setMd5(jsonObject.getString(url));
                listAPIs.add(listAPI);
            }
            return listAPIs;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<CountryApi> getCountry(ArrayList<ListAPI> listAPIs) {
        ArrayList<CountryApi> ListCountry = new ArrayList<>();
        if (listAPIs.size() > 0 && listAPIs != null) {
            for (ListAPI api : listAPIs) {
                String country = api.getUri().replace(ConstantAPI.SERVER, "").replace(".json", "");
                if (country.equalsIgnoreCase("new") || country.equalsIgnoreCase("top") || country.equalsIgnoreCase("all")) {

                } else {
                    String CountryName = country.substring(0, 1).toUpperCase() + country.substring(1);
                    CountryApi countryApi = new CountryApi();
                    countryApi.setCountry(CountryName);
                    countryApi.setAPIUrl(api.getUri());
                    ListCountry.add(countryApi);
                }
            }
            return ListCountry;
        }
        return null;

    }
}
