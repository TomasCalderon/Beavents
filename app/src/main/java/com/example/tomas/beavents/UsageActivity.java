package com.example.tomas.beavents;

import android.app.ActionBar;
import android.os.Bundle;

/**
 * Created by Kathy on 4/11/2015.
 */
public class UsageActivity extends BaseActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usage_page);
        super.onCreateDrawer();
    }
}
