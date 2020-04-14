package com.hrishi3331studio.template_informal.Notifications;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hrishi3331studio.template_informal.R;

public class Notifications extends AppCompatActivity {

    private DatabaseReference mRef;
    private RecyclerView notificationsView;
    private LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        notificationsView = (RecyclerView)findViewById(R.id.notifications_view);
        mRef = FirebaseDatabase.getInstance().getReference().child("notifications");
        manager = new LinearLayoutManager(Notifications.this, LinearLayoutManager.VERTICAL, false);
        notificationsView.setLayoutManager(manager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Notification, NotificationViewHolder> adapter = new FirebaseRecyclerAdapter<Notification, NotificationViewHolder>(Notification.class, R.layout.notification_layout, NotificationViewHolder.class, mRef) {
            @Override
            protected void populateViewHolder(NotificationViewHolder viewHolder, Notification model, int position) {
                viewHolder.setDate(model.getDate());
                viewHolder.setTitle(model.getSubject());
                viewHolder.setContent(model.getContent());
            }
        };
        notificationsView.setAdapter(adapter);
    }

    public void goBack(View view){
        finish();
    }
}
