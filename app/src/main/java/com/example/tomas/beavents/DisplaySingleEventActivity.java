package com.example.tomas.beavents;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by LLP-student on 4/3/2015.
 */
public class DisplaySingleEventActivity extends BaseActivity {

    private Event mEvent;



    private ImageView mImageView;
    private Bitmap mBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_single_event_activity);
        super.onCreateDrawer();

        this.mEvent = (Event) getIntent().getSerializableExtra(DisplayMultipleEventsActivity.SER_KEY);

        this.mImageView = (ImageView) findViewById(R.id.eventImage);
        new LoadImage().execute(DisplayMultipleEventsActivity.IMAGEBASE + mEvent.getImage());

        this.displayEventInformation();
        Button saveButton = (Button) findViewById(R.id.saveButton);

        if(!this.checkIfSaved(mEvent.getImage())){
            this.enableSaveButton();
        }
        else{
            this.disableSaveButton();
        }
    }

    /**
     * Change save button to save events
     */
    private void enableSaveButton(){
        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setText("Save/Bookmark");
        saveButton.setBackgroundColor(Color.parseColor("#C5D9C6"));
        saveButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                saveEvent();                }
        });
    }

    /**
     * Change save button to remove event
     */
    private void disableSaveButton(){
        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setText("Remove from saved events");
        saveButton.setBackgroundColor(Color.parseColor("#D9ABAB"));
        saveButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                removeEvent();                }
        });
    }

    private void saveEvent(){
        String fileName=mEvent.getImage();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String all_saved_events=prefs.getString("saved_events","none");
        if(all_saved_events.equals("none")){
            String[] created_events_list=new String[]{fileName};
            editor.putString("saved_events", gson.toJson(created_events_list));
            editor.commit();
        } else{
            String[] created_events_list = gson.fromJson(all_saved_events, String[].class);
            List<String> created_events = new ArrayList(Arrays.asList(created_events_list));
            if(!created_events.contains(fileName)){
                created_events.add(fileName);
                String[] temp = new String[created_events.size()];
                temp = created_events.toArray(temp);
                editor.putString("saved_events", gson.toJson(temp));
                editor.commit();
            }
        }

        this.disableSaveButton();
    }

    private void removeEvent(){
        String fileName=mEvent.getImage();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String all_saved_events=prefs.getString("saved_events","none");
        if(all_saved_events.equals("none")){
            //Do nothing
        } else{
            String[] created_events_list = gson.fromJson(all_saved_events, String[].class);
            List<String> created_events = new ArrayList(Arrays.asList(created_events_list));
            if(created_events.contains(fileName)){
                created_events.remove(fileName);
                String[] temp = new String[created_events.size()];
                temp = created_events.toArray(temp);
                editor.putString("saved_events", gson.toJson(temp));
                editor.commit();
            }
        }

        this.enableSaveButton();

    }




    private boolean checkIfSaved(String fileName){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String saved_events=prefs.getString("saved_events","none");
        if(saved_events.equals("none")){
            return false;
        } else{
            Gson gson = new Gson();
            String[] events_list = gson.fromJson(saved_events, String[].class);
            List<String> events = new ArrayList(Arrays.asList(events_list));
            return events.contains(fileName);
        }
    }

    private void displayEventInformation() {



        TextView eventName = (TextView) findViewById(R.id.eventName);
        eventName.setText(mEvent.getName());

        TextView eventTime = (TextView) findViewById(R.id.eventTime);
        eventTime.setText(mEvent.getDate()+"\n"+mEvent.getTime());


        TextView eventLocation = (TextView) findViewById(R.id.eventLocation);
        eventLocation.setText(mEvent.getLocation());

        TextView eventDescription = (TextView) findViewById(R.id.eventDescription);
        eventDescription.setText(mEvent.getDescription());

    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        protected Bitmap doInBackground(String... args) {
            try {
                mBitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            } catch ( Exception e) {
                e.printStackTrace();
            }
            return mBitmap;
        }
        protected void onPostExecute(Bitmap image) {
            if(image != null){
                mImageView.setImageBitmap(image);
            }else{
                Toast.makeText(DisplaySingleEventActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

