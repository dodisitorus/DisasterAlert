package com.dodi.disasteralert.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dodi.disasteralert.R;
import com.dodi.disasteralert.models.Notification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Notification> notificationArrayList;
    private Context context;

    public NotificationAdapter(List<Notification> notifications, Context ctx) {
        this.notificationArrayList = notifications;
        this.context = ctx;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);

        return new ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Notification notification = notificationArrayList.get(position);
        // create time to display
        long myLong = (long) (notification.getTimestamp());
        @SuppressLint("SimpleDateFormat")
        String timeToDisplay = new SimpleDateFormat("MMMM dd, yyyy | HH:mm").format(new Date(myLong));

        ((ViewHolder) holder).title.setText(notification.getObject());
        ((ViewHolder) holder).confidence.setText("Confident : " + notification.getScore());
        ((ViewHolder) holder).time.setText(timeToDisplay);
        if (notification.getImage().equals("-")) {
            ((ViewHolder) holder).card_view_image.setVisibility(View.GONE);
        } else {
            Glide.with(context)
                    .load(notification.getImage())
                    .into(((ViewHolder) holder).imageView);

        }
    }

    @Override
    public int getItemCount() {
        return notificationArrayList.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView confidence;
        TextView time;
        ImageView imageView;
        CardView card_view_image;

        ViewHolder(View view) {
            super(view);
            title = itemView.findViewById(R.id.title);
            confidence = itemView.findViewById(R.id.score);
            time = itemView.findViewById(R.id.time);
            imageView = itemView.findViewById(R.id.image_recognize);
            card_view_image = itemView.findViewById(R.id.card_view_image);
        }
    }

    public void updateAdapter(List<Notification> notifications){
        this.notificationArrayList = new ArrayList<>();
        this.notificationArrayList = notifications;

        notifyDataSetChanged();
    }

}
