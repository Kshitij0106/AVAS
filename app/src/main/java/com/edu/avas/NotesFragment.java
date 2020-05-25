package com.edu.avas;

import android.content.Intent;
import android.net.Uri;
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
import com.edu.avas.Model.Notes;
import com.edu.avas.ViewHolder.NotesViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotesFragment extends Fragment {
    private SwipeRefreshLayout swipeLayoutCourseTopicsNotes;
    private TextView topicFragmentCourseNameNotes, NotesHeading, noNotes;
    private RecyclerView courseTopicNotesRecyclerView;
    private FirebaseRecyclerOptions<Notes> notesOptions;
    private FirebaseRecyclerAdapter<Notes, NotesViewHolder> notesAdapter;
    private DatabaseReference notesRef;
    private FirebaseAuth auth;

    public NotesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        Bundle bundle = this.getArguments();
        final String courseName = bundle.getString("Cname");

        swipeLayoutCourseTopicsNotes = view.findViewById(R.id.swipeLayoutCourseTopicsNotes);
        topicFragmentCourseNameNotes = view.findViewById(R.id.topicFragmentCourseNameNotes);
        topicFragmentCourseNameNotes.setText(courseName);
        NotesHeading = view.findViewById(R.id.NotesHeading);
        noNotes = view.findViewById(R.id.noNotes);
        courseTopicNotesRecyclerView = view.findViewById(R.id.courseTopicNotesRecyclerView);

        swipeLayoutCourseTopicsNotes.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkNotes(courseName);
            }
        });

        checkNotes(courseName);
        return view;
    }

    private void checkNotes(final String name) {
        if (Connectivity.isConnectedToInternet(getContext())) {
            swipeLayoutCourseTopicsNotes.setRefreshing(false);
            notesRef = FirebaseDatabase.getInstance().getReference("CoursesDetails").child(name).child("Notes");
            notesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        showNotes(name);
                    } else {
                        NotesHeading.setVisibility(View.GONE);
                        courseTopicNotesRecyclerView.setVisibility(View.GONE);
                        noNotes.setVisibility(View.VISIBLE);
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

    private void showNotes(final String name) {
        notesRef = FirebaseDatabase.getInstance().getReference("CoursesDetails").child(name).child("Notes");
        notesOptions = new FirebaseRecyclerOptions.Builder<Notes>().setQuery(notesRef, Notes.class).build();
        notesAdapter = new FirebaseRecyclerAdapter<Notes, NotesViewHolder>(notesOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final NotesViewHolder notesViewHolder, int i, @NonNull final Notes notes) {
                notesViewHolder.courseTopicNotesTitle.setText(notes.getNotesTitle());
                notesViewHolder.coursesTopicNotesLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(notes.getNotes()));
                        notesViewHolder.coursesTopicNotesLayout.getContext().startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_notes, parent, false);
                NotesViewHolder nvh = new NotesViewHolder(view);
                return nvh;
            }
        };

        courseTopicNotesRecyclerView.setHasFixedSize(true);
        courseTopicNotesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        courseTopicNotesRecyclerView.setAdapter(notesAdapter);
        courseTopicNotesRecyclerView.setNestedScrollingEnabled(false);
        notesAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (notesAdapter != null) {
            notesAdapter.startListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (notesAdapter != null) {
            notesAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (notesAdapter != null) {
            notesAdapter.stopListening();
        }
    }
}