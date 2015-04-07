package com.example.tomas.beavents;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by LLP-student on 4/3/2015.
 */
public class DisplaySingleEventActivity extends BaseActivity {

    private Event mEvent;

    private ProgressDialog pDialog;

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

        TextView addToCalendar = (TextView) findViewById(R.id.calendar_button);
        addToCalendar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                addToCalendar();                }
        });



        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        if(this.checkIfCreatedByUser(mEvent.getImage())){
            deleteButton.setVisibility(View.VISIBLE);
        };

//        deleteButton.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//                deleteCurrentEvent();                }
//        });
    }



    private boolean checkIfCreatedByUser(String fileName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String saved_events=prefs.getString("created_events","none");
        if(saved_events.equals("none")){
            return false;
        } else{
            Gson gson = new Gson();
            String[] events_list = gson.fromJson(saved_events, String[].class);
            List<String> events = new ArrayList(Arrays.asList(events_list));
            return events.contains(fileName);
        }
    }

    private Integer[] convertEventTime(){
        // Converts event's time to and integer array
        //      If time is not valid: returns []
        //      If time is valid (starttime to endtime):
        //             returns [start_hourOfDay, start_minute, end_hourOfDay, end_minute]

        String time = mEvent.getTime();
        try{
            String startTime = time.split(" to ")[0];
            int add=0;
            if(startTime.split(" ")[1]=="PM") add=12;
            startTime=startTime.split(" ")[0];
            int start_hourOfDay = Integer.parseInt(startTime.split(":")[0])+add;
            int start_minute = Integer.parseInt(startTime.split(":")[1]);


            String endTime = time.split(" to ")[1];
            add=0;
            if(endTime.split(" ")[1]=="PM") add=12;
            endTime=endTime.split(" ")[0];
            int end_hourOfDay = Integer.parseInt(endTime.split(":")[0])+add;
            int end_minute = Integer.parseInt(endTime.split(":")[1]);

            return new Integer[]{start_hourOfDay, start_minute, end_hourOfDay, end_minute};

        } catch(Exception e){
            return new Integer[]{};
        }
    }

    private Integer[] convertEventDate(){
        // Converts event's time to and integer array
        //      If date is not valid: returns [2015,3,1] april fools!
        //      If date is valid (mm/dd/yyyy):
        //             returns [year,month,day]
        // NOTE GREGORIAN CALENDAR TREATS MONTH 0 AS JANUARY
        String date = mEvent.getDate();
        try{
            int month = Integer.parseInt(date.split("/")[0])-1;
            int day = Integer.parseInt(date.split("/")[1]);
            int year = Integer.parseInt(date.split("/")[2]);
            return new Integer[]{year,month,day};

        } catch(Exception e){
            return new Integer[]{2015,3,1};
        }

    }

    private void addToCalendar(){
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.TITLE, mEvent.getName());
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, mEvent.getLocation());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, mEvent.getDescription());

        Integer[] date = convertEventDate();
        Integer[] time = convertEventTime();

        if(time.length==0){ // all day event
            GregorianCalendar calDate = new GregorianCalendar(date[0], date[1], date[2]);
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                    calDate.getTimeInMillis());
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                    calDate.getTimeInMillis());
            intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
        } else{
            GregorianCalendar calDate1 = new GregorianCalendar(date[0], date[1], date[2], time[0], time[1]);
            GregorianCalendar calDate2 = new GregorianCalendar(date[0], date[1], date[2], time[2], time[3]);
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                    calDate1.getTimeInMillis());
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                    calDate2.getTimeInMillis());
        }

        // make it a recurring Event
        //intent.putExtra(Events.RRULE, "FREQ=WEEKLY;COUNT=11;WKST=SU;BYDAY=TU,TH");

        // Making it private and shown as busy
        intent.putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE);
        intent.putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

        startActivity(intent);

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

//    private void deleteCurrentEvent() {
//        new DeleteEvent().execute();
//    }
//
//    class DeleteEvent extends AsyncTask<String, String, String> {
//
//        /**
//         * Before starting background thread Show Progress Dialog
//         * */
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(DisplaySingleEventActivity.this);
//            pDialog.setMessage("Deleting Event..");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(true);
//            pDialog.show();
//        }
//
//        /**
//         * Creating product
//         * */
//        protected String doInBackground(String... args) {
//
//            String eventTitle = titlefld.getText().toString();
//            String date = dayfld.getText().toString();
//            String location = locationfld.getText().toString();
//            //String categories = categoriesfld.getText().toString();
//            String description = descriptionfld.getText().toString();
//            String time  = startTimefld.getText().toString()+" to "+endTimefld.getText().toString();
//            String cat1= category1.getSelectedItem().toString();
//            String cat2= category2.getSelectedItem().toString();
//            String cat3= category3.getSelectedItem().toString();
//
//
//            String courseNum1= course1.getSelectedItem().toString();
//            String courseNum2= course2.getSelectedItem().toString();
//
//            if(cat1.equals("Select a Category"))cat1="";
//            if(cat2.equals("Select a Category"))cat2="";
//            if(cat3.equals("Select a Category"))cat3="";
//            if(courseNum1.equals("Select Course Number"))courseNum1="0";
//            else courseNum1=courseNum1.split(" ")[1];
//            if(courseNum2.equals("Select Course Number"))courseNum2="0";
//            else courseNum2=courseNum2.split(" ")[1];
//
//
//            // Building Parameters
//            List<NameValuePair> params = new ArrayList<NameValuePair>();
//            params.add(new BasicNameValuePair("Image", imageFile));
//            params.add(new BasicNameValuePair("Name", eventTitle));
//            params.add(new BasicNameValuePair("Date", date));
//            params.add(new BasicNameValuePair("Location", location));
//            params.add(new BasicNameValuePair("Time", time));
//            params.add(new BasicNameValuePair("Description", description));
//            params.add(new BasicNameValuePair("Category1",cat1));
//            params.add(new BasicNameValuePair("Category2",cat2));
//            params.add(new BasicNameValuePair("Category3",cat3));
//            params.add(new BasicNameValuePair("CourseNum1",courseNum1));
//            params.add(new BasicNameValuePair("CourseNum2",courseNum2));
//
//            sendData(params);
//            pDialog.dismiss();
//            return null;
//        }
//
//        /**
//         * After completing background task Dismiss the progress dialog
//         * **/
//        protected void onPostExecute(String file_url) {
//            // dismiss the dialog once done
//            pDialog.dismiss();
//        }

}

