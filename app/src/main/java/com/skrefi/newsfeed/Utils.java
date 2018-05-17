package com.skrefi.newsfeed;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.skrefi.newsfeed.Constants.BAD_RESPONSE;
import static com.skrefi.newsfeed.Constants.CONNECT_TIME_OUT;
import static com.skrefi.newsfeed.Constants.GOOD_RESPONSE;
import static com.skrefi.newsfeed.Constants.READ_TIME_OUT;
import static com.skrefi.newsfeed.Constants.REQUEST_METHOD;
import static com.skrefi.newsfeed.Constants.RESPONSE;
import static com.skrefi.newsfeed.Constants.RESULTS;
import static com.skrefi.newsfeed.Constants.SERVICE_UNAVAILABLE;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static void printLog(String tag ,Exception e , String msg){
        Log.e(tag, "ctrl_f_mee Error: " + msg + e );
    }

    public static List<Event> fetchEventsData(String urlString) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            printLog(TAG,e,"InterruptedException");
            return null;
        }

        List<Event> events;
        try {
            URL url = createUrl(urlString);
            String jsonResponse;
            jsonResponse = makeHttpRequest(url);
            events = getDataFromJsonResponse(jsonResponse);
        } catch (NullPointerException e) {
            printLog(TAG,e,"NullPointerException (fetchEventsData)");
            return null;
        }
        return events;
    }

    private static List<Event> getDataFromJsonResponse(String jsonResponse) {
        List<Event> events = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject response = jsonObject.getJSONObject(RESPONSE);
            JSONArray results = response.getJSONArray(RESULTS);

            for (int i = 0; i < results.length(); i++) {
                JSONObject object = results.getJSONObject(i);
                String title = object.getString("webTitle");
                String category = object.getString("sectionName");
                String author;
                if(object.getJSONArray("tags").length()>0)
                    if(object.getJSONArray("tags").getJSONObject(0).getString("webTitle").length()>0)
                        author = "N/A";
                    //if(object.getJSONArray("tags").has("webTitle"))    .has() is not valid, as the reviewer told me (you may be the same) so I hope this above IFs work :)

                author = object.getJSONArray("tags").getJSONObject(0).getString("webTitle");
                String date = object.getString("webPublicationDate"); //Format: 2018-05-12T20:02:09Z

                date = date.replace("T"," ");
                date = date.replace("Z"," ");
                String url = object.getString("webUrl");
                events.add(new Event(title, category, author, date, url));
            }
        } catch (JSONException e) {
            printLog(TAG,e,"JSONException (getDataFromJsonResponse)");
            return null;
        }
        return events;
    }

    private static String makeHttpRequest(URL url) {
        String jsonResponse = null;
        try {
            //Making the connection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(REQUEST_METHOD);
            urlConnection.setReadTimeout(READ_TIME_OUT);
            urlConnection.setConnectTimeout(CONNECT_TIME_OUT);
            urlConnection.connect();

            int response = urlConnection.getResponseCode();
            if (response == GOOD_RESPONSE) {
                InputStream inputStream = urlConnection.getInputStream();
                jsonResponse = readInputStream(inputStream);
            } else if (response == BAD_RESPONSE || response == SERVICE_UNAVAILABLE) {
                return jsonResponse; // null
            }

        } catch (IOException e) {
            printLog(TAG,e,"IOException (makeHttpRequest)");
            return null;
        }
        return jsonResponse;
    }

    private static String readInputStream(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder builder = new StringBuilder();

        String line = null;
        try {
            line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            printLog(TAG,e,"IOException (readInputStream)");
            return null;
        }
        return builder.toString();
    }

    private static URL createUrl(String urlString) {
        //Create the URL object
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            printLog(TAG,e,"MalformedURLException (createUrl)");
            return null;
        }
        return url;
    }

}