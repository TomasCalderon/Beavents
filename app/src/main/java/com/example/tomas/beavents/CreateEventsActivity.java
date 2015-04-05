package com.example.tomas.beavents;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.text.Editable;
import android.widget.ImageView;


/**
 * Created by LLP-student on 3/26/2015.
 */
public class CreateEventsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_events);
        super.onCreateDrawer();
    }

    /**
     * OnClick method for final button that stores event the user created.
     *
     * @param v
     */
    public void allEvent(View v){
        final EditText titlefld = (EditText) findViewById(R.id.eventTitle);
        final EditText dayfld = (EditText) findViewById(R.id.time);
        final EditText locationfld = (EditText) findViewById(R.id.location);
        final EditText categoriesfld = (EditText) findViewById(R.id.categories);
        final EditText descriptionfld = (EditText) findViewById(R.id.description);
        String eventTitle = titlefld.getText().toString();
        String day = dayfld.getText().toString();
        String location = locationfld.getText().toString();
        String categories = categoriesfld.getText().toString();
        String description = descriptionfld.getText().toString();

        //access database to determine the name for the image

        //String image, String name, String time, String location,
        //String[] categories, String description
        //Event newEvent = new Event()


                }
}



