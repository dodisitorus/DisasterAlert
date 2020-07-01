package com.dodi.disasteralert;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.dodi.disasteralert.adapters.VisualRecognitionAdapter;
import com.dodi.disasteralert.models.VisualRecognitionModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EndActivity extends AppCompatActivity {

    private List<VisualRecognitionModel> visualRecognitionModelList = new ArrayList<>();
    private VisualRecognitionAdapter mAdapter;

    private RecyclerView recyclerView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        recyclerView = findViewById(R.id.list_item);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        toolbar.setTitle(getString(R.string.title_visual_result));

        mAdapter = new VisualRecognitionAdapter(this, visualRecognitionModelList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        updateList();

        getSetDataList();
    }

    private void updateList() {
        if (visualRecognitionModelList != null) {
            mAdapter.updateAdapter(visualRecognitionModelList);
        }
    }

    private void getSetDataList() {
        List<VisualRecognitionModel> list = (List<VisualRecognitionModel>) getIntent().getSerializableExtra("visualResult");
        Log.d("IntentResult", list.get(0).getClassName());
        visualRecognitionModelList = list;
        updateList();
    }
}
