package com.dodi.disasteralert.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dodi.disasteralert.R;
import com.dodi.disasteralert.adapters.NotificationAdapter;
import com.dodi.disasteralert.models.Notification;
import com.dodi.disasteralert.utils.UserPreferences;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class NotificationFragment extends Fragment {

    private List<Notification> notificationArrayList = new ArrayList<>();
    private NotificationAdapter mAdapter;
    private RecyclerView recyclerView;
    private ConstraintLayout empty_layout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout shimmerFrameLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = view.findViewById(R.id.list_item);
        empty_layout = view.findViewById(R.id.empty_layout);
        swipeRefreshLayout = view.findViewById(R.id.swipe_list);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);

        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);

        // recycle view init
        mAdapter = new NotificationAdapter(notificationArrayList, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                updateList();
            }
        });

        // get notification list
        getListNotification();
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            List<Notification> notifications = new ArrayList<>();
            for (DataSnapshot notifySnapshot: dataSnapshot.getChildren()) {
                Notification notification = notifySnapshot.getValue(Notification.class);
                notifications.add(notification);
            }
            notificationArrayList = notifications;
            updateList();
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w("DatabaseError", "Failed to read value.", error.toException());
        }
    };

    private void getListNotification() {
        String uid = UserPreferences.getData(UserPreferences.userIdKey, getContext());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("list-fire").child(uid);
        Query query = myRef.orderByChild("reversetime");
        query.addValueEventListener(valueEventListener);
    }

    private void updateList() {
        swipeRefreshLayout.setRefreshing(false);
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        if (notificationArrayList != null) {
            mAdapter.updateAdapter(notificationArrayList);
            if (notificationArrayList.size() == 0) {
                empty_layout.setVisibility(View.VISIBLE);
            } else {
                empty_layout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
