package com.example.tomas.beavents;

import android.app.Application;

import org.piwik.sdk.PiwikApplication;

/**
 * Created by Kathy on 4/11/2015.
 */
public class BeaventsApplication extends PiwikApplication {
    public BeaventsApplication(){
        super();
    }

    @Override
    public String getTrackerUrl() {
        return "http://beavents.piwik.pro/piwik.php";
    }


    @Override
    public String getAuthToken() {
        return null;
    }

    @Override
    public Integer getSiteId() {
        return 3;
    }

}
