package com.hrishi3331studio.template_informal.Notifications;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.hrishi3331studio.template_informal.R;

public class NotificationViewHolder extends RecyclerView.ViewHolder {

    private View mView;
    private TextView dateView;
    private TextView titleView;
    private TextView contentView;

    public NotificationViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        dateView = (TextView)mView.findViewById(R.id.notification_date);
        titleView = (TextView)mView.findViewById(R.id.notification_title);
        contentView = (TextView)mView.findViewById(R.id.notification_content);
    }

    public void setDate(String date){
        dateView.setText(date);
    }

    public void setTitle(String title){
        titleView.setText(title);
    }

    public void setContent(String content){
        contentView.setText(content);
    }
}
