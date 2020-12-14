package com.edu.avas;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.avas.Common.Connectivity;
import com.edu.avas.Common.Session;
import com.edu.avas.Database.MyCoursesDatabase;
import com.edu.avas.Model.Courses;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

public class CourseDetailsFragment extends Fragment {
    private SwipeRefreshLayout swipeLayoutCourseDetails;
    private TextView courseDetailsName, courseDetailsDesc, courseDetailsAvailability, courseDetailsPrice;
    private Button buyNowButton;
    private DatabaseReference ref, courseRequest, userRef;
    private FirebaseAuth auth;
    private Session session;

    public CourseDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_details, container, false);

        Bundle bundle = this.getArguments();
        final String name = bundle.getString("Cname");

        swipeLayoutCourseDetails = view.findViewById(R.id.swipeLayoutCourseDetails);
        courseDetailsName = view.findViewById(R.id.courseDetailsName);
        courseDetailsDesc = view.findViewById(R.id.courseDetailsDesc);
        courseDetailsPrice = view.findViewById(R.id.courseDetailsPrice);
        courseDetailsAvailability = view.findViewById(R.id.courseDetailsAvailability);
        buyNowButton = view.findViewById(R.id.buyNowButton);
        session = new Session(getContext());

        swipeLayoutCourseDetails.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showCourses(name);
            }
        });
        showCourses(name);
        requestCourse(name);

        return view;
    }

    public void showCourses(String name) {
        if (Connectivity.isConnectedToInternet(getContext())) {
            swipeLayoutCourseDetails.setRefreshing(false);

            courseDetailsName.setText(name);
            ref = FirebaseDatabase.getInstance().getReference("CoursesDetails").child(name);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Courses courses = dataSnapshot.getValue(Courses.class);
                    String a = courses.getCourseDesc();
                    String desc = a.replace("\\n", "\n");
                    courseDetailsDesc.setText(desc);
                    int price = Integer.parseInt(courses.getCoursePrice());
                    Locale locale = new Locale("EN","IN");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                    courseDetailsPrice.setText("-> Course Price: "+fmt.format(price));
                    String courseAvailability = courses.getCourseAvailability();
                    if (courseAvailability.equals("NA")) {
                        courseDetailsAvailability.setVisibility(View.VISIBLE);
                        buyNowButton.setEnabled(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void requestCourse(final String courseName) {
        buyNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connectivity.isConnectedToInternet(getContext())) {
                    if (session.isLoggedIn()) {
                        auth = FirebaseAuth.getInstance();
                        FirebaseUser user = auth.getCurrentUser();
                        final String uid = user.getUid();

                        userRef = FirebaseDatabase.getInstance().getReference("Students").child(uid).child("userFormStatus");
                        userRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    Bundle cNameBundle = new Bundle();
                                    cNameBundle.putString("Cname", courseName);
                                    ApplicationForm form = new ApplicationForm();
                                    form.setArguments(cNameBundle);
                                    getFragmentManager().beginTransaction().replace(R.id.mainPage, form).addToBackStack("").commit();
                                } else {
                                    //open payment link
                                    courseRequest = FirebaseDatabase.getInstance().getReference("Students").child(uid).child("My Courses");

                                    String oId = String.valueOf(System.currentTimeMillis());
                                    String price = courseDetailsPrice.getText().toString();
                                    Courses requestCourse = new Courses(courseName,oId, "Pending");
                                    courseRequest.child(oId).setValue(requestCourse);
                                    MyCoursesDatabase database = new MyCoursesDatabase(getContext());
                                    database.addToList(courseName);

                                    getFragmentManager().beginTransaction().replace(R.id.mainPage, new HomePageFragment()).addToBackStack("").commit();
                                    Toast.makeText(getContext(), "Course Requested \nPlease Wait till teacher gives you access", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else {
                        AlertDialog.Builder logInDialog = new AlertDialog.Builder(getContext());
                        logInDialog.setMessage("Log In First To Buy This Course");
                        logInDialog.setPositiveButton("Log In", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getFragmentManager().beginTransaction().replace(R.id.mainPage, new LogInFragment()).addToBackStack(" ").commit();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        logInDialog.show();
                    }
                } else {
                    Toast.makeText(getContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}