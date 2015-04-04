package com.example.tomas.beavents;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by LLP-student on 4/3/2015.
 */


public class Event implements Serializable{
    private static final long serialVersionUID = -7060210544600464481L;

    String image;
    String name;
    String time;
    String location;
    String[] categories;
    String description;
    public Event(String image){
        this.image = image;
    }
    public Event(String image, String name, String time, String location,
                 String[] categories, String description){
        this.image=image;
        this.name=name;
        this.time=time;
        this.location=location;
        this.categories=categories;
        this.description=description;
    }

    public String getImage(){
        return this.image;
    }

    public String getName(){
        return this.name;
    }
    public String getTime(){
        return this.time;
    }

    public String getLocation(){
        return this.location;
    }

    public String[] getCategories(){
        return this.categories;
    }

    public String getDescription(){
        return this.description;
    }

}

