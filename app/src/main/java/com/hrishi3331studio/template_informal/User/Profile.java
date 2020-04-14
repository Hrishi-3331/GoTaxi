package com.hrishi3331studio.template_informal.User;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hrishi3331studio.template_informal.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity {

    private TextView profile_name;
    private TextView profile_email;
    private TextView profile_contact;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference mref;
    private RoundedImageView prof_image;
    private ProgressDialog wait_dialog;
    private FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile_email = (TextView)findViewById(R.id.profile_email);
        profile_name = (TextView)findViewById(R.id.profile_name1);
        prof_image = (RoundedImageView) findViewById(R.id.profile_image1);
        profile_contact = (TextView)findViewById(R.id.profile_contact);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        mref = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        mref.child("contact").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    profile_contact.setText(dataSnapshot.getValue().toString().trim());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        assert user != null;
        profile_name.setText(user.getDisplayName());
        profile_email.setText(user.getEmail());

        try {
            Picasso.get().load(user.getPhotoUrl()).into(prof_image);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        wait_dialog = new ProgressDialog(Profile.this);
        wait_dialog.setTitle("Updating Profile");
        wait_dialog.setMessage("Please wait..");
        wait_dialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public void Back(View view){
        finish();
    }

    public void ChangeName(View view){
        Dialog request_dialog = CreateDialog(0);
        request_dialog.show();
    }

    public void ChangeMobile(View view){
        Dialog request_dialog = CreateDialog(2);
        request_dialog.show();
    }

    public AlertDialog CreateDialog(final int code){
        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
        LayoutInflater inflater = getLayoutInflater();
        View DialogLayout = inflater.inflate(R.layout.name_change_dialog, null);
        builder.setView(DialogLayout);

        Button ok = (Button) DialogLayout.findViewById(R.id.btn_ok);
        Button cancel = (Button) DialogLayout.findViewById(R.id.btn_cancel);
        final EditText edtxt = (EditText)DialogLayout.findViewById(R.id.dialog_editext);
        final TextView title = (TextView)DialogLayout.findViewById(R.id.dialog_title);
        final TextView message = (TextView)DialogLayout.findViewById(R.id.dialog_message);

        final AlertDialog dialog = builder.create();

        switch (code){
            case 0:
                edtxt.setHint("Username");
                title.setText("Change Username");
                message.setText("Enter new username below");
                break;

            case 1:
                edtxt.setHint("Enrollment number (BTXX)");
                title.setText("Change Enrollment Number");
                message.setText("Enter new enrollment number below");
                break;

            case 2:
                edtxt.setHint("Mobile number");
                title.setText("Change Mobile Number");
                message.setText("Enter new mobile number below");
                break;

        }

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtxt.getText().toString().trim().isEmpty()){
                    wait_dialog.show();
                    UserProfileChangeRequest request;
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(mUser.getUid());
                    switch (code){
                        case 0:
                            request = new UserProfileChangeRequest.Builder().setDisplayName(edtxt.getText().toString().trim()).build();
                            mUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    wait_dialog.dismiss();
                                    if (task.isSuccessful()){
                                        profile_name.setText(edtxt.getText().toString().trim());
                                        Toast.makeText(Profile.this, "Username updated successfully ", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(Profile.this, "Error in updating username. Try again later!", Toast.LENGTH_SHORT).show();
                                    }
                                    dialog.dismiss();
                                }
                            });
                            reference.child("name").setValue(edtxt.getText().toString().trim());
                            break;

                        case 2:

                            reference.child("contact").setValue(edtxt.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    wait_dialog.dismiss();
                                    if (task.isSuccessful()){
                                        Toast.makeText(Profile.this, "Mobile number updated successfully ", Toast.LENGTH_SHORT).show();
                                        profile_contact.setText(edtxt.getText().toString().trim());
                                    }
                                    else {
                                        Toast.makeText(Profile.this, "Error in updating mobile number. Try again later!", Toast.LENGTH_SHORT).show();
                                    }
                                    dialog.dismiss();
                                }
                            });
                            break;
                    }
                }
                else {
                    Toast.makeText(Profile.this, "No data entered", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public void updateImage(Uri uri){
        UserProfileChangeRequest request = new  UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
        wait_dialog.show();
        FirebaseDatabase.getInstance().getReference().child("users").child(mUser.getUid()).child("photourl").setValue(uri.toString());
        mUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                wait_dialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(Profile.this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(Profile.this, "Profile picture update failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void ChangeImage(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
        LayoutInflater inflater = getLayoutInflater();
        View DialogLayout = inflater.inflate(R.layout.image_select_dialog, null);
        builder.setView(DialogLayout);

        ImageButton mav1 = (ImageButton)DialogLayout.findViewById(R.id.male_avtar_1);
        ImageButton mav2 = (ImageButton)DialogLayout.findViewById(R.id.male_avtar_2);
        ImageButton mav3 = (ImageButton)DialogLayout.findViewById(R.id.male_avtar_3);
        ImageButton mav4 = (ImageButton)DialogLayout.findViewById(R.id.male_avtar_4);
        ImageButton mav5 = (ImageButton)DialogLayout.findViewById(R.id.male_avtar_5);
        ImageButton mav6 = (ImageButton)DialogLayout.findViewById(R.id.male_avtar_6);
        ImageButton uploadImage = (ImageButton)DialogLayout.findViewById(R.id.upload_image_button);

        final AlertDialog Idialog = builder.create();

        mav1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prof_image.setImageResource(R.drawable.mav);
                Uri image_uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/tronix2021-e751b.appspot.com/o/avtars%2Fmav.png?alt=media&token=5a78ad75-d258-47ec-8706-159e59f5505a");
                updateImage(image_uri);
                Idialog.dismiss();
            }
        });

        mav2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prof_image.setImageResource(R.drawable.mav1);
                Uri image_uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/tronix2021-e751b.appspot.com/o/avtars%2Fmav1.png?alt=media&token=5832a175-c5a3-4e2b-921c-21a49eb54d7d");
                updateImage(image_uri);
                Idialog.dismiss();
            }
        });

        mav3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prof_image.setImageResource(R.drawable.mav2);
                Uri image_uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/tronix2021-e751b.appspot.com/o/avtars%2Fmav2.png?alt=media&token=23d48f64-8d43-480e-8222-35c28f4cd92e");
                updateImage(image_uri);
                Idialog.dismiss();
            }
        });

        mav4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prof_image.setImageResource(R.drawable.mav4);
                Uri image_uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/tronix2021-e751b.appspot.com/o/avtars%2Fmav4.png?alt=media&token=e350fc5f-38a4-4651-b2fe-64413e2c1dd4");
                updateImage(image_uri);
                Idialog.dismiss();
            }
        });

        mav5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prof_image.setImageResource(R.drawable.avfm);
                Uri image_uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/tronix2021-e751b.appspot.com/o/avtars%2Favfm.jpg?alt=media&token=981f97ce-43cd-47e8-ae38-a019d25c3af0");
                updateImage(image_uri);
                Idialog.dismiss();
            }
        });

        mav6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prof_image.setImageResource(R.drawable.mav5);
                Uri image_uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/tronix2021-e751b.appspot.com/o/avtars%2Fmav5.png?alt=media&token=b9b6dc7d-dbe2-4165-8c8b-6e971cef06cc");
                updateImage(image_uri);
                Idialog.dismiss();
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 420);
                Idialog.dismiss();
            }
        });
        Idialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 420 && resultCode == RESULT_OK){
            wait_dialog.show();
            final Uri ImageUri = data.getData();
            final StorageReference reference = FirebaseStorage.getInstance().getReference().child("userphotos").child(ImageUri.getLastPathSegment());
            reference.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
                                mUser.updateProfile(request);
                                prof_image.setImageURI(ImageUri);
                                FirebaseDatabase.getInstance().getReference().child("users").child(mUser.getUid()).child("photourl").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        wait_dialog.dismiss();
                                        if (task.isSuccessful()){
                                            Toast.makeText(Profile.this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(Profile.this, "Technical error occured. Try again later.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                wait_dialog.dismiss();
                                Toast.makeText(Profile.this, "Error in fetching download url. Try again later", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        wait_dialog.dismiss();
                        Toast.makeText(Profile.this, "Error in uploading new profile picture. Try again later", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void goBack(View view){
        finish();
    }

}
