package com.dodi.disasteralert.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dodi.disasteralert.R;
import com.dodi.disasteralert.models.Message;

import java.util.ArrayList;


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int SELF = 100;
    private ArrayList<Message> messageArrayList;

    public ChatAdapter(ArrayList<Message> messageArrayList) {
        this.messageArrayList = messageArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        // view type is to identify where to render the chat message
        // left or right
        if (viewType == SELF) {
            // self message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_self, parent, false);
        } else {
            // WatBot message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_watson, parent, false);
        }

        return new ViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        if (message.getId() != null && message.getId().equals("1")) return SELF;

        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        Message message = messageArrayList.get(position);
        String timeToDisplay = message.getTime();

        switch (message.getType()) {
            case TEXT:
                ((ViewHolder) holder).message.setText(message.getMessage());
                ((ViewHolder) holder).time.setText(timeToDisplay);
                break;
            case IMAGE:
                ((ViewHolder) holder).message.setVisibility(View.GONE);
                ((ViewHolder) holder).time.setText(timeToDisplay);
                ImageView iv = ((ViewHolder) holder).image;
                Glide
                        .with(iv.getContext())
                        .load(message.getUrl())
                        .into(iv);
        }
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        ImageView image;
        TextView time;

        ViewHolder(View view) {
            super(view);
            message = itemView.findViewById(R.id.message);
            image = itemView.findViewById(R.id.image);
            time = itemView.findViewById(R.id.text_message_time);

            //TODO: Uncomment this if you want to use a custom Font
            /*String customFont = "Montserrat-Regular.ttf";
            Typeface typeface = Typeface.createFromAsset(itemView.getContext().getAssets(), customFont);
            message.setTypeface(typeface);*/

        }
    }
}