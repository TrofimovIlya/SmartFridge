package ru.hse.smartrefrigerator.activities;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

import ru.hse.smartrefrigerator.R;

public class AuthenticationActivity extends Activity {

    private static final String SETTINGS_LOGGED_IN_TAG = "LOGGED_IN";
    private static final String GMAIL = "login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        ImageButton bLoginGPlus = (ImageButton)findViewById(R.id.bLoginGPlus);
        if(bLoginGPlus != null) {
            bLoginGPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                                new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false, null, null, null, null);
                        startActivityForResult(intent, 1);
                    } catch (ActivityNotFoundException e) {

                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.authentication, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            SharedPreferences mySharedPreferences = getPreferences(0);
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putBoolean(SETTINGS_LOGGED_IN_TAG,true);
            editor.putString(GMAIL, accountName);
            editor.commit();

            Intent go = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(go);


        }
    }
}
