package com.edu.avas.Model;

public class Notes {

    String notes,notesTitle;

    public Notes() {
    }

    public Notes(String notes, String notesTitle) {
        this.notes = notes;
        this.notesTitle = notesTitle;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotesTitle() {
        return notesTitle;
    }

    public void setNotesTitle(String notesTitle) {
        this.notesTitle = notesTitle;
    }
}