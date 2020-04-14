package com.hrishi3331studio.template_informal.UserAuth;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hrishi3331studio.template_informal.General.MainActivity;
import com.hrishi3331studio.template_informal.R;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private EditText name;
    private EditText email;
    private EditText mobile;
    private EditText password;
    private EditText password2;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().child("users");

        name = (EditText)findViewById(R.id.form_name);
        email = (EditText)findViewById(R.id.form_email);
        mobile = (EditText)findViewById(R.id.form_phone);
        password = (EditText)findViewById(R.id.form_pass);
        password2 = (EditText)findViewById(R.id.form_pass2);

        dialog = new ProgressDialog(SignUp.this);
        dialog.setTitle("Creating Account");
        dialog.setMessage("Please wait...");
        dialog.setCanceledOnTouchOutside(false);
    }

    public void Register(View view){
        String name = this.name.getText().toString();
        String email = this.email.getText().toString();
        String mobile = this.mobile.getText().toString();
        String password = this.password.getText().toString();
        String password2 = this.password2.getText().toString();

        if (name.isEmpty()){
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
        }
        else if (email.isEmpty()){
            Toast.makeText(this, "Please enter a valid Email ID", Toast.LENGTH_SHORT).show();
        }
        else if (mobile.isEmpty()){
            Toast.makeText(this, "Please enter your mobile number", Toast.LENGTH_SHORT).show();
        }
        else if (password.length() < 6){
            Toast.makeText(this, "Password length should be atleast 6 characters", Toast.LENGTH_SHORT).show();
        }
        else if (!password.equals(password2)){
            Toast.makeText(this, "Re-entered password did not match", Toast.LENGTH_SHORT).show();
        }
        else{
            ConfirmSignUp(name, email, password, mobile);
        }
    }

    private void ConfirmSignUp(final String name, final String email, final String password, final String mobile) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
        builder.setTitle("Sign Up");
        builder.setMessage("Do you want to create account?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SignUp(name, email, password, mobile);
                dialog.dismiss();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog confirmation = builder.create();
        confirmation.show();
    }

    private void SignUp(final String name, String email, String password, final String mobile) {
        dialog.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                    builder.setDisplayName(name);
                    user.updateProfile(builder.build());
                    DatabaseReference aref = mRef.child(user.getUid());
                    aref.child("name").setValue(name);
                    aref.child("contact").setValue(mobile);
                    aref.child("email").setValue(user.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(SignUp.this, "New Account created successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                startActivity(new Intent(SignUp.this, MainActivity.class));
                                finish();
                            }else {
                                Toast.makeText(SignUp.this, "Error in updating your account. Please try again or contact us", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });

                }
                else {
                    Toast.makeText(SignUp.this, task.getException().getLocalizedMessage().trim(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

    }

    public void goBack(View view){
        finish();
    }
}
