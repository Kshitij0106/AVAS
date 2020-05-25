package com.edu.avas;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.avas.Common.Connectivity;
import com.edu.avas.Model.Courses;
import com.edu.avas.ViewHolder.CoursesTopicViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CoursesTopicsFragment extends Fragment {
    private SwipeRefreshLayout swipeLayoutCourseTopics;
    private TextView topicFragmentCourseName, topicsHeading, courseStatusPending, goToTeacher;
    private ImageView courseNotes;
    private RecyclerView courseTopicRecyclerView;
    private FirebaseRecyclerOptions<Courses> topicsOptions;
    private FirebaseRecyclerAdapter<Courses, CoursesTopicViewHolder> topicsAdapter;
    private FirebaseAuth auth;
    private DatabaseReference studentCoursesRef, courseTopicRef;

    public CoursesTopicsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle nameBundle = this.getArguments();
        final String courseName = nameBundle.getString("Cname");
        View view = inflater.inflate(R.layout.fragment_courses_topics, container, false);

        swipeLayoutCourseTopics = view.findViewById(R.id.swipeLayoutCourseTopics);
        courseNotes = view.findViewById(R.id.courseNotes);
        topicFragmentCourseName = view.findViewById(R.id.topicFragmentCourseName);
        topicFragmentCourseName.setText(courseName);
        topicsHeading = view.findViewById(R.id.topicsHeading);
        courseStatusPending = view.findViewById(R.id.courseStatusPending);
        courseTopicRecyclerView = view.findViewById(R.id.courseTopicRecyclerView);
        goToTeacher = view.findViewById(R.id.goToTeacher);

        swipeLayoutCourseTopics.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkStatus(courseName);
            }
        });

        courseNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle courseNotes = new Bundle();
                courseNotes.putString("Cname", courseName);
                NotesFragment nf = new NotesFragment();
                nf.setArguments(courseNotes);
                getFragmentManager().beginTransaction().replace(R.id.mainPage, nf).addToBackStack(" ").commit();
            }
        });

        checkStatus(courseName);
        setGoToTeacher();
        return view;
    }

    public void checkStatus(final String courseName) {
        if (Connectivity.isConnectedToInternet(getContext())) {
            swipeLayoutCourseTopics.setRefreshing(false);
            auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            studentCoursesRef = FirebaseDatabase.getInstance().getReference("Students").child(user.getUid()).child("My Courses");
            studentCoursesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Courses courses = data.getValue(Courses.class);
                        if (courses.getCourseName().equals(courseName)) {
                            String status = courses.getStatus();
                            if (status.equals("Pending")) {
                                courseStatusPending.setVisibility(View.VISIBLE);
                                goToTeacher.setVisibility(View.VISIBLE);
                                topicsHeading.setVisibility(View.GONE);
                                courseTopicRecyclerView.setVisibility(View.GONE);
                                courseNotes.setVisibility(View.GONE);
                            } else if (status.equals("Granted")) {
                                showTopics(courseName);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(getContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTopics(String courseName) {
        courseTopicRef = FirebaseDatabase.getInstance().getReference("CoursesDetails").child(courseName).child("Videos");
        topicsOptions = new FirebaseRecyclerOptions.Builder<Courses>().setQuery(courseTopicRef, Courses.class).build();
        topicsAdapter = new FirebaseRecyclerAdapter<Courses, CoursesTopicViewHolder>(topicsOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CoursesTopicViewHolder coursesTopicViewHolder, int i, @NonNull final Courses courses) {
                coursesTopicViewHolder.courseTopicVideoTitle.setText(courses.getVideoTitle());
                final String url = courses.getCourseVideo();

                coursesTopicViewHolder.coursesTopicLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), VideoActivity.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public CoursesTopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.courses_topics_layout, parent, false);
                CoursesTopicViewHolder ctvh = new CoursesTopicViewHolder(view);
                return ctvh;
            }
        };

        courseTopicRecyclerView.setHasFixedSize(true);
        courseTopicRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        courseTopicRecyclerView.setAdapter(topicsAdapter);
        courseTopicRecyclerView.setNestedScrollingEnabled(false);
        topicsAdapter.startListening();
    }

    public void setGoToTeacher() {
        goToTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.mainPage, new TeacherFragment()).addToBackStack(" ").commit();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (topicsAdapter != null) {
            topicsAdapter.startListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (topicsAdapter != null) {
            topicsAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (topicsAdapter != null) {
            topicsAdapter.stopListening();
        }
    }
}