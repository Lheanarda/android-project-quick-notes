package com.example.mynotes;

public class NoteModel {
    private String noteID,note,addedBy;
    private long addedOn;

    public NoteModel() { //FOR ADAPTER PURPOSE NEED EMPTY CONSTRUCTOR
    }

    public NoteModel(String noteID, String note, String addedBy, long addedOn) {
        this.noteID = noteID;
        this.note = note;
        this.addedBy = addedBy;
        this.addedOn = addedOn;
    }

    public String getNoteID() {
        return noteID;
    }

    public void setNoteID(String noteID) {
        this.noteID = noteID;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public long getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(long addedOn) {
        this.addedOn = addedOn;
    }
}
