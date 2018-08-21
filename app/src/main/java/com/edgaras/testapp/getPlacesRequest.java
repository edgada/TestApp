package com.edgaras.testapp;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class getPlacesRequest extends AsyncTask<String, Void, Boolean> {
    @Override
    protected Boolean doInBackground(String... params) {
        Boolean result = false;
        int limit = 20;
        int offset = 0;

        try {
            int placesCount = howMany(params[0]);
            for(int i=0;i<placesCount; i+=limit)
            {
                JSONObject temp = getRequest(params[0], String.valueOf(offset), String.valueOf(limit));
                if(temp != null)
                {
                    JSONArray results = temp.getJSONArray("places");
                    for (int c=0; c<results.length();c++)
                    {
                        JSONObject place = results.getJSONObject(c);
                        if(place.has("life-span"))
                        {
                            JSONObject place2 = place.getJSONObject("life-span");
                            if(place2.has("begin"))
                            {
                                if(place.has("coordinates"))
                                {
                                    String begin = place2.getString("begin");
                                    char[] tempBegin = begin.toCharArray();
                                    begin = String.valueOf(tempBegin[0]) + String.valueOf(tempBegin[1]) + String.valueOf(tempBegin[2]) + String.valueOf(tempBegin[3]);
                                    int beginDate = Integer.parseInt(begin);
                                    int lifespan = beginDate - 1990;
                                    if(lifespan > 0)
                                    {
                                        JSONObject coordinates = place.getJSONObject("coordinates");
                                        String latitude = coordinates.getString("latitude");
                                        String longitude = coordinates.getString("longitude");
                                        String name = place.getString("name");

                                        FoundedPlace fp = new FoundedPlace(Double.parseDouble(latitude), Double.parseDouble(longitude), name, lifespan);
                                        MainActivity.allPlaces.add(fp);
                                    }
                                }
                            }
                        }
                    }
                }
                offset += limit;
            }
            result = true;
        }
        catch (Exception e) {
            result = false;
        }
        return result;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
    }

    private int howMany(String word){
        try
        {
            URL url = new URL("https://musicbrainz.org/ws/2/place/?query=" + word + "&limit=1&fmt=json");
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept","application/json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            JSONObject js = new JSONObject(stringBuilder.toString());
            int placesCount = js.getInt("count");

            return placesCount;
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    private JSONObject getRequest(String word, String offset, String limit){
        try
        {
            URL url = new URL("https://musicbrainz.org/ws/2/place/?query=" + word + "&offset=" + offset + "&limit=" + limit + "&fmt=json");
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept","application/json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            JSONObject js = new JSONObject(stringBuilder.toString());
            return js;
        }
        catch (Exception e)
        {
            return null;
        }
    }

}
