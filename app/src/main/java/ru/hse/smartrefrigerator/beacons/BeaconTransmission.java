package ru.hse.smartrefrigerator.beacons;


import android.util.Log;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.*;
import org.json.JSONObject;
import ru.hse.smartrefrigerator.utils.PrivateData;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by KingUrgot on 09.07.2015.
 */
public class BeaconTransmission {

    public static final String APP_ROOT = "mobile.ng.bluemix.net";
    public static final String HTTPS = "https";
    public static final String ROUTE = "data/rest/v1/apps/";
    public static final String APP_KEY = PrivateData.getBluemixAppKey();
    public static final String APP_SECRET = PrivateData.getBluemixAppSecret();
    static List<BeaconMark> beacons;

    public static List<BeaconMark> getBeacons() {
        return beacons;
    }

    public static void setBeacons(List<BeaconMark> beacons) {
        BeaconTransmission.beacons = beacons;
    }

    public static void addBeacon(BeaconMark beacon, RequestQueue queue) {
        Gson gson = new Gson();
        List<BeaconMark> arrList = new ArrayList<BeaconMark>();
        arrList.add(beacon);
        String jsoned = gson.toJson(arrList);

        URI bb = UriBuilder.fromPath(HTTPS + "://" + APP_ROOT).path(ROUTE).path(APP_KEY)
                .path("injections").queryParam("classname", "Beacon").build();

        Log.i("jsoned", jsoned);

        JsonObjectRequest jaRequest = new JsonObjectRequest(Request.Method.POST, bb.toString(), jsoned,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("BeaconAdd", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("BeaconError", error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("IBM-Application-Secret", APP_SECRET);
                return map;
            }
        };
        queue.add(jaRequest);
    }

    public static void loadBeacons(RequestQueue queue, final OnBeaconsLoadCallback cb) {
        URI bb = UriBuilder.fromPath(HTTPS + "://" + APP_ROOT).path(ROUTE).path(APP_KEY)
                .path("objects").queryParam("classname", "Beacon").build();
        //final List<String> response = new ArrayList<String>();

        Log.i("tag", bb.toString());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, bb.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String resp) {
                        Log.i("resp", resp);
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jo = (JsonObject) jsonParser.parse(resp);
                        JsonArray jarr = jo.getAsJsonArray("object");

                        List<BeaconMark> beacons = new ArrayList<>();

                        for (JsonElement jEl : jarr) {
                            JsonObject jBeacon = jEl.getAsJsonObject().getAsJsonObject("attributes");
                            Integer uId = jBeacon.getAsJsonPrimitive("uID").getAsInt();

                            List<String> products = new ArrayList<>();
                            JsonArray jProducts = jBeacon.getAsJsonArray("productList");
                            for (JsonElement jStr : jProducts) {
                                products.add(jStr.getAsString());
                            }

                            BeaconMark bm = new BeaconMark(products, uId);
                            beacons.add(bm);
                        }

                        cb.onLoad(beacons);
                        Log.i("beacons", beacons.toString());
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("tag", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("IBM-Application-Secret", APP_SECRET);
                return map;
            }
        };

        queue.add(stringRequest);
    }
}
