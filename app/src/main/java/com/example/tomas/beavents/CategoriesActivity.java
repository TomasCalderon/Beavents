package com.example.tomas.beavents;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by LLP-student on 3/26/2015.
 */

public class CategoriesActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private String[] mEventCategories;
    private ListView mCategoriesListView;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories);
        super.onCreateDrawer();

        mEventCategories = getResources().getStringArray(R.array.eventCategories);
        mCategoriesListView = (ListView) findViewById(R.id.listOfCategories);

        // Set the adapter for the list view
        mCategoriesListView.setAdapter(new ArrayAdapter<String>(this,R.layout.categories_list_layout,
                mEventCategories));
        // Set the list's click listener
        mCategoriesListView.setOnItemClickListener(this);



    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mCategoriesListView.setItemChecked(position, true);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}