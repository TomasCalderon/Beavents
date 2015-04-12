package com.example.tomas.beavents;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by LLP-student on 4/6/2015.
 */
public class SearchCourseNumberActivity extends BaseActivity{
    private String[] mEventCategories;
    private ListView mCategoriesListView;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ((BeaventsApplication) getApplication()).getTracker()
                .trackScreenView("/search_by_course_number", "Search By Course Number");
        setContentView(R.layout.categories);
        super.onCreateDrawer();

        mEventCategories = getResources().getStringArray(R.array.course_list_preference);
        mCategoriesListView = (ListView) findViewById(R.id.listOfCategories);

        // Set the adapter for the list view
        mCategoriesListView.setAdapter(new ArrayAdapter<String>(this,R.layout.categories_list_layout,
                mEventCategories));
        // Set the list's click listener


        mCategoriesListView.setOnItemClickListener(this);



    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (super.isDrawerOpen())super.onItemClick(parent,view,position,id);
        else{
            ((BeaventsApplication) getApplication()).getTracker().trackEvent("search_by_course_number", "button_click", "search", position);
            mCategoriesListView.setItemChecked(position, true);
            String eventsToDisplay = mEventCategories[position].split(" ")[1];
            Intent intent = new Intent(this, DisplayMultipleEventsActivity.class);
            intent.putExtra("eventsToDisplay", "number"+eventsToDisplay);
            startActivity(intent);
        }
    }
}
