package com.edu.avas.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.edu.avas.R;

public class NotesViewHolder extends RecyclerView.ViewHolder {
    public ConstraintLayout coursesTopicNotesLayout;
    public TextView courseTopicNotesTitle;

    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);

        coursesTopicNotesLayout = itemView.findViewById(R.id.coursesTopicNotesLayout);
        courseTopicNotesTitle = itemView.findViewById(R.id.courseTopicNotesTitle);
    }
}