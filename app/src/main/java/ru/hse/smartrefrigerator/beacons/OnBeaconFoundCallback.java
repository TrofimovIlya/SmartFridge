package ru.hse.smartrefrigerator.beacons;


import java.util.List;

/**
 * Created by KingUrgot on 10.07.2015.
 */
public interface OnBeaconFoundCallback {
    void onFound(List<String> products);
}
