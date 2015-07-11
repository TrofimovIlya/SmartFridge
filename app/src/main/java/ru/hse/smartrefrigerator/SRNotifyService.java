package ru.hse.smartrefrigerator;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.android.volley.toolbox.Volley;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import ru.hse.smartrefrigerator.activities.MainActivity;
import ru.hse.smartrefrigerator.beacons.BeaconMark;
import ru.hse.smartrefrigerator.beacons.BeaconTransmission;
import ru.hse.smartrefrigerator.beacons.OnBeaconFoundCallback;
import ru.hse.smartrefrigerator.beacons.OnBeaconsLoadCallback;
import ru.hse.smartrefrigerator.models.Product;

public class SRNotifyService extends Service {
    static int id = 0;
    public SRNotifyService() {
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public SRNotifyService getService() {
            // Return this instance of LocalService so clients can call public methods
            return SRNotifyService.this;
        }
    }



    @Override
    public IBinder onBind(Intent intent) {
        beaconManager = new BeaconManager(this);

        Thread searchThread = new Thread(new Runnable() {
            @Override
            public void run() {
                BeaconTransmission.loadBeacons(Volley.newRequestQueue(SRNotifyService.this), new OnBeaconsLoadCallback() {
                    @Override
                    public void onLoad(List<BeaconMark> bes) {

                        //Все биконы в БД
                        loadedBeacons = bes;

                        startBeaconSearch(bes, ALL_ESTIMOTE_BEACONS, beaconManager, new OnBeaconFoundCallback() {
                            @Override
                            public void onFound(List<String> products) {
                                List<String> required = new LinkedList<String>();

                                for (Product prod : userProducts) {
                                    if (prod.getExpirationDate().before(new Date(System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)))) {
                                        for (String product : products) {
                                            if (product.toLowerCase().equals(prod.getName().toLowerCase())) {
                                                required.add(product);
                                            }
                                        }
                                    }
                                }

                                //Настоящий день
                                Integer year = localCalendar.get(Calendar.YEAR);
                                Integer month = localCalendar.get(Calendar.MONTH);
                                Integer day = localCalendar.get(Calendar.DAY_OF_MONTH);

                                //Если в этот день ещё не было уведомления
                                if (!year.equals(lastCheckBeaconYear) || !month.equals(lastCheckBeaconMonth) || !day.equals(lastCheckBeaconDay)) {
                                    Context context = getApplicationContext();
                                    String require = "";
                                    for (String product: required){
                                        require += product + ", ";
                                    }

                                    if (require.length() > 3) {
                                        require = require.substring(0, require.length() - 2);
                                    }

                                    Intent resultIntent = new Intent(context, MainActivity.class);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                                            resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    Notification n = new Notification.Builder(context)
                                            .setContentTitle("Полезный совет")
                                            .setSmallIcon(R.drawable.notificon)
                                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                            .setContentIntent(pendingIntent)
                                            .setStyle(new Notification.BigTextStyle().bigText("В магазине Шестёрочка, что находится неподалёку, можно приобрести свежие продукты, так как у продуктов " + require + " из Вашего холодильника скоро истекает срок годности."))
                                            .build();

                                    NotificationManager notificationManager =
                                            (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

                                    notificationManager.notify(id++, n);

                                    //сохранить день, в который было послено уведомление
                                    lastCheckBeaconYear = year;
                                    lastCheckBeaconMonth = month;
                                    lastCheckBeaconDay = day;
                                }
                            }
                        });
                    }
                });
            }
        });

        searchThread.start();

        return mBinder;
    }


    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private static final Region ALL_ESTIMOTE_BEACONS = new Region("SomeRegionName", ESTIMOTE_PROXIMITY_UUID, null, null);

    private BeaconManager beaconManager;

    List<Product> userProducts = new LinkedList<Product>();
    List<BeaconMark> loadedBeacons;
    Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());

    //День последнего последнего оповещения о магазине
    Integer lastCheckBeaconYear;
    Integer lastCheckBeaconMonth;
    Integer lastCheckBeaconDay;

    //День последнего последнего оповещения о сроке годности
    Integer lastExpNotifyYear;
    Integer lastExpNotifyMonth;
    Integer lastExpNotifyDay;

    public void setUserProducts(List<Product> userProducts) {
        this.userProducts = userProducts;
    }

    void startBeaconSearch(final List<BeaconMark> loadedBeacons, Region region, final BeaconManager beaconManager, final OnBeaconFoundCallback cb) {
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {

                //Настоящий день
                Integer year = localCalendar.get(Calendar.YEAR);
                Integer month = localCalendar.get(Calendar.MONTH);
                Integer day = localCalendar.get(Calendar.DAY_OF_MONTH);

                //Если в этот день ещё не было уведомления
                if (!year.equals(lastExpNotifyYear) || !month.equals(lastExpNotifyMonth) || !day.equals(lastExpNotifyDay)) {

                    List<String> expire = new LinkedList<String>();

                    for (Product p : userProducts) {
                        if (p.getExpirationDate().before(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)))) {
                            expire.add(p.getName());
                        }
                    }

                    if (!expire.isEmpty()) {
                        Context context = getApplicationContext();
                        String require = "";
                        for (String product: expire) {
                            require += product + ", ";
                        }

                        if (require.length() > 3) {
                            require = require.substring(0, require.length() - 2);
                        }

                        Intent resultIntent = new Intent(context, MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        Notification n = new Notification.Builder(context)
                                .setContentTitle("Smart Fridge")
                                .setSmallIcon(R.drawable.notificon)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setContentIntent(pendingIntent).setStyle(new Notification.BigTextStyle().bigText("Продукты, которые скоро испортятся: " + require)).build();


                        NotificationManager notificationManager =
                                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

                        notificationManager.notify(id++, n);
                    }

                    //сохранить день, в который было послено уведомление
                    lastExpNotifyYear = year;
                    lastExpNotifyMonth = month;
                    lastExpNotifyDay = day;
                }

                for (Beacon bc : beacons) {
                    for (BeaconMark bm : loadedBeacons) {
                        if (bc.getMajor() == bm.getuID()) {
                            cb.onFound(bm.getProductList());
                        }
                    }
                }
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
                } catch (RemoteException e) {
                    //Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
    }


    public void onDestroy() {
        super.onDestroy();
        try {
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
        } catch (RemoteException e) {
            Log.e("Destroy", "Cannot stop but it does not matter now", e);
        }
        beaconManager.disconnect();
    }
}
