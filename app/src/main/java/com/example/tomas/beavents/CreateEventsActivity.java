package com.example.tomas.beavents;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import static android.widget.ArrayAdapter.*;


/**
 * Created by LLP-student on 3/26/2015.
 */
@SuppressLint("NewApi")
public class CreateEventsActivity extends BaseActivity {

    private static final String CREATE_EVENT_URL = "http://beavents.net84.net/create_event.php";
    private static final String TAG_SUCCESS = "success";
    private ProgressDialog pDialog;
    ProgressDialog prgDialog;
    String encodedString;
    RequestParams params = new RequestParams();
    String imgPath, fileName;
    Bitmap bitmap;
    private static int RESULT_LOAD_IMG = 1;

    public EditText titlefld;
    public Button dayfld;
    public Button startTimefld;
    public Button endTimefld;
    public EditText locationfld;
    public EditText descriptionfld;
    public Spinner course1;
    public Spinner course2;
    public Spinner course3;
    public Spinner category1;
    public Spinner category2;
    public Spinner category3;

    public String imageFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_events);
        super.onCreateDrawer();

        prgDialog = new ProgressDialog(this);
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        titlefld = (EditText) findViewById(R.id.eventTitle);
        dayfld = (Button) findViewById(R.id.date);
        startTimefld= (Button) findViewById(R.id.startTime);
        endTimefld = (Button) findViewById(R.id.endTime);
        locationfld = (EditText) findViewById(R.id.location);
        descriptionfld = (EditText) findViewById(R.id.description);

        dayfld.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                showDatePickerDialog(v);                }
        });
        startTimefld.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                showTimePickerDialog1(v);                }
        });
        endTimefld.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showTimePickerDialog2(v);
            }
        });
        loadSpinners();

    }

    private void loadSpinners() {
        course1 = (Spinner) findViewById(R.id.course1);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = createFromResource(this,
                R.array.course_spinner, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        course1.setAdapter(adapter);

        course2 = (Spinner) findViewById(R.id.course2);
        course2.setAdapter(adapter);
/*
        course3 = (Spinner) findViewById(R.id.course3);
        course3.setAdapter(adapter);*/
        ArrayAdapter<CharSequence> adapter2 = createFromResource(this,
                R.array.category_spinner, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        category1 = (Spinner) findViewById(R.id.category1);
        category2 = (Spinner) findViewById(R.id.category2);
        category3 = (Spinner) findViewById(R.id.category3);

        category1.setAdapter(adapter2);
        category2.setAdapter(adapter2);
        category3.setAdapter(adapter2);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(),"datePicker");
        //newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog1(View v) {
        DialogFragment newFragment = new TimePickerFragment1();
        newFragment.show(getFragmentManager(),"timePicker");
        //newFragment.show(getSupportFragmentManager(), "timePicker");
    }
    public void showTimePickerDialog2(View v) {
        DialogFragment newFragment = new TimePickerFragment2();
        newFragment.show(getFragmentManager(),"timePicker");
        //newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    // When Image is selected from Gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgPath = cursor.getString(columnIndex);
                cursor.close();
                ImageView imgView = (ImageView) findViewById(R.id.imgView);
                // Set the Image in ImageView
                imgView.setImageBitmap(BitmapFactory
                        .decodeFile(imgPath));
                // Get the Image's file name
                String fileNameSegments[] = imgPath.split("/");
                fileName = fileNameSegments[fileNameSegments.length - 1];
                Random rand = new Random();
                for(int i =0; i < 4 ; i++){
                    int randomNum = rand.nextInt(20) + 1;
                    fileName = randomNum + fileName;
                }
                // Put file name in Async Http Post Param which will used in Php web app


                this.imageFile = fileName;
                params.put("filename", fileName);

                //Put fileName into created_events
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor editor = prefs.edit();
                Gson gson = new Gson();
                String all_created_events=prefs.getString("created_events","none");
                if(all_created_events.equals("none")){
                    String[] created_events_list=new String[]{fileName};
                    editor.putString("created_events", gson.toJson(created_events_list));
                    editor.commit();
                } else{
                    String[] created_events_list = gson.fromJson(all_created_events, String[].class);
                    List<String> created_events = new ArrayList(Arrays.asList(created_events_list));

                    created_events.add(fileName);
                    String[] temp = new String[created_events.size()];
                    temp = created_events.toArray(temp);
                    editor.putString("created_events", gson.toJson(temp));
                    editor.commit();
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    // When Upload button is clicked
    public void uploadImage(View v) {
        // When Image is selected from Gallery
        if (imgPath != null && !imgPath.isEmpty()) {
            prgDialog.setMessage("Converting Image to Binary Data");
            prgDialog.show();
            // Convert image to String using Base64
            encodeImagetoString();
            new CreateNewProduct().execute();
            // When Image is not selected from Gallery
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "You must select image from gallery before you try to upload",
                    Toast.LENGTH_LONG).show();
        }

    }

    // AsyncTask - To convert Image to String
    public void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {

            };

            @Override
            protected String doInBackground(Void... params) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                bitmap = BitmapFactory.decodeFile(imgPath,
                        options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String
                encodedString = Base64.encodeToString(byte_arr, 0);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                prgDialog.setMessage("Calling Upload");
                // Put converted Image string into Async Http Post param
                params.put("image", encodedString);
                // Trigger Image upload
                triggerImageUpload();
            }
        }.execute(null, null, null);
    }

    public void triggerImageUpload() {
        makeHTTPCall();
    }

    // http://192.168.2.4:9000/imgupload/upload_image.php
    // http://192.168.2.4:9999/ImageUploadWebApp/uploadimg.jsp
    // Make Http call to upload Image to Php server
    public void makeHTTPCall() {
        prgDialog.setMessage("Invoking Php");
        AsyncHttpClient client = new AsyncHttpClient();
        // Don't forget to change the IP address to your LAN address. Port no as well.
        client.post("http://beavents.net84.net/upload_image.php",
                params, new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {
                        // Hide Progress Dialog
                        prgDialog.hide();
                        Toast.makeText(getApplicationContext(), response,
                                Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(CreateEventsActivity.this, MyCreatedEventActivity.class);
                        startActivity(intent);
                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // Hide Progress Dialog
                        prgDialog.hide();
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(getApplicationContext(),
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(getApplicationContext(),
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Error Occured \n Most Common Error: \n1. Device not connected to Internet\n2. Web App is not deployed in App server\n3. App server is not running\n HTTP Status code : "
                                            + statusCode, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Dismiss the progress bar when application is closed
        if (prgDialog != null) {
            prgDialog.dismiss();
        }
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            System.out.println(month+"/"+day+"/"+year);
            Button dateButton  = (Button) this.getActivity().findViewById(R.id.date);
            dateButton.setText(month+"/"+day+"/"+year);
        }
    }

    /**
     * I am dumb and this is currently the only solution I can think of
     */
    public static class TimePickerFragment1 extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Button startTimeButton  = (Button) this.getActivity().findViewById(R.id.startTime);
            startTimeButton.setText(hourOfDay+":"+minute);
        }
    }
    public static class TimePickerFragment2 extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Button endTimeButton  = (Button) this.getActivity().findViewById(R.id.endTime);
            endTimeButton.setText(hourOfDay+":"+minute);
        }
    }


    class CreateNewProduct extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CreateEventsActivity.this);
            pDialog.setMessage("Creating Event..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {

            String eventTitle = titlefld.getText().toString();
            String date = dayfld.getText().toString();
            String location = locationfld.getText().toString();
            //String categories = categoriesfld.getText().toString();
            String description = descriptionfld.getText().toString();
            String time  = startTimefld.getText().toString()+" to "+endTimefld.getText().toString();
            String cat1= category1.getSelectedItem().toString();
            String cat2= category2.getSelectedItem().toString();
            String cat3= category3.getSelectedItem().toString();
            String courseNum1= course1.getSelectedItem().toString();
            String courseNum2= course2.getSelectedItem().toString();

            if(cat1.equals("Select a Category"))cat1="";
            if(cat2.equals("Select a Category"))cat2="";
            if(cat3.equals("Select a Category"))cat3="";
            if(courseNum1.equals("Select Course Number"))courseNum1="";
            if(courseNum2.equals("Select Course Number"))courseNum2="";


            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Image", imageFile));
            params.add(new BasicNameValuePair("Name", eventTitle));
            params.add(new BasicNameValuePair("Date", date));
            params.add(new BasicNameValuePair("Location", location));
            params.add(new BasicNameValuePair("Time", time));
            params.add(new BasicNameValuePair("Description", description));
            params.add(new BasicNameValuePair("Category1",cat1));
            params.add(new BasicNameValuePair("Category2",cat2));
            params.add(new BasicNameValuePair("Category3",cat3));
            params.add(new BasicNameValuePair("CourseNum1",courseNum1));
            params.add(new BasicNameValuePair("CourseNum2",courseNum2));

            sendData(params);
            pDialog.dismiss();
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }

    public void sendData(List<NameValuePair> params){
        String result = "";
        InputStream isr = null;
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(CREATE_EVENT_URL); //YOUR PHP SCRIPT ADDRESS
            //this is what gives the parameters to the url
            httppost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            isr = entity.getContent();
        }
        catch(Exception e){
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        //convert response to string
        //useful for debugging so do not remove
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(isr,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            isr.close();

            result=sb.toString();
        }
        catch(Exception e){
            Log.e("log_tag", "Error  converting result "+e.toString());
        }

    }

}
