package com.example.flickrbrowser;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class GetFlickrJsonData extends AsyncTask<String,Void,List<Photo>> implements GetRawData.DownloadComplete {

    private static final String TAG = "GetFlickrJsonData";
    private List<Photo>mPhotoList = null;
    private String mBaseUrl;
    private String mLanguage;
    private boolean mMatchAll;
    private final OnDataAvailable mCallback;
    private boolean runningOnSameThread = false; // The false condition mean GetFlickrJsonData and MainActivity both are running on same threads.
    public GetFlickrJsonData(OnDataAvailable callback, String baseUrl, String language, boolean matchAll) {
        Log.d(TAG, "GetFlickrJsonData: Constructor called");
        this.mBaseUrl = baseUrl;
        this.mLanguage = language;
        this.mMatchAll = matchAll;
        this.mCallback = callback;
    }

    @Override
    protected void onPostExecute(List<Photo> photos) {

        Log.d(TAG, "onPostExecute: starts");

        if(mCallback!=null){
            runningOnSameThread = false;
            mCallback.onDataAvailable(mPhotoList,DownloadStatus.OK);
        }
        else{
            mCallback.onDataAvailable(null,DownloadStatus.FAILED_OR_EMPTY);
        }
        Log.d(TAG, "onPostExecute: ends");

    }

    @Override
    protected List<Photo> doInBackground(String... params) {
        Log.d(TAG, "doInBackground: start");
        String destinationUri = createUrl(params[0],mLanguage,mMatchAll);
        GetRawData getRawData = new GetRawData(this);
        getRawData.runInSameThread(destinationUri);
        Log.d(TAG, "doInBackground: ends");
        return mPhotoList;
    }

    @Override
    public void onDownloadComplete(String data, DownloadStatus status) { // Execution of this function mean Both this class and GetRawData are running in background.
        Log.d(TAG, "onDownloadComplete: status = "+status);

        if(status == DownloadStatus.OK){
            mPhotoList = new ArrayList<>();
            try{
                JSONObject jsonData = new JSONObject(data);
                JSONArray itemsArray = jsonData.getJSONArray("items");

                for(int i=0;i<itemsArray.length();i++){
                    JSONObject image = itemsArray.getJSONObject(i);
                    String image_title = image.getString("title");
                    String image_author = image.getString("author");
                    String image_author_id = image.getString("author_id");
                    String image_tags = image.getString("tags");
                    String photoUrl =image.getJSONObject("media").getString("m");
                    String image_link = photoUrl.replaceFirst("_m.","_b.");

                    Photo photoObject = new Photo(image_title,image_author,image_author_id,image_link,image_tags,photoUrl);
                    mPhotoList.add(photoObject);
                    Log.d(TAG, "onDownloadComplete: Complete"+ photoObject.toString());
                }
            }catch (JSONException e){
                e.printStackTrace();
                Log.d(TAG, "onDownloadComplete: Error Processing JSON data"+ e.getMessage());
                status= DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        if(mCallback!=null && runningOnSameThread){
            mCallback.onDataAvailable(mPhotoList,status);
        }
        Log.d(TAG, "onDownloadComplete: Ends");
    }


    //Execute this class on the main thread and
    public void executeOnSameThread(String searchCriteria){   // Execution of this function means both main activity and this class are running on the same thread.
        Log.d(TAG, "executeOnSameThread: starts");       // and Get raw data is running on different thread.
        runningOnSameThread = true;
        String destinationUrl = createUrl(searchCriteria,mLanguage,mMatchAll);

        GetRawData getRawData = new GetRawData(this);
        getRawData.execute(destinationUrl);
        Log.d(TAG, "executeOnSameThread: ends");

    }
    interface OnDataAvailable{

        void onDataAvailable(List<Photo> data, DownloadStatus status);
    }
    private String createUrl(String searchCriteria, String lang, boolean matchAll ){
        Log.d(TAG, "createUrl: starts");

        return Uri.parse(mBaseUrl).buildUpon()
                        .appendQueryParameter("tags",searchCriteria)
                            .appendQueryParameter("tagmode",matchAll?"All":"Any")
                                .appendQueryParameter("format","json")
                                        .appendQueryParameter("nojsoncallback","1")
                                                .build().toString();


    }}
