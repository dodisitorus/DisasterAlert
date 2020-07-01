package com.dodi.disasteralert.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dodi.disasteralert.R;
import com.dodi.disasteralert.models.VisualRecognitionModel;

import java.util.ArrayList;
import java.util.List;

public class VisualRecognitionAdapter extends RecyclerView.Adapter<VisualRecognitionHolder> {

    private List<VisualRecognitionModel> visualRecognitionModelList;
    private Context context;

    public VisualRecognitionAdapter(Context context, List<VisualRecognitionModel> visualRecognitionModels) {
        this.visualRecognitionModelList = visualRecognitionModels;
        this.context = context;
    }

    @NonNull
    @Override
    public VisualRecognitionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_visual_result, parent, false);
        return new VisualRecognitionHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VisualRecognitionHolder holder, int position) {
        holder.bindContent(visualRecognitionModelList.get(position), context);
    }

    @Override
    public int getItemCount() {
        return visualRecognitionModelList.size();
    }

    public void updateAdapter(List<VisualRecognitionModel> list) {
        this.visualRecognitionModelList = new ArrayList<>();
        this.visualRecognitionModelList = list;
        notifyDataSetChanged();
    }
}
