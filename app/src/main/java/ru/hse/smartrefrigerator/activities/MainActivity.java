package ru.hse.smartrefrigerator.activities;

import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import com.android.volley.toolbox.Volley;
import com.dd.CircularProgressButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.service.ServiceResponseException;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import ru.hse.smartrefrigerator.R;
import ru.hse.smartrefrigerator.controllers.DataMarkerInterface;
import ru.hse.smartrefrigerator.controllers.ProductDataProvider;
import ru.hse.smartrefrigerator.fragments.ProductDataProviderFragment;
import ru.hse.smartrefrigerator.fragments.SwipeableProductListFragment;
import ru.hse.smartrefrigerator.models.Product;
import ru.hse.smartrefrigerator.net.OnProductModifyCallback;
import ru.hse.smartrefrigerator.net.ProductListTransmission;
import ru.hse.smartrefrigerator.services.SRNotifyService;
import ru.hse.smartrefrigerator.utils.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements DataMarkerInterface, SwipeableProductListFragment.OnCompleteListener {
    private static final String FRAGMENT_TAG_DATA_PROVIDER = "data provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";

    ServiceConnection sConn;
    Intent intent;
    AudioReciever mRecorder;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    SRNotifyService mService;
    boolean mBound = false;
    boolean recording;
    boolean readyToRecord;
    View mView;
    MaterialEditText nameEditText;
    MaterialEditText dateEditText;
    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SRNotifyService.LocalBinder binder = (SRNotifyService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(new ProductDataProviderFragment(), FRAGMENT_TAG_DATA_PROVIDER)
                    .commit();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SwipeableProductListFragment(), FRAGMENT_LIST_VIEW)
                    .commit();
        }


        init();

        mRecorder = new AudioReciever();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, SRNotifyService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void init() {
        initToolbar();

        recording = false;
        readyToRecord = true;

        final CircularProgressButton circularButton1 = (CircularProgressButton) findViewById(R.id.circularButton1);
        circularButton1.setIndeterminateProgressMode(true);
        circularButton1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (readyToRecord && !recording && event.getAction() == MotionEvent.ACTION_DOWN) {
                    circularButton1.setProgress(50);
                    mRecorder.startRecording();
                    readyToRecord = false;
                    recording = true;
                } else if (recording && event.getAction() == MotionEvent.ACTION_UP) {
                    recording = false;
                    mRecorder.stopRecording();
                    final SpeechToText service = new SpeechToText();
                    service.setUsernameAndPassword(PrivateData.getBluemixUser(), PrivateData.getBluemixPass());
                    final File audio = new File(mRecorder.getFileName());
                    if (audio.isFile()) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String transcript;

                                try {
                                    transcript = service.recognize(audio, "audio/l16; rate=44100").toString();
                                } catch (ServiceResponseException e) {
                                    Log.i("tag", e.getMessage());
                                    transcript = "";
                                } catch (Exception e) {
                                    transcript = "";
                                }

                                final String transcriptf = transcript;

                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        circularButton1.setProgress(100);
                                        createDialog(DateParse.parseTranscript(transcriptf));

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                circularButton1.setProgress(0);
                                                readyToRecord = true;
                                            }
                                        }, 1500);
                                    }
                                });

                            }
                        }).start();

                    }
                }

                return true;
            }
        });
    }

    private void createDialog(String s) {
        mView = getLayoutInflater().inflate(R.layout.dialog_add_product, null);
        nameEditText = (MaterialEditText) mView.findViewById(R.id.et_name);
        dateEditText = (MaterialEditText) mView.findViewById(R.id.et_date);

        if (s.length() > 0) {
            String[] ss = DateParse.parseVoiceString(s);
            nameEditText.setText(ss[0]);
            dateEditText.setText(ss[1]);
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
        builder.setView(mView);
        builder.setTitle(R.string.dialog_title);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.ready, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (nameEditText != null && dateEditText != null) {
                    String name = nameEditText.getText().toString();
                    String date = dateEditText.getText().toString();

                    if (date.length() > 0 && name.length() > 0) {
                        getDataProvider().addProduct(new Product(name, DateParse.dateFromString(date)));
                        refreshRV();

                        String id = prefs.getString(PreferencesConsts.USER_ID, PrivateData.getDefUserId());
                        String version = prefs.getString(PreferencesConsts.LIST_VERSION, "0");
                        editor.putString(PreferencesConsts.LIST, JsonHelper.getJSONFromList(getDataProvider().getProducts()));
                        editor.commit();

                        ProductListTransmission.updateList(getDataProvider().getProducts(), Volley.newRequestQueue(MainActivity.this), id, version, new OnProductModifyCallback() {
                            @Override
                            public void onModify(String id, String version) {
                                editor.putString(PreferencesConsts.LIST_VERSION, version);
                                editor.commit();
                            }
                        });

                        if (mBound) {
                            mService.setUserProducts(getDataProvider().getProducts());
                        }
                    }
                }
            }
        });
        builder.show();
    }

    private void initToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.iconwhite);
        }
    }

    /**
     * This method will be called when a list item is removed
     *
     * @param position The position of the item within data set
     */
    @Override
    public void onItemRemoved(int position) {
        SnackbarManager.show(
                Snackbar.with(getApplicationContext())
                        .text(R.string.item_deleted)
                        .actionLabel(R.string.undo)
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {
                                onItemUndoActionClicked();
                            }
                        })
                        .actionColorResource(R.color.snackbar_undo_color)
                        .duration(2000)
                        .type(SnackbarType.MULTI_LINE)
                        .swipeToDismiss(false)
                , this);

        String id = prefs.getString(PreferencesConsts.USER_ID, PrivateData.getDefUserId());
        String version = prefs.getString(PreferencesConsts.LIST_VERSION, "0");
        editor.putString(PreferencesConsts.LIST, JsonHelper.getJSONFromList(getDataProvider().getProducts()));
        editor.commit();

        ProductListTransmission.updateList(getDataProvider().getProducts(), Volley.newRequestQueue(this), id, version, new OnProductModifyCallback() {
            @Override
            public void onModify(String id, String version) {
                editor.putString(PreferencesConsts.LIST_VERSION, version);
                editor.commit();
            }
        });

        if (mBound) {
            mService.setUserProducts(getDataProvider().getProducts());
        }
    }

    /**
     * This method will be called when a list item is clicked
     *
     * @param position The position of the item within data set
     */
    @Override
    public void onItemClicked(int position) {
        // nothing
    }

    // call me whenever you add items
    private void refreshRV() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
        ((SwipeableProductListFragment) fragment).notifyDataSetChanged();
    }

    private void onItemUndoActionClicked() {
        int position = getDataProvider().undoLastRemoval();
        if (position >= 0) {
            final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
            ((SwipeableProductListFragment) fragment).notifyItemInserted(position);
        }

        String id = prefs.getString(PreferencesConsts.USER_ID, PrivateData.getDefUserId());
        String version = prefs.getString(PreferencesConsts.LIST_VERSION, "0");
        editor.putString(PreferencesConsts.LIST, JsonHelper.getJSONFromList(getDataProvider().getProducts()));
        editor.commit();

        ProductListTransmission.updateList(getDataProvider().getProducts(), Volley.newRequestQueue(this), id, version, new OnProductModifyCallback() {
            @Override
            public void onModify(String id, String version) {
                editor.putString(PreferencesConsts.LIST_VERSION, version);
                editor.commit();
            }
        });

        if (mBound) {
            mService.setUserProducts(getDataProvider().getProducts());
        }
    }

    @Override
    public ProductDataProvider getDataProvider() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_DATA_PROVIDER);
        return ((ProductDataProviderFragment) fragment).getDataProvider();
    }

    @Override
    public void onSwipeableComplete() {
        String id = prefs.getString(PreferencesConsts.USER_ID, PrivateData.getDefUserId());

        getDataProvider().initData();
        getDataProvider().addProducts(JsonHelper.getListFromJSON(prefs.getString(PreferencesConsts.LIST, "")));
        refreshRV();

        if (mBound) {
            mService.setUserProducts(getDataProvider().getProducts());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
