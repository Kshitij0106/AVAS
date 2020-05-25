package com.edu.avas.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edu.avas.Model.Courses;
import com.edu.avas.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CoursesViewHolder> {
    private List<Courses> coursesList;
    private Context context;
    private onCourseClickedListener adapterListener;

    public interface onCourseClickedListener {
        void onCourseClicked(int pos, String name);
    }

    public void setOnCourseClicked(onCourseClickedListener onCourseClicked) {
        adapterListener = onCourseClicked;
    }

    public CoursesAdapter(List<Courses> coursesList, Context context) {
        this.coursesList = coursesList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull CoursesViewHolder holder, int position) {
        Picasso.get().load(coursesList.get(holder.getAdapterPosition()).getCoursePic()).fit().centerCrop().into(holder.coursePic);
        holder.courseName.setText(coursesList.get(holder.getAdapterPosition()).getCourseName());
    }

    @NonNull
    @Override
    public CoursesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_courses, parent, false);
        CoursesViewHolder cvh = new CoursesViewHolder(view);
        return cvh;
    }

    @Override
    public int getItemCount() {
        return coursesList.size();
    }

    public class CoursesViewHolder extends RecyclerView.ViewHolder {
        public ImageView coursePic;
        public TextView courseName;

        public CoursesViewHolder(@NonNull View itemView) {
            super(itemView);

            coursePic = itemView.findViewById(R.id.coursePic);
            courseName = itemView.findViewById(R.id.courseName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adapterListener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            adapterListener.onCourseClicked(pos, courseName.getText().toString());
                        }
                    }
                }
            });
        }
    }
}