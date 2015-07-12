package ru.hse.smartrefrigerator.net;

import ru.hse.smartrefrigerator.models.Product;

import java.util.ArrayList;

/**
 * Created by KingUrgot on 09.07.2015.
 */
public interface OnProductsGetCallback {
    void onGet(ArrayList<Product> products);
}
