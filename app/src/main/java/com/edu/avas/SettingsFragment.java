package com.edu.avas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.edu.avas.Common.Connectivity;
import com.edu.avas.Database.MyCoursesDatabase;
import com.edu.avas.Common.Session;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsFragment extends Fragment implements View.OnClickListener {
    private CardView logOutCardView, deleteAccountCardView, aboutUsCardView, contactUsCardView;
    private FirebaseAuth auth;
    private Session session;

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        logOutCardView = view.findViewById(R.id.logOutCardView);
        deleteAccountCardView = view.findViewById(R.id.deleteAccountCardView);
        aboutUsCardView = view.findViewById(R.id.aboutUsCardView);
        contactUsCardView = view.findViewById(R.id.contactUsCardView);
        session = new Session(getContext());

        if (!session.isLoggedIn()) {
            logOutCardView.setEnabled(false);
            deleteAccountCardView.setEnabled(false);
        }

        logOutCardView.setOnClickListener(this);
        deleteAccountCardView.setOnClickListener(this);
        aboutUsCardView.setOnClickListener(this);
        contactUsCardView.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == logOutCardView) {
            if (Connectivity.isConnectedToInternet(getContext())) {
                AlertDialog.Builder logOutDalog = new AlertDialog.Builder(getContext());
                logOutDalog.setMessage("LogOut !");
                logOutDalog.setCancelable(false);
                logOutDalog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Session session = new Session(getContext());
                        session.setLoggedIn(false);
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        Toast.makeText(getActivity(), "LogOut Successful", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                logOutDalog.show();
            } else {
                Toast.makeText(getContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            }

        } else if (v == deleteAccountCardView) {
            if (Connectivity.isConnectedToInternet(getContext())) {
                AlertDialog.Builder deleteAcc = new AlertDialog.Builder(getContext());
                deleteAcc.setMessage("Are you sure you want to Delete this Account !");
                deleteAcc.setCancelable(false);
                deleteAcc.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        auth = FirebaseAuth.getInstance();
                        FirebaseUser user = auth.getCurrentUser();
                        user.delete();
                        MyCoursesDatabase database = new MyCoursesDatabase(getContext());
                        database.deleteList();
                        Session session = new Session(getContext());
                        session.setLoggedIn(false);
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        Toast.makeText(getActivity(), "Account Deleted Successful", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                deleteAcc.show();
            } else {
                Toast.makeText(getContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            }

        } else if (v == aboutUsCardView) {
            getFragmentManager().beginTransaction().replace(R.id.mainPage, new AboutUsFragment()).addToBackStack(" ").commit();
        } else if (v == contactUsCardView) {
            getFragmentManager().beginTransaction().replace(R.id.mainPage, new ContactUsFragment()).addToBackStack(" ").commit();
        }
    }
}