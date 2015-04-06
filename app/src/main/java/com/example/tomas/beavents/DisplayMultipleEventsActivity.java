package com.example.tomas.beavents;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LLP-student on 4/5/2015.
 */
public class DisplayMultipleEventsActivity extends BaseActivity {
    public  final static String SER_KEY = "com.example.tomas.beavents.events";
    public final static String DATABASE = "http://beavents.net84.net/get_all_events.php";
    public final static String IMAGEBASE = "http://beavents.net84.net/images/";


    private List<String> imagePaths = new ArrayList<>();
    private List<String> categories = new ArrayList<>();
    DisplayImageOptions options;
    private ImageLoader imageLoader;
    private List<Event> loadedEvents = new ArrayList<>();
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.onCreateDrawer();

        StrictMode.enableDefaults(); //STRICT MODE ENABLED

        String eventsToDisplay = (String) getIntent().getExtras().getString("eventsToDisplay");

        if(eventsToDisplay.equals("INTERESTS")){
            //Put settings in categories
        }else{
            categories.add(eventsToDisplay);
        }

        new LoadImage(this).execute(0);



        //David was here

        //Block of code to make events clickable.

    }

    private class LoadImage extends AsyncTask<Integer, Integer, Integer> {

        private Context mContext;

        public LoadImage(Context context) {
            mContext = context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DisplayMultipleEventsActivity.this);
            pDialog.setMessage("Loading Events ....");
            pDialog.show();
        }
        protected Integer doInBackground(Integer... a) {
            getData();
            return 0;
        }
        protected void onPostExecute(Integer b) {
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));

            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.empty_photo)
                    .showImageForEmptyUri(R.drawable.empty_photo)
                    .showImageOnFail(R.drawable.big_problem)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();

            final GridView gridview = (GridView) findViewById(R.id.gridview);
            gridview.setAdapter(new ImageAdapter());

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //System.out.println("CLICKED: "+id);

                    gridview.getAdapter().getItem(position);

                    Event clickedEvent = loadedEvents.get(position);
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable(SER_KEY,clickedEvent);

                    Intent intent = new Intent(DisplayMultipleEventsActivity.this, DisplaySingleEventActivity.class);
                    intent.putExtras(mBundle);
                    startActivity(intent);
                }
            });

            pDialog.dismiss();
        }
    }

    public void getData(){
        String result = "";
        InputStream isr = null;
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(DATABASE); //YOUR PHP SCRIPT ADDRESS

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            isr = entity.getContent();
        }
        catch(Exception e){
            Log.e("log_tag", "Error in http connection " + e.toString());
        }
        //convert response to string
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


        //parse json data
        try {
            JSONArray jArray = new JSONArray(result);

            for(int i=0; i<jArray.length();i++){
                JSONObject json = jArray.getJSONObject(i);
                Event fetchedEvent = parseEvent(json);
                loadedEvents.add(fetchedEvent);
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("log_tag", "Error Parsing Data "+e.toString());
        }

    }

    static class ViewHolder {
        ImageView imageView;
    }

    //    our custom adapter
    private class ImageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return imagePaths.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView,
                            ViewGroup parent) {
            View view = convertView;
            final ViewHolder gridViewImageHolder;
            TextView name;
//            check to see if we have a view
            if (convertView == null) {
//                no view - so create a new one
                view = getLayoutInflater().inflate(R.layout.item_grid_image, parent, false);
                gridViewImageHolder = new ViewHolder();
                gridViewImageHolder.imageView = (ImageView) view.findViewById(R.id.image);
                gridViewImageHolder.imageView.setMaxHeight(80);
                gridViewImageHolder.imageView.setMaxWidth(80);
                view.setTag(gridViewImageHolder);


            } else {
//                we've got a view
                gridViewImageHolder = (ViewHolder) view.getTag();
            }


            imageLoader.displayImage(IMAGEBASE + imagePaths.get(position)

                    , gridViewImageHolder.imageView
                    , options);

            return view;
        }
    }

    public Event parseEvent(JSONObject json){
        try {

            String image = json.getString("Image");
            imagePaths.add(image);

            String name = json.getString("Name");
            String date = json.getString("Date");
            String time = json.getString("Time");
            String location = json.getString("Location");
            String description = json.getString("Description");

            return new Event(image,name,date,time,location,description);
        }
        catch (Exception e) {
            // TODO: handle exception
            Log.e("log_tag", "Error Parsing Data "+e.toString());
        }

        return new Event("no_image","Name","Date","Time","Location","Description");
    }
}