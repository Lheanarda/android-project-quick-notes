package com.example.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.ProxyFileDescriptorCallback;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private EditText edtEmail,edtPassword;
    private String email,password;
    private ProgressDialog pbSendEmail,pbLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
    }

    public void LogInClick(View view){
        email = edtEmail.getText().toString();
        password = edtPassword.getText().toString();

        if(email.equals("")){
            edtEmail.setError("Enter Email");
        }else if (password.equals("")){
            edtPassword.setError("Enter Password");
        }else{
            pbLogin = ProgressDialog.show(this,"Login","Retrieving Data...",true);
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                pbLogin.dismiss();
                                startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                finish();
                            }else{
                                pbLogin.dismiss();
                                Toast.makeText(MainActivity.this
                                        ,"Login Failed : "+task.getException()
                                        ,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    public void SignUpClick (View view){
        startActivity(new Intent(MainActivity.this,SignupActivity.class));
    }

    public void ForgotPasswordClick (View view){
        email = edtEmail.getText().toString();
        password = edtPassword.getText().toString();

        if(email.equals("")){
            edtEmail.setError("Enter Email");
        }else{
            pbSendEmail = ProgressDialog.show(this,"Reset Passsword","Sending Email...",true);
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        pbSendEmail.dismiss();
                        Toast.makeText(MainActivity.this,"Email Has Been Sent ! Check Your Email",Toast.LENGTH_SHORT).show();
                    }else {
                        pbSendEmail.dismiss();
                        Toast.makeText(MainActivity.this,"Failed to send email : "+task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        if(mUser!=null){
            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
