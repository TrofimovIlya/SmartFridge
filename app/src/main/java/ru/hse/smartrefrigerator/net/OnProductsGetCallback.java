package ru.hse.smartrefrigerator.net;

import ru.hse.smartrefrigerator.models.Product;

import java.util.List;

/**
 * Created by KingUrgot on 09.07.2015.
 */
public interface OnProductsGetCallback {
    void onGet(List<Product> products);
}
