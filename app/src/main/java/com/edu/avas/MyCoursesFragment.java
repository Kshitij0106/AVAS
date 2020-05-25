package com.edu.avas;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.avas.Common.Connectivity;
import com.edu.avas.Common.Session;
import com.edu.avas.Model.Courses;
import com.edu.avas.ViewHolder.MyCoursesViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyCoursesFragment extends Fragment {
    public SwipeRefreshLayout swipeLayoutMyCourses;
    private RecyclerView myCoursesRecyclerView;
    private FirebaseAuth auth;
    private TextView coursesInProgress;
    private DatabaseReference courseRef;
    private FirebaseRecyclerOptions<Courses> coursesOptions;
    private FirebaseRecyclerAdapter<Courses, MyCoursesViewHolder> coursesAdapter;
    private TextView noPurchaseCourse, purchaseCourse;
    private Session session;
    private TextView logInToSeeCourse, goToLogIn;

    public MyCoursesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_courses, container, false);

        swipeLayoutMyCourses = view.findViewById(R.id.swipeLayoutMyCourses);
        coursesInProgress = view.findViewById(R.id.coursesInProgress);
        myCoursesRecyclerView = view.findViewById(R.id.myCoursesRecyclerView);
        noPurchaseCourse = view.findViewById(R.id.noPurchaseCourse);
        purchaseCourse = view.findViewById(R.id.purchaseCourse);
        logInToSeeCourse = view.findViewById(R.id.logInToSeeCourse);
        goToLogIn = view.findViewById(R.id.goToLogIn);
        session = new Session(getContext());

        swipeLayoutMyCourses.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkLogInStatus();
            }
        });

        checkLogInStatus();
        return view;
    }

    public void checkLogInStatus() {
        if (Connectivity.isConnectedToInternet(getContext())) {
            swipeLayoutMyCourses.setRefreshing(false);
            if (session.isLoggedIn()) {
                checkMyCourses();
            } else {
                coursesInProgress.setVisibility(View.GONE);
                logInToSeeCourse.setVisibility(View.VISIBLE);
                goToLogIn.setVisibility(View.VISIBLE);
                goToLogIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getFragmentManager().beginTransaction().replace(R.id.mainPage, new LogInFragment()).addToBackStack(" ").commit();
                    }
                });
            }
        } else {
            Toast.makeText(getContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkMyCourses() {
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String uid = user.getUid();
        courseRef = FirebaseDatabase.getInstance().getReference("Students").child(uid).child("My Courses");
        courseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    showMyCourses();
                } else {
                    coursesInProgress.setVisibility(View.GONE);
                    noPurchaseCourse.setVisibility(View.VISIBLE);
                    purchaseCourse.setVisibility(View.VISIBLE);
                    purchaseCourse.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getFragmentManager().beginTransaction().replace(R.id.mainPage, new HomePageFragment()).addToBackStack(" ").commit();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showMyCourses() {
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String uid = user.getUid();
        courseRef = FirebaseDatabase.getInstance().getReference("Students").child(uid).child("My Courses");
        coursesOptions = new FirebaseRecyclerOptions.Builder<Courses>().setQuery(courseRef, Courses.class).build();
        coursesAdapter = new FirebaseRecyclerAdapter<Courses, MyCoursesViewHolder>(coursesOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final MyCoursesViewHolder myCoursesViewHolder, int i, @NonNull Courses courses) {
                if (courses.getStatus().equals("Granted")) {
                    myCoursesViewHolder.courseStatusLayout.setVisibility(View.VISIBLE);
                    myCoursesViewHolder.myCourseName.setText(courses.getCourseName());

                    myCoursesViewHolder.courseStatusLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle courseName = new Bundle();
                            courseName.putString("Cname", myCoursesViewHolder.myCourseName.getText().toString());
                            CoursesTopicsFragment ctf = new CoursesTopicsFragment();
                            ctf.setArguments(courseName);
                            getFragmentManager().beginTransaction().replace(R.id.mainPage, ctf).addToBackStack(" ").commit();
                        }
                    });
                }
            }

            @NonNull
            @Override
            public MyCoursesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_my_courses, parent, false);
                MyCoursesViewHolder cvh = new MyCoursesViewHolder(view);
                return cvh;
            }
        };

        myCoursesRecyclerView.setHasFixedSize(true);
        myCoursesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myCoursesRecyclerView.setAdapter(coursesAdapter);
        coursesAdapter.startListening();
        coursesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (coursesAdapter != null) {
            coursesAdapter.startListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (coursesAdapter != null) {
            coursesAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (coursesAdapter != null) {
            coursesAdapter.stopListening();
        }
    }
}