package ru.hse.smartrefrigerator.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import ru.hse.smartrefrigerator.R;

public class SplashScreenActivity extends Activity {
    private static final String SETTINGS_LOGGED_IN_TAG = "LOGGED_IN";
    protected Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_splash_screen);
        mContext = this;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);


        new Handler().postDelayed(new Runnable() {
            public void run() {
                //SharedPreferences settings = getPreferences(0);

                Intent intent;
                if (prefs.getBoolean(SETTINGS_LOGGED_IN_TAG, false)) {
                   intent = new Intent(mContext, MainActivity.class);
                } else {
                   intent = new Intent(mContext, AuthenticationActivity.class);
                }

                mContext.startActivity(intent);
                finish();
            }
        }, 3500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
