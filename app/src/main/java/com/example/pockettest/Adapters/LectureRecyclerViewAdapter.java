package com.example.pockettest.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pockettest.Activites.YoutubeVideoPlayer;
import com.example.pockettest.Model.VideoDetails;
import com.example.pockettest.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;
import java.util.zip.Inflater;

public class LectureRecyclerViewAdapter extends RecyclerView.Adapter<LectureRecyclerViewAdapter.ViewHolder>{

    private List<VideoDetails> videoList;
    private Context ctx;

    public LectureRecyclerViewAdapter(List<VideoDetails> itemList, Context ctx) {
        this.videoList = itemList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public LectureRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list, parent, false);
        return new ViewHolder(view, ctx);
    }

    @Override
    public void onBindViewHolder(@NonNull LectureRecyclerViewAdapter.ViewHolder holder, int position) {
        VideoDetails videoDetails = videoList.get(position);
        holder.title.setText(videoDetails.getTitle());
        Picasso.get()
                .load(videoDetails.getThumbnailURL())
                .placeholder(R.drawable.play)
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title;
        private ImageView thumbnail;
        private Context context;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            title = itemView.findViewById(R.id.videoTitle);
            thumbnail = itemView.findViewById(R.id.thumbnailId);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            VideoDetails videoDetails = videoList.get(getAdapterPosition());
            Intent intent = new Intent(context, YoutubeVideoPlayer.class);
            intent.putExtra("videoId", videoDetails.getVideoId());
            context.startActivity(intent);

        }
    }
}