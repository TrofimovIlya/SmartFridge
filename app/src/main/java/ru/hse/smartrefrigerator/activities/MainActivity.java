package ru.hse.smartrefrigerator.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.ButterKnife;

import com.melnykov.fab.FloatingActionButton;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import ru.hse.smartrefrigerator.R;
import ru.hse.smartrefrigerator.controllers.DataMarkerInterface;
import ru.hse.smartrefrigerator.controllers.ProductDataProvider;
import ru.hse.smartrefrigerator.fragments.AddressesDataProviderFragment;
import ru.hse.smartrefrigerator.fragments.SwipeableProductListFragment;
import ru.hse.smartrefrigerator.models.Product;


public class MainActivity extends AppCompatActivity implements DataMarkerInterface, SwipeableProductListFragment.OnCompleteListener {
    private static final String FRAGMENT_TAG_DATA_PROVIDER = "data provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";

    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(new AddressesDataProviderFragment(), FRAGMENT_TAG_DATA_PROVIDER)
                    .commit();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SwipeableProductListFragment(), FRAGMENT_LIST_VIEW)
                    .commit();
        }


        init();
    }

    @Override
    public void onResume() {
        super.onResume();

        mFab.hide(false);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                mFab.show(true);
            }
        }, 500);
    }

    private void init() {
        initToolbar();

        mFab = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void initToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
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
    }

    @Override
    public ProductDataProvider getDataProvider() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_DATA_PROVIDER);
        return ((AddressesDataProviderFragment) fragment).getDataProvider();
    }

    @Override
    public void onSwipeableComplete() {
        // load items here

        for (int i = 0; i < 20; i++) {
            getDataProvider().addProduct(new Product("ProductName " + i));
        }

        refreshRV();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
