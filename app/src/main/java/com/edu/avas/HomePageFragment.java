package com.edu.avas;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.avas.Common.Connectivity;
import com.edu.avas.Common.Session;
import com.edu.avas.Database.MyCoursesDatabase;
import com.edu.avas.Model.Courses;
import com.edu.avas.Model.CoursesCategories;
import com.edu.avas.Model.User;
import com.edu.avas.ViewHolder.CoursesAdapter;
import com.edu.avas.ViewHolder.CoursesCategoriesViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomePageFragment extends Fragment {
    private SwipeRefreshLayout swipeLayoutHomePage;
    private BottomNavigationView bottomNavigationView;
    private TextView showUserName;
    private FirebaseAuth auth;
    private DatabaseReference userRef, courseRef;
    private RecyclerView categoriesRecyclerView;
    private FirebaseRecyclerOptions<CoursesCategories> coursesOptions;
    private FirebaseRecyclerAdapter<CoursesCategories, CoursesCategoriesViewHolder> coursesAdapter;
    private ArrayList<Courses> courses;
    private Session session;
    private ArrayList<String> myCourses;

    public HomePageFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        swipeLayoutHomePage = view.findViewById(R.id.swipeLayoutHomePage);
        bottomNavigationView = view.findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        showUserName = view.findViewById(R.id.showUserName);
        session = new Session(getContext());

        categoriesRecyclerView = view.findViewById(R.id.categoriesRecyclerView);
        categoriesRecyclerView.setHasFixedSize(true);
        courses = new ArrayList<>();
        myCourses = new ArrayList<>();

        swipeLayoutHomePage.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showCourses();
            }
        });

        if (session.isLoggedIn()) {
            auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            final String uid = user.getUid();
            ShowUsername(uid);
        } else {
            showUserName.setText("Welcome Guest");
        }

        showCourses();

        return view;
    }

    private void showCourses() {
        if (Connectivity.isConnectedToInternet(getContext())) {
            swipeLayoutHomePage.setRefreshing(false);
            courseRef = FirebaseDatabase.getInstance().getReference("Courses");
            coursesOptions = new FirebaseRecyclerOptions.Builder<CoursesCategories>().setQuery(courseRef, CoursesCategories.class).build();
            coursesAdapter = new FirebaseRecyclerAdapter<CoursesCategories, CoursesCategoriesViewHolder>(coursesOptions) {
                @Override
                protected void onBindViewHolder(@NonNull CoursesCategoriesViewHolder coursesCategoriesViewHolder, int i, @NonNull CoursesCategories coursesCategories) {
                    courses.clear();
                    coursesCategoriesViewHolder.coursesTitle.setText(coursesCategories.getTitle());
                    courses = coursesCategories.getCoursesList();

                    CoursesAdapter adapter = new CoursesAdapter(courses, getContext());

                    adapter.setOnCourseClicked(new CoursesAdapter.onCourseClickedListener() {
                        @Override
                        public void onCourseClicked(int pos, String name) {
                            Bundle bundle = new Bundle();
                            bundle.putString("Cname", name);
                            if (session.isLoggedIn()) {
                                MyCoursesDatabase database = new MyCoursesDatabase(getContext());
                                int check = database.checkInList(name);
                                if (check == 1) {
                                    CoursesTopicsFragment ctf = new CoursesTopicsFragment();
                                    ctf.setArguments(bundle);
                                    getFragmentManager().beginTransaction().replace(R.id.mainPage, ctf).addToBackStack(" ").commit();
                                } else if (check == 0) {
                                    CourseDetailsFragment cdf = new CourseDetailsFragment();
                                    cdf.setArguments(bundle);
                                    getFragmentManager().beginTransaction().replace(R.id.mainPage, cdf).addToBackStack(" ").commit();
                                }
                            } else {
                                CourseDetailsFragment cdf = new CourseDetailsFragment();
                                cdf.setArguments(bundle);
                                getFragmentManager().beginTransaction().replace(R.id.mainPage, cdf).addToBackStack(" ").commit();
                            }
                        }
                    });

                    coursesCategoriesViewHolder.coursesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    coursesCategoriesViewHolder.coursesRecyclerView.setAdapter(adapter);
                    coursesCategoriesViewHolder.coursesRecyclerView.setNestedScrollingEnabled(false);
                }

                @NonNull
                @Override
                public CoursesCategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_courses_categories, parent, false);
                    CoursesCategoriesViewHolder ccvh = new CoursesCategoriesViewHolder(view);
                    return ccvh;
                }
            };

            categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            categoriesRecyclerView.setAdapter(coursesAdapter);
            coursesAdapter.startListening();
            coursesAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void ShowUsername(String uid) {
        userRef = FirebaseDatabase.getInstance().getReference("Students").child(uid);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String userName = user.getUserName();
                showUserName.setText(" Welcome " + userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment goToFragment = null;
            switch (item.getItemId()) {
                case R.id.chatNav:
                    goToFragment = new TeacherFragment();
                    break;
                case R.id.coursesNav:
                    goToFragment = new MyCoursesFragment();
                    break;
                case R.id.prfileNav:
                    goToFragment = new ProfileFragment();
                    break;
                case R.id.settingsNav:
                    goToFragment = new SettingsFragment();
                    break;
            }
            getFragmentManager().beginTransaction().replace(R.id.mainPage, goToFragment).addToBackStack(" ").commit();
            return true;
        }
    };

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