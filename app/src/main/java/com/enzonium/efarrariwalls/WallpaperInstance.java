/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.enzonium.efarrariwalls;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;

import static com.enzonium.efarrariwalls.NetworkAbstractionLayer.BASE_URL;

/**
 * Represents an Item in our application. Each item has a name, id, full size image url and
 * thumbnail url.
 */
public class WallpaperInstance extends Searchable11 {

    static ArrayList<WallpaperInstance> activeData = new ArrayList<>();

    static final String TAG = "WallpaperInstance";

    private String pack, name, date, sensor, url, urlthumb, location;
    private String id;
    private Bitmap thumb;
    private BitmapDrawable fullImage;
    private String searchString;


    public WallpaperInstance(String pack, String name, String location, String date, String sensor, String url, String urlthumb, String id) {
        this.pack = pack;
        this.name = name;
        this.date = date;
        this.sensor = sensor;
        this.url = url;
        this.urlthumb = urlthumb;
        this.id = id;
        this.location = location;
        generateSearchString();
    }

    private void generateSearchString() {
        searchString = pack +" "+ name +" "+ date +" "+ sensor +" "+ location;
    }

    public String getSearchString() {
        return searchString;
    }

    public static WallpaperInstance findById(int searchId){
        return findById(searchId, activeData);
    }

    public static WallpaperInstance findById(int searchId, ArrayList<WallpaperInstance> source){

        for(WallpaperInstance wp11: source){
            if(wp11.getId() == searchId)
                return wp11;
        }

        return null;

    }


    public int getId() {
        return name.hashCode() ;
    }

    public static int getGlobalPosition(WallpaperInstance comparo){
        for(int i = 0; i<activeData.size(); i++) {
            if (activeData.get(i).equals(comparo))
                return i;
        }
                return 0;

        }


    public static ArrayList<WallpaperInstance> getPack(String pack) {

        ArrayList<WallpaperInstance> toReturn = new ArrayList<>();

        for (WallpaperInstance e : activeData) {
            if (e.getPackName().equalsIgnoreCase(pack))
                toReturn.add(e);
        }
        return toReturn;
    }

    public int getCount(String pack){
        int counter = 0;
        for(WallpaperInstance e:activeData ){
           if(e.getPackName().equalsIgnoreCase(pack)) counter++;
        }
        return counter;}


    public String getName() {
        return name;
    }

    public String getPackName(){return pack;}

    public String getSensor(){ return sensor;}

    public String getDate() {return date;}

    public String getLocation() {return location;}

    public String getUrlthumb(){return BASE_URL + urlthumb;}

    public String getThumbnailUrl(){return BASE_URL + urlthumb;}

    public Bitmap getThumb(){return thumb;}

    public void setThumb(Bitmap newThumb){
        thumb = newThumb;
    }

    public void setImage(BitmapDrawable fullImage){
        Log.e(TAG, "fullImage Set for " + name);
        this.fullImage = fullImage;
    }

    public BitmapDrawable getImage() {
        return this.fullImage;
    }

    public String getImageUrl(){ return  BASE_URL + url;}

}
