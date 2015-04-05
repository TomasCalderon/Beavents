package com.example.tomas.beavents;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by LLP-student on 3/26/2015.
 */
public class MyEventsActivity extends BaseActivity{
    ActionBar.Tab tab1, tab2;
    //Fragment fragmentTab1 = new MySavedEventActivity();
    //Fragment fragmentTab2 = new MyCreatedEventActivity();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_events);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        tab1 = actionBar.newTab();
        tab1.setText("1");
        tab2 = actionBar.newTab();
        tab2.setText("2");

        //tab1.setTabListener(new TabListenerActivity(fragmentTab1));
        //tab2.setTabListener(new TabListenerActivity(fragmentTab2));

        actionBar.addTab(tab1);
        actionBar.addTab(tab2);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
