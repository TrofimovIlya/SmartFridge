package ru.hse.smartrefrigerator.beacons;

import java.util.List;

/**
 * Created by KingUrgot on 09.07.2015.
 */
public class BeaconMark {
    Integer uID;
    List<String> productList;

    public BeaconMark(List<String> productList, Integer uID) {
        this.productList = productList;
        this.uID = uID;
    }

    public Integer getuID() {
        return uID;
    }

    public void setuID(Integer uID) {
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
