package com.edu.avas.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.edu.avas.R;

public class CoursesTopicViewHolder extends RecyclerView.ViewHolder {
    public ConstraintLayout coursesTopicLayout;
    public TextView courseTopicVideoTitle;

    public CoursesTopicViewHolder(@NonNull View itemView) {
        super(itemView);

        coursesTopicLayout = itemView.findViewById(R.id.coursesTopicLayout);
        courseTopicVideoTitle = itemView.findViewById(R.id.courseTopicVideoTitle);
    }
}