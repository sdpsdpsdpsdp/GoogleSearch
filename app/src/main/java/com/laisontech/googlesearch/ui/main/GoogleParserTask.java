package com.laisontech.googlesearch.ui.main;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.laisontech.googlesearch.interfaces.OnLoadDataFromTaskListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SDP on 2018/4/24.
 */

public class GoogleParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    private OnLoadDataFromTaskListener listener;

    public GoogleParserTask(OnLoadDataFromTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
        try {
            JSONObject jObject = new JSONObject(jsonData[0]);
            DirectionsJSONParser parser = new DirectionsJSONParser();
            List<List<HashMap<String, String>>> routes;
            routes = parser.parse(jObject);
            return routes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        if (result == null || result.size() < 1) {
            listener.onLoadDataFromTask(null);
            return;
        }
        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();
            List<HashMap<String, String>> path = result.get(i);
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                points.add(position);
            }
            lineOptions.addAll(points);
            lineOptions.width(3);
            lineOptions.color(Color.BLUE);
        }
        if (lineOptions != null) {
            lineOptions.clickable(true);
        }
        listener.onLoadDataFromTask(lineOptions);
    }
}
