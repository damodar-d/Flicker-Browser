package com.example.flickrbrowser;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flickrbrowser.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GetFlickrJsonData.OnDataAvailable, RecyclerItemClickListener.OnRecyclerClickListener {

    private static final String TAG = "MainActivity";
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private FlickerRecyclerViewAdapter mFlickerRecyclerViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Main Thread = "+ Thread.currentThread().getName());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        mFlickerRecyclerViewAdapter = new FlickerRecyclerViewAdapter( new ArrayList<Photo>(),this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,recyclerView,this));
        recyclerView.setAdapter(mFlickerRecyclerViewAdapter);


        binding.fab.setOnClickListener(view ->
        {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.fab)
                .setAction("Action", null).show();
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDataAvailable(List<Photo> data, DownloadStatus status) {

        Log.d(TAG, "onDataAvailable: start");
        if(status==DownloadStatus.OK){
            mFlickerRecyclerViewAdapter.loadNewData(data);
        }
        else{
            Log.d(TAG, "onDataAvailable: Failed to fetch data: "+status);
        }
        Log.d(TAG, "onDataAvailable: ends");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: starts");
        super.onResume();
        GetFlickrJsonData getFlickrJsonData = new GetFlickrJsonData(this,
                "https://www.flickr.com/services/feeds/photos_public.gne"
        ,"en-us", true);
        getFlickrJsonData.executeOnSameThread("android lollipop ");
        Log.d(TAG, "onResume: ends");
    }

    @Override
    public void onItemClick(View view, int position) {

        Log.d(TAG, "onItemClick: starts");
        Toast.makeText(this,"simple tap at position: "+ position,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemLongClick(View view, int position) {

        Log.d(TAG, "onItemLongClick: starts");
        Toast.makeText(this,"long tap at position: "+ position,Toast.LENGTH_LONG).show();
    }
}