package ru.hse.smartrefrigerator.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import ru.hse.smartrefrigerator.models.Product;

import java.util.ArrayList;
import java.util.List;

public class JsonHelper {
    public static ArrayList<Product> getListFromJSON(String jsonString) {
        if (jsonString.length() == 0) {
            return new ArrayList<Product>();
        }

        JsonParser jsonParser = new JsonParser();
        Gson gson = new Gson();

        JsonArray productsList = jsonParser.parse(jsonString).getAsJsonArray();
        ArrayList<Product> resList = new ArrayList<Product>();
        for (JsonElement product : productsList) {
            Product p = gson.fromJson(product, Product.class);
            resList.add(p);
        }

        return resList;
    }

    public static String getJSONFromList(List<Product> products) {
        return new Gson().toJson(products);
    }
}
