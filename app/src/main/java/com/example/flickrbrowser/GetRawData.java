package com.example.flickrbrowser;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

enum DownloadStatus{IDLE,PROCESSING,NOT_INITIALIZED, FAILED_OR_EMPTY,OK}
class GetRawData extends AsyncTask<String,Void,String> {

    private static final String TAG = "GetRawData";
    private DownloadStatus mDowloadStatus;
    private final DownloadComplete mCallBack;

    public GetRawData(DownloadComplete mcallBack) {

        this.mDowloadStatus = DownloadStatus.IDLE;
        this.mCallBack = mcallBack;
    }

    @Override
    protected String doInBackground(String... strings) {

        Log.d(TAG, "doInBackground: Background Thread = "+ Thread.currentThread().getName());

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        if(strings == null){
            this.mDowloadStatus = DownloadStatus.FAILED_OR_EMPTY;
            return null;
        }
        try {
            mDowloadStatus = DownloadStatus.PROCESSING;
            URL url = new URL(strings[0]);
            connection = (HttpsURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int response = connection.getResponseCode();
            Log.d(TAG, "doInBackground: Response code was: "+response);

            StringBuilder result = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while(null!= (line=reader.readLine())){
                result.append(line).append("\n");
            }

            mDowloadStatus= DownloadStatus.OK;
            return result.toString();


        }catch (MalformedURLException e){
            Log.e(TAG, "doInBackground: URL malformed exception"+ e.getMessage());
        }
        catch (IOException e){
            Log.e(TAG, "doInBackground: IO Exception"+e.getMessage() );
        }
        catch (SecurityException e){
            Log.e(TAG, "doInBackground: Security Exception"+e.getMessage() );
        }
        finally {
            if(connection!=null){
                connection.disconnect();
            }
            try{
                if(reader!=null){
                    reader.close();
                }
            }
            catch (IOException e){
                Log.d(TAG, "doInBackground: Error closing stream "+e.getMessage());
            }
            mDowloadStatus= DownloadStatus.OK;

        }

        return null;
    }

    void runInSameThread(String s){
        Log.d(TAG, "runInSameThread: starts");
        if(mCallBack!=null){

            String result = doInBackground(s);
            mCallBack.onDownloadComplete(result,mDowloadStatus);
        }
        onPostExecute(doInBackground(s));
        Log.d(TAG, "runInSameThread: ends");
    }
    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: Parameter = "+s);
        if(this.mCallBack!=null){
            mCallBack.onDownloadComplete(s,DownloadStatus.OK);
        }else{
            mCallBack.onDownloadComplete(s,DownloadStatus.FAILED_OR_EMPTY);

        }
    }

    interface DownloadComplete{
        void onDownloadComplete(String response, DownloadStatus status);
    }


}
