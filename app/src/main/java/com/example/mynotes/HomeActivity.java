package com.example.mynotes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class HomeActivity extends AppCompatActivity {
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private  String userID,strNote;
    private EditText edtAddNote;
    private ProgressDialog pdLoadContent;

    private RecyclerView rvNotes;
    private NoteAdapter adapter;
    private List<NoteModel> noteModelList = new ArrayList<>();
    private DatabaseReference mDatabaseNotes;

    private ChildEventListener mChildEventListener;
    private boolean loadNotesExecuted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("My Notes");
        edtAddNote = findViewById(R.id.edtAddNote);
        //GET USER ID
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userID = currentUser.getUid();
        //END GET USER ID

        mRootRef = FirebaseDatabase.getInstance().getReference();
        rvNotes = findViewById(R.id.rvNotes);
        noteModelList = new ArrayList<>();
        adapter = new NoteAdapter(this,noteModelList);

        //GridLayoutManager --> Grid Format (Gallery)
        //LinearLayoutManager -->Linear Format
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true); //DESCENDING READING
        layoutManager.setStackFromEnd(true); //DESCENDING READING

        rvNotes.setLayoutManager(layoutManager); //assign layout manager to recycler view
        rvNotes.setAdapter(adapter);

        //PROGRESS DIALOG
        new MyTask().execute();



    }
    public class MyTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            pdLoadContent = ProgressDialog.show(HomeActivity.this,"","Load Your Notes...");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(loadNotesExecuted == true){
                pdLoadContent.dismiss();
            }

        }

        @Override
        protected Void doInBackground(Void... voids) {
            loadNotes();
            return null;
        }
    }

    private void loadNotes(){

        mDatabaseNotes = mRootRef.child("notes").child(userID); //SELECT NOTES BY USERID
        Query noteQuery = mDatabaseNotes.orderByKey();
        noteModelList.clear();//clearing existing record to pass the new record
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                NoteModel note = dataSnapshot.getValue(NoteModel.class);
                if(!noteModelList.contains(note)){
                    noteModelList.add(note);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                NoteModel note = dataSnapshot.getValue(NoteModel.class);
                noteModelList.remove(note);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        noteQuery.addChildEventListener(mChildEventListener);
        loadNotesExecuted = true;

    }

    public void btnAddClick(View view) {
        strNote = edtAddNote.getText().toString().trim();
        if (strNote.equals("")) {
            edtAddNote.setError("Type Something");
            return;
        }
        //FORM A PATH FOR DATABASE
        //Suppose "Notes" == normal table AND with hash map generate column
        String current_user_ref = "notes/" + userID;
        DatabaseReference note_push = mRootRef.child("notes").child(userID).push(); //add new note id below userid
        String noteID = note_push.getKey(); //NOTE UNIQUE KEY

        //GENERATE COLUMNS
        Map noteMap = new HashMap();
        noteMap.put("noteID", noteID);
        noteMap.put("note", strNote);
        noteMap.put("addedOn", ServerValue.TIMESTAMP); //GET CURRENT TIMESTAMP
        noteMap.put("addedBy", userID);

        Map noteNodeMap = new HashMap();
        noteNodeMap.put(current_user_ref + "/" + noteID, noteMap);

//        mRootRef.updateChildren(noteNodeMap);//UPDATE DATABASE
        mRootRef.updateChildren(noteNodeMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError!=null){
                    Toast.makeText(HomeActivity.this,
                            "Something Went Wrong : "+databaseError.getMessage(),
                            Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(HomeActivity.this,
                            "Note Saved Successfully",
                            Toast.LENGTH_SHORT).show();
                    edtAddNote.setText("");

                }
            }
        });

    }

    //ADD MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id =item.getItemId(); //menu yg di pilih
        if(id == R.id.menuProfile){
            Intent intent = new Intent(HomeActivity.this,ProfileActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
