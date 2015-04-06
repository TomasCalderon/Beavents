package com.example.tomas.beavents;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;

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

    }

    private void displayEventInformation() {



        TextView eventName = (TextView) findViewById(R.id.eventName);
        eventName.setText(mEvent.getName());

        TextView eventTime = (TextView) findViewById(R.id.eventTime);
        eventTime.setText(mEvent.getTime());

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

