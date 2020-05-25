package com.edu.avas;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.avas.Common.Connectivity;
import com.edu.avas.Common.Session;
import com.edu.avas.Model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TeacherFragment extends Fragment {
    private TextView teacherId;
    private ImageView unSeenMessage;
    private CardView cardLayout;
    private DatabaseReference teacherRef, msgRef;
    private FirebaseAuth auth;
    private Session session;
    private TextView logInToTalk, goToLogInToTalk;

    public TeacherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher, container, false);

        unSeenMessage = view.findViewById(R.id.unSeenMessage);
        teacherId = view.findViewById(R.id.teacherId);
        cardLayout = view.findViewById(R.id.cardLayout);
        session = new Session(getContext());
        logInToTalk = view.findViewById(R.id.logInToTalk);
        goToLogInToTalk = view.findViewById(R.id.goToLogInToTalk);

        checkLogInStatus();

        return view;
    }

    public void checkLogInStatus() {
        if (Connectivity.isConnectedToInternet(getContext())) {
            if (session.isLoggedIn()) {
                showTeacher();
                checkMsgStatus();
            } else {
                logInToTalk.setVisibility(View.VISIBLE);
                goToLogInToTalk.setVisibility(View.VISIBLE);
                cardLayout.setVisibility(View.GONE);
                goToLogInToTalk.setOnClickListener(new View.OnClickListener() {
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

    public void showTeacher() {
        teacherRef = FirebaseDatabase.getInstance().getReference("Teacher");
        teacherRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    final String tid = data.child("tid").getValue(String.class);
                    teacherId.setText(tid);

                    cardLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Connectivity.isConnectedToInternet(getContext())) {
                                Bundle bundle = new Bundle();
                                bundle.putString("tid", tid);
                                ChatFragment chat = new ChatFragment();
                                chat.setArguments(bundle);
                                getFragmentManager().beginTransaction().replace(R.id.mainPage, chat).addToBackStack(" ").commit();
                            } else {
                                Toast.makeText(getContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkMsgStatus() {
        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();

        msgRef = FirebaseDatabase.getInstance().getReference("Conversations");
        msgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Message msg = data.getValue(Message.class);
                    if (msg.getSenderId().equals(teacherId.getText().toString()) && msg.getReceiverId().equals(user.getUid()) && msg.getStatus().equals("unSeen")) {
                        unSeenMessage.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}