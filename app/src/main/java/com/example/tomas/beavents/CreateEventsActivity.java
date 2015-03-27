package com.example.tomas.beavents;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by LLP-student on 3/26/2015.
 */
public class CreateEventsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_events);
        super.onCreateDrawer();
    }


}
