package ru.hse.smartrefrigerator.net;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;
import ru.hse.smartrefrigerator.models.Product;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

/**
 * Created by KingUrgot on 09.07.2015.
 */
public class ProductListTransmission {

    public static final String APP_ROOT = "mobile.ng.bluemix.net";
    public static final String HTTPS = "https";
    public static final String ROUTE = "data/rest/v1/apps/";
    public static final String APP_KEY = "7595f02c-4ea0-49dd-b98d-61c6018b8850";
    public static final String APP_SECRET = "70ec21ea62a41068e8dd289e84eb092e273a3c1e";


    static String listID;
    static String lastVersion;




    public static void getByID(RequestQueue queue, String listID, final OnProductsGetCallback cb) {
        URI bb = UriBuilder.fromPath(HTTPS + "://" + APP_ROOT).path(ROUTE).path(APP_KEY).path("objects").path(listID).build();


        Log.i("tag", bb.toString());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, bb.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String resp) {
                        Log.i("resp", resp);
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jo = (JsonObject)jsonParser.parse(resp);
                        jo = (JsonObject)jo.getAsJsonArray("object").get(0);
                        String objID = jo.getAsJsonPrimitive("objectId").getAsString();
                        String version = jo.getAsJsonPrimitive("version").getAsString();
                        JsonArray prodList = jo.getAsJsonArray("attributes");

                        ArrayList<Product> resultList = new ArrayList<Product>();
                        Gson gson = new Gson();
                        for (JsonElement product : prodList) {
                            Product p = gson.fromJson(product, Product.class);
                            resultList.add(p);
                        }
                        cb.onGet(resultList);
                        Log.i("List", resultList.toString());
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("tag", "kek");
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

    public static void addList(List<Product> products, RequestQueue queue, final OnProductModifyCallback cb) {
        Gson gson = new Gson();
        List<List<Product>> arrList = new ArrayList<List<Product>>();
        arrList.add(products);
        String jsoned = gson.toJson((arrList));

        URI bb = UriBuilder.fromPath(HTTPS+"://"+APP_ROOT).path(ROUTE).path(APP_KEY)
                .path("injections").queryParam("classname", "Productlist").build();

        Log.i("jsoned", jsoned);

        JsonObjectRequest jaRequest = new JsonObjectRequest(Request.Method.POST, bb.toString(), jsoned,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("resp", response.toString());
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jo = (JsonObject)jsonParser.parse(response.toString());
                        jo = (JsonObject)jo.getAsJsonArray("object").get(0);
                        String objID = jo.getAsJsonPrimitive("objectId").getAsString();
                        String version = jo.getAsJsonPrimitive("version").getAsString();
                        cb.onModify(objID, version);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("err", error.toString());
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("IBM-Application-Secret", APP_SECRET);
                return map;
            }
        };
        queue.add(jaRequest);
    }

    public static void updateList(List<Product> products, RequestQueue queue, String listID, String version, final OnProductModifyCallback cb) {
        Gson gson = new Gson();
        List<List<Product>> arrList = new ArrayList<List<Product>>();
        arrList.add(products);
        JsonElement listEl = gson.toJsonTree(products);
        JsonObject job = new JsonObject();
        job.addProperty("objectId", listID);
        job.addProperty("version", version);
        job.add("updates", listEl);

        Log.i("request", job.toString());

        URI bb = UriBuilder.fromPath(HTTPS+"://"+APP_ROOT).path(ROUTE).path(APP_KEY).path("objects").path(listID).build();

        JsonObjectRequest jaRequest = new JsonObjectRequest(Request.Method.PUT, bb.toString(), job.toString(),
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("resp", response.toString());
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jo = (JsonObject)jsonParser.parse(response.toString());
                        jo = jo.getAsJsonObject("object");
                        String version = jo.getAsJsonPrimitive("version").getAsString();
                        String objID = jo.getAsJsonPrimitive("objectId").getAsString();
                        cb.onModify(objID, version);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("IBM-Application-Secret", APP_SECRET);
                return map;
            }
        };
        queue.add(jaRequest);
    }
}
