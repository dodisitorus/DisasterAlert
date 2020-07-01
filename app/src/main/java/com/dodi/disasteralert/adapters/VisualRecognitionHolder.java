package com.dodi.disasteralert.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dodi.disasteralert.R;
import com.dodi.disasteralert.models.VisualRecognitionModel;

class VisualRecognitionHolder extends RecyclerView.ViewHolder  {
    private Context context;
    private TextView name;

    VisualRecognitionHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
    }

    @SuppressLint("SetTextI18n")
    void bindContent(VisualRecognitionModel visualRecognitionModel, Context context) {
        this.context = context;
        String text = "Class : " + visualRecognitionModel.getClassName() + " \n "
                + "Type Hierarchy : " + visualRecognitionModel.getTypeHierarchy() + " \n "
                + "Score : " + visualRecognitionModel.getScore();
        name.setText(text);
    }
}
