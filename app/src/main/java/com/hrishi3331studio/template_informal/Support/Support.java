package com.hrishi3331studio.template_informal.Support;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hrishi3331studio.template_informal.R;

public class Support extends AppCompatActivity {

    private EditText message;
    private RecyclerView messageView;
    private DatabaseReference href;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        message = (EditText)findViewById(R.id.message_text);
        messageView = (RecyclerView)findViewById(R.id.chat_messages);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        href = FirebaseDatabase.getInstance().getReference().child("support").child(mUser.getUid());

        LinearLayoutManager manager = new LinearLayoutManager(Support.this, LinearLayoutManager.VERTICAL, false);
        manager.setStackFromEnd(true);
        messageView.setLayoutManager(manager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Message, MessageViewHolder> adapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(Message.class, R.layout.message_layout, MessageViewHolder.class, href) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message model, int position) {
                viewHolder.showMessage(model.getContent(), model.getSender());
            }
        };
        messageView.setAdapter(adapter);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        View messageView;
        TextView messageText;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageView = itemView;
            messageText = (TextView)messageView.findViewById(R.id.message_box);
        }

        public void showMessage(String content, String sender){
            messageText.setText(content);
            if (sender.equals("user")) {
                messageView.setBackgroundResource(R.drawable.mymessagebackground);
                messageText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            }
            else
            {
                messageView.setBackgroundResource(R.drawable.adminmessage);
                messageText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            }
        }
    }

    public void sendMessage(View view){
        String message = this.message.getText().toString();
        View mview = this.getCurrentFocus();
        if (mview != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if (message.length() > 0){
            DatabaseReference mref = href.push();
            mref.child("sender").setValue("user");
            mref.child("content").setValue(message);


            this.message.setText("");
        }

    }
}

