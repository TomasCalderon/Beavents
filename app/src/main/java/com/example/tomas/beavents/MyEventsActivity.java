package com.example.tomas.beavents;

import android.os.Bundle;

/**
 * Created by LLP-student on 3/26/2015.
 */
public class MyEventsActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_events);
        super.onCreateDrawer();
    }
}
