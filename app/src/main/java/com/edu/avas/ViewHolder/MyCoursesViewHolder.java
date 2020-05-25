package com.edu.avas.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.edu.avas.R;

public class MyCoursesViewHolder extends RecyclerView.ViewHolder {
    public TextView myCourseName;
    public ConstraintLayout courseStatusLayout;

    public MyCoursesViewHolder(@NonNull View itemView) {
        super(itemView);

        courseStatusLayout = itemView.findViewById(R.id.courseStatusLayout);
        myCourseName = itemView.findViewById(R.id.myCourseName);
    }
}