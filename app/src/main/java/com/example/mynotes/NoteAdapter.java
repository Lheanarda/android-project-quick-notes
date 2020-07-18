package com.example.mynotes;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {

    public class NoteHolder extends RecyclerView.ViewHolder{
        //ACCORDING TO OBJECTS IN NOTE_LAYOUT
        TextView txtNote,txtAddedOn;
        ImageView imgDeleteNote,imgCopyText;
        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            txtNote = itemView.findViewById(R.id.txtNote);
            txtAddedOn = itemView.findViewById(R.id.txtAddedOn);
            imgDeleteNote = itemView.findViewById(R.id.imgDeleteNote);
            imgCopyText = itemView.findViewById(R.id.imgCopyText);
        }
    }
    private Context mContext;
    private List<NoteModel> mNoteList;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    //GENERATE CONSTRUCTOR
    public NoteAdapter(Context context, List<NoteModel> noteList) {
        this.mContext = context;
        this.mNoteList = noteList;
        this.mAuth = FirebaseAuth.getInstance();

    }

    @NonNull
    @Override
    public NoteAdapter.NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout,parent,false);
        return new NoteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NoteAdapter.NoteHolder holder, int position) {
        final NoteModel noteModel = mNoteList.get(position);
        holder.txtNote.setText(noteModel.getNote());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy HH:mm"); //Customize timestamp format
        String addedOn = dateFormat.format(new Date(noteModel.getAddedOn()));

        holder.txtAddedOn.setText(addedOn);

        //FOR DELETE PURPOSE
        holder.imgDeleteNote.setTag(noteModel.getNoteID());
        holder.imgDeleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String userID = currentUser.getUid();

                mRootRef.child("notes").child(userID).child(holder.imgDeleteNote.getTag().toString())
                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(mContext, "Note Deleted",Toast.LENGTH_SHORT).show();
                            mNoteList.remove(noteModel);
                            notifyDataSetChanged();
                        }else {
                            Toast.makeText(mContext,"Failed to delete : "+task.getException(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //FOR COPY PURPOSE
        holder.imgCopyText.setTag(noteModel.getNoteID());
        holder.imgCopyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String userID = currentUser.getUid();

                DatabaseReference currentNoteref =
                        mRootRef.child("notes").child(userID)
                                .child(holder.imgCopyText.getTag().toString()).child("note");

                currentNoteref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String getNote = dataSnapshot.getValue(String.class);
                        ClipboardManager clipboardManager = (ClipboardManager) mContext
                                .getSystemService(mContext.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("GetNote",getNote);
                        clipboardManager.setPrimaryClip(clipData);
                        Toast.makeText(mContext,"Copied to clipboard",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        //END COPY
    }

    @Override
    public int getItemCount() {
        try{
            return mNoteList.size(); //total yg akan dikluarkan
        }catch (Exception ex){
            return 0; //kalau error ga keluarin apa"
        }

    }
}
