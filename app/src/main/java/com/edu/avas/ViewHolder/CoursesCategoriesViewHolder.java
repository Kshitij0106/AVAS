package com.edu.avas.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edu.avas.R;

public class CoursesCategoriesViewHolder extends RecyclerView.ViewHolder {
    public TextView coursesTitle;
    public RecyclerView coursesRecyclerView;

    public CoursesCategoriesViewHolder(@NonNull View itemView) {
        super(itemView);

        coursesTitle = itemView.findViewById(R.id.coursesTitle);
        coursesRecyclerView = itemView.findViewById(R.id.coursesRecyclerView);
    }
}