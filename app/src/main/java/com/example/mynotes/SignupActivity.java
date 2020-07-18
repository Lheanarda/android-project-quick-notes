package com.example.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    private EditText edtEmail,edtPassword,edtConfirmationPassword;
    private String email,password,confirmationPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edtEmail = findViewById(R.id.edtEmailSignUp);
        edtPassword = findViewById(R.id.edtPasswordSignUp);
        edtConfirmationPassword = findViewById(R.id.edtConfirmationPassword);
    }

    public void btnSignUpClick(View view){
        email = edtEmail.getText().toString();
        password = edtPassword.getText().toString();
        confirmationPassword = edtConfirmationPassword.getText().toString();

        if(email.equals("")){
            edtEmail.setError("Enter Email");
        }else if(password.equals("")){
            edtPassword.setError("Enter Password");
        }else if (confirmationPassword.equals("")){
            edtConfirmationPassword.setError("Enter Password Confirmation");
        }else if(!confirmationPassword.equals(password)){
            edtConfirmationPassword.setError("Confirmation Password Incorrect");
        }
        else{
            //pendaftaran akun melalui email di firebase
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignupActivity.this,"Account has been made",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignupActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(SignupActivity.this,"Failed to create account : "+task.getException()
                                ,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
