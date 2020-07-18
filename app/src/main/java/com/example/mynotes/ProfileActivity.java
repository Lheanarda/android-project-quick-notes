package com.example.mynotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {

    private EditText edtName;
    private ImageView imgProfile;
    private ProgressDialog pbUpdateProfile;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private StorageReference mRootStorage;
    Uri localFileUri, serverFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edtName = findViewById(R.id.edtName);
        imgProfile = findViewById(R.id.imgProfile);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRootStorage = FirebaseStorage.getInstance().getReference();

        //GET USERNAME FROM EMAIL
        if(mUser!=null){
            edtName.setText(mUser.getDisplayName());
            Uri photoUri = mUser.getPhotoUrl();
            if(photoUri!=null){
                Glide.with(this)
                        .load(photoUri)
                        .placeholder(R.drawable.default_profile)
                        .error(R.drawable.default_profile)
                        .into(imgProfile);

            }
        }
    }

    public void UpdateNameOnly(){
        //UPDATE ACCOUNT DISPLAY NAME
        pbUpdateProfile = ProgressDialog.show(this,"","Updating Profile");
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(edtName.getText().toString())
                .build();

        mUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    pbUpdateProfile.dismiss();
                    Toast.makeText(ProfileActivity.this, "Profile Updated Sucessfully", Toast.LENGTH_SHORT).show();
                }
                else {
                    pbUpdateProfile.dismiss();
                    Toast.makeText(ProfileActivity.this, "Failed to update Profile :"
                            + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void UpdateNameAndProfilePicture(){
        pbUpdateProfile = ProgressDialog.show(this,"","Updating Profile...");
        String file_name =mUser.getUid()+".jpg";
        final StorageReference fileref =mRootStorage.child("images/"+file_name);
        fileref.putFile(localFileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pbUpdateProfile.dismiss();
                        fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                serverFileUri = uri;
                                UserProfileChangeRequest request =new UserProfileChangeRequest.Builder()
                                        .setDisplayName(edtName.getText().toString())
                                        .setPhotoUri(serverFileUri)
                                        .build();
                                mUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(ProfileActivity.this, "Profile Updated Sucessfully", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(ProfileActivity.this, "Failed to update Profile :"
                                                    + task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
    }

    public void btnSaveProfileClick(View view){
        if(edtName.getText().toString().trim().equals("")){
            edtName.setError("Enter Name");
        }else{
            if(localFileUri!=null){
                UpdateNameAndProfilePicture();
            }else{
                UpdateNameOnly();
            }
        }
    }

    public void ImgPickImages(View view){
        //ALIHKAN KE GAMBAR
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //SET IMAGES METHOD
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101){
            if(resultCode==RESULT_OK){
                localFileUri = data.getData();
                imgProfile.setImageURI(localFileUri);
            }
        }
    }

    public void btnLogoutClick(View view){
        mAuth.signOut();
        Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }


}
