package ru.hse.smartrefrigerator.net;

import java.util.List;

/**
 * Created by KingUrgot on 09.07.2015.
 */
public class BeaconMark {
    String uID;
    List<String> productList;

    public BeaconMark(List<String> productList, String uID) {
        this.productList = productList;
        this.uID = uID;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public List<String> getProductList() {
        return productList;
    }

    public void setProductList(List<String> productList) {
        this.productList = productList;
    }

    @Override
    public String toString() {
        return "uID: " + uID + "\nProducts: " + productList;
    }
}
