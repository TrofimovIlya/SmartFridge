package ru.hse.smartrefrigerator.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import ru.hse.smartrefrigerator.R;
import ru.hse.smartrefrigerator.models.Product;
import ru.hse.smartrefrigerator.net.OnProductModifyCallback;
import ru.hse.smartrefrigerator.net.ProductListTransmission;
import ru.hse.smartrefrigerator.utils.PreferencesConsts;

import java.util.ArrayList;

//import com.google.android.gms.auth.GoogleAuthUtil;
//import com.google.android.gms.common.AccountPicker;

public class AuthenticationActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        ImageButton bLoginGPlus = (ImageButton) findViewById(R.id.bLoginGPlus);
        if (bLoginGPlus != null) {
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
        if (id == R.id.home) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            ProductListTransmission.addList(new ArrayList<Product>(), Volley.newRequestQueue(this), new OnProductModifyCallback() {
                @Override
                public void onModify(String id, String version) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AuthenticationActivity.this);
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putString(PreferencesConsts.LIST_VERSION, version);
                    editor.putString(PreferencesConsts.USER_ID, id);

                    editor.apply();
                }
            });

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }
}
