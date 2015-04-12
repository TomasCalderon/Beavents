package com.example.tomas.beavents;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by LLP-student on 4/5/2015.
 */
public class DisplayMultipleEventsActivity extends BaseActivity {
    public  final static String SER_KEY = "com.example.tomas.beavents.events";
    public final static String DATABASE = "http://beavents.net84.net/get_some_images.php";
    public final static String IMAGEBASE = "http://beavents.net84.net/images/";


    private List<String> imagePaths = new ArrayList<>();
    private String eventsToDisplay;
    DisplayImageOptions options;
    private ImageLoader imageLoader;
    private List<Event> loadedEvents = new ArrayList<>();
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BeaventsApplication) getApplication()).getTracker()
                .trackScreenView("/display_multiple_events", "Suggested Events");

        setContentView(R.layout.activity_main);
            super.onCreateDrawer();

        StrictMode.enableDefaults(); //STRICT MODE ENABLED

        eventsToDisplay = (String) getIntent().getExtras().getString("eventsToDisplay");

        if(eventsToDisplay.equals("ALL")){
            this.setTitle("All Events");
        }

        new LoadImage(this).execute(0);

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
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            String queryString = createQueryString();
            params.add(new BasicNameValuePair("Query", queryString));
            getData(params);
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

    private String createQueryString() {
        if(eventsToDisplay.equals("INTERESTS")){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            Gson gson = new Gson();
            String interests=prefs.getString("user_interests","none");

            if(interests.equals("[]")){
                return "SELECT * FROM Events WHERE CourseNum1 = 25";
            }else {
                String[] interests_list = gson.fromJson(interests, String[].class);
                List<String> all_interests = new ArrayList(Arrays.asList(interests_list));
                String categories = getCategoriesQueryString(all_interests);
                String courseNums = getCourseNumberQueryString(all_interests);
                Log.e("please",courseNums);
                String queryString = "SELECT * FROM Events WHERE Category1 in " + categories+ " or Category2 in "+categories
                        + " or Category3 in " + categories + " or CourseNum1 in " + courseNums + " or CourseNum2 in " + courseNums ;
                Log.e("please",queryString);
                return queryString ;
            }
        }else if(eventsToDisplay.contains("number")){
            String courseNumber = eventsToDisplay.substring(6);
            String a = "SELECT * FROM Events WHERE CourseNum1 =" + courseNumber+ " or CourseNum2 ="+courseNumber;
            return a;
        }else if(eventsToDisplay.equals("ALL")){
            return "SELECT * FROM Events WHERE CourseNum1 NOT in (25)";
        }
        else{
            eventsToDisplay = "'"+eventsToDisplay+"'";
            String a = "SELECT * FROM Events WHERE Category1 =" + eventsToDisplay+ " or Category2 ="+eventsToDisplay
                    + " or Category3 = " + eventsToDisplay;
            return a;
        }

    }

    private String getCategoriesQueryString(List<String> all_interests) {
        String categories = "(";
        int counter = 0;
        for (int i = 0; i < all_interests.size(); i++) {
            String currentInterest = all_interests.get(i);
            if(!currentInterest.contains("Course")){
                if (counter == 0) {
                    categories += "'" +currentInterest + "'";
                    counter +=1;
                } else {
                    categories += ",'" + currentInterest + "'";
                }
            }
        }
        if(categories.equals("(")){
            //To avoid errors, we return a nonempty query.
            return "('b')";
        }
        return categories+")";
    }

    private String getCourseNumberQueryString(List<String> all_interests) {
        String courseNums = "(";
        int counter = 0;
        for (int i = 0; i < all_interests.size(); i++) {
            String currentInterest = all_interests.get(i);
            if(currentInterest.contains("Course")){
                if (counter == 0) {
                    courseNums += currentInterest.split(" ")[1];
                    counter +=1;
                } else {
                    courseNums += ","+currentInterest.split(" ")[1] ;
                }
            }
        }
        if(courseNums.equals("(")){
            //To avoid errors, we return a nonempty query.
            return "(30)";
        }
        return courseNums+")";
    }

    public void getData(List<NameValuePair> params){
        String result = "";
        InputStream isr = null;
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(DATABASE); //YOUR PHP SCRIPT ADDRESS

            httppost.setEntity(new UrlEncodedFormEntity(params));

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

                //Integer[] date = DisplaySingleEventActivity.convertEventDate(fetchedEvent.getDate());
                Integer[] time = DisplaySingleEventActivity.convertEventTime(fetchedEvent.getTime());

                Date currentDate = new Date();
                boolean eventOccursAfter=false;
                if(fetchedEvent.getDate().length()==0)eventOccursAfter=true;
                else if(time.length==0){ //All day
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    Date d = sdf.parse(fetchedEvent.getDate());
                    if(d.after(currentDate))eventOccursAfter=true;
                }
                else{
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                    Date d = sdf.parse(fetchedEvent.getDate()+" "+String.format("%02d:%02d",time[0],time[1]));
                    if(d.after(currentDate))eventOccursAfter=true;
                }

                if(eventOccursAfter){
                    loadedEvents.add(fetchedEvent);
                    imagePaths.add(fetchedEvent.getImage());
                }

                //System.out.println(eventOccursAfter);
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
                view.setTag(R.id.text, view.findViewById(R.id.text));


            } else {
//                we've got a view
                gridViewImageHolder = (ViewHolder) view.getTag();

            }


            imageLoader.displayImage(IMAGEBASE + imagePaths.get(position)
                    , gridViewImageHolder.imageView
                    , options);
            name = (TextView) view.getTag(R.id.text);
            name.setText(loadedEvents.get(position).getName()+"\n"+ loadedEvents.get(position).getDate() + "\n"+ loadedEvents.get(position).getTime());

            return view;
        }
    }

    public Event parseEvent(JSONObject json){
        try {

            String image = json.getString("Image");

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
