package android.eservices.dynamicfragmentmenu;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationInterface {

    private final static String FRAGMENT_NUMBER_KEY = "Fragment_Number";
    private final static String FRAGMENT_STORED_KEY = "Fragment_Stored";
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private SelectableNavigationView navigationView;
    private SparseArray<Fragment> fragmentArray;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupNavigationElements();


        //If available :
        if (savedInstanceState != null) {
            //1° - Retrieve the stored fragment from the saved bundle (see SO link in indications below, bottom of the class)
            //2° - Use the replace fragment to display the retrieved fragment
            //3° - Add the restored fragment to the cache so it is not recreated when selected the menu item again
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_STORED_KEY);
            replaceFragment(currentFragment);
            int menuIdx = savedInstanceState.getInt(FRAGMENT_NUMBER_KEY);
            fragmentArray.append(menuIdx, currentFragment);
        }else{
            //If the bundle is null, then display the default fragment using navigationView.setSelectedItem();
            navigationView.setSelectedItem(navigationView.getMenu().getItem(0));
        }
        //Let's imagine we retrieve the stored counter state, before creating the favorite Fragment
        //and then be able to update and manage its state.
        updateFavoriteCounter(3);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setupNavigationElements() {
        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);

        fragmentArray = new SparseArray<>(3);

        navigationView = findViewById(R.id.navigation);
        navigationView.inflateHeaderView(R.layout.navigation_header);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemIdx = menuItem.getOrder();
                if(fragmentArray.indexOfKey(itemIdx) < 0){
                    switch (itemIdx){
                        case 0:
                            fragmentArray.append(itemIdx, SelectedFragment.newInstance());
                            break;
                        case 1:
                            fragmentArray.append(itemIdx, FavoritesFragment.newInstance());
                            break;
                        default:
                            logoff();
                            return false;
                    }
                }
                currentFragment = fragmentArray.get(itemIdx);
                replaceFragment(currentFragment);
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }


    private void replaceFragment(Fragment newFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, newFragment);
        fragmentTransaction.commit();
    }

    private void logoff() {
        finish();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void updateFavoriteCounter(int counter) {
        String counterContent = counter > 9 ? getString(R.string.counter_full) : Integer.toString(counter);
        View counterView = navigationView.getMenu().getItem(1).getActionView();
        counterView.setVisibility(counter > 0 ? View.VISIBLE : View.GONE);
        ((TextView) counterView.findViewById(R.id.counter_view)).setText(counterContent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        int itemIdx = navigationView.getCheckedItem().getOrder();
        outState.putInt(FRAGMENT_NUMBER_KEY, itemIdx);
        getSupportFragmentManager().putFragment(outState, FRAGMENT_STORED_KEY, currentFragment);

    }

}
