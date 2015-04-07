package com.example.tomas.beavents;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

/**
 * Created by LLP-student on 3/26/2015.
 */
public class BaseActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    private String[] mMenuOptions;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;


    protected void onCreateDrawer() {
        //setContentView(R.layout.activity_main);

        mMenuOptions = getResources().getStringArray(R.array.menu);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.categories_list_layout,
                mMenuOptions));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(this);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.options_menu, menu);

        //SearchManager searchManager =
        //        (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //SearchView searchView =
        //        (SearchView) menu.findItem(R.id.search).getActionView();
        //ComponentName cn = new ComponentName(this,SearchResultsActivity.class);
        //searchView.setSearchableInfo(
        //        searchManager.getSearchableInfo(cn));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println(id);
        mDrawerList.setItemChecked(position, true);

        Intent intent = chosenScreenIntent(mMenuOptions[position]);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(intent);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    public Intent chosenScreenIntent(String optionChosen){
        switch(optionChosen){
            case "Suggested Events":
                String eventsToDisplay = "INTERESTS";
                Intent intent = new Intent(this, DisplayMultipleEventsActivity.class);
                intent.putExtra("eventsToDisplay", eventsToDisplay);
                return intent;
            case "All Events":
                return new Intent(this, MainActivity.class);
            case "Search by Categories":
                return new Intent(this, CategoriesActivity.class);
            case "Search by Course Number":
                return new Intent(this, SearchCourseNumberActivity.class);
            case "Create Event":
                return new Intent(this,CreateEventsActivity.class);
            case "My Saved Events":
                return new Intent(this, MySavedEventActivity.class);
            case "My Created Events":
                return new Intent(this, MyCreatedEventActivity.class);

            case "Settings":
                return new Intent(this, SettingsActivity.class);
            default:
                return new Intent(this, MainActivity.class);
        }
    }

    public boolean isDrawerOpen(){
        return mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }
}

