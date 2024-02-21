package com.example.flickrbrowser;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

class FlickerRecyclerViewAdapter extends RecyclerView.Adapter<FlickerRecyclerViewAdapter.FlickerImageViewHolder> {


    private static final String TAG = "FlickerRecyclerViewAdap";
    private List<Photo>mPhotoList;
    private Context mContext;

    public FlickerRecyclerViewAdapter(List<Photo> photoList, Context context) {
        this.mPhotoList = photoList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public FlickerImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.browse,parent,false);
        return new FlickerImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlickerImageViewHolder holder, int position) {

        Photo  photoItem = this.mPhotoList.get(position);
        Picasso.get().load(photoItem.getImage())
                .error(R.drawable.placeholder) // If there is any error
                .placeholder(R.drawable.placeholder) // If no image has been assigned
                .into(holder.thumbnail);

        holder.title.setText(photoItem.getTitle());

    }

    @Override
    public int getItemCount() {
        return (this.mPhotoList!=null && this.mPhotoList.size()!=0) ? this.mPhotoList.size() : 0;
    }


    void loadNewData(List<Photo> newPhotos){
        this.mPhotoList = newPhotos;
        notifyDataSetChanged();
    }

    public Photo getPhoto(int position){
        return (this.mPhotoList!=null && this.mPhotoList.size()!=0)?this.mPhotoList.get(position): null ;
    }

    static class FlickerImageViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "FlickerImageViewHolder";
        private ImageView thumbnail=null;
        private TextView title = null;


        public FlickerImageViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "FlickerImageViewHolder: called");
            this.thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            this.title = (TextView) itemView.findViewById(R.id.thumbnail_title);

        }
    }
}
