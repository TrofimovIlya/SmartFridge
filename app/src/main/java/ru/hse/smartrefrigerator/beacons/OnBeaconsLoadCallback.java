package ru.hse.smartrefrigerator.beacons;


import java.util.List;

/**
 * Created by KingUrgot on 09.07.2015.
 */
public interface OnBeaconsLoadCallback {

    void onLoad(List<BeaconMark> beacons);

}
