package com.edu.avas;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.edu.avas.Common.Connectivity;
import com.edu.avas.Common.Session;
import com.edu.avas.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpFragment extends Fragment {
    private ImageView logo;
    private EditText userFullName, userEmail, userPass, userCnfrmPass;
    private Button signUp;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Session session;
    private FirebaseAuth auth;
    private DatabaseReference reference;

    public SignUpFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        logo = view.findViewById(R.id.logoSignUp);
        userFullName = view.findViewById(R.id.fullNameSignUp);
        userEmail = view.findViewById(R.id.userEmailSignUp);
        userPass = view.findViewById(R.id.userPassSignUp);
        userCnfrmPass = view.findViewById(R.id.userCmfrmPassSignUp);
        signUp = view.findViewById(R.id.signUpButton);
        pref = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editor = pref.edit();
        session = new Session(getActivity());
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Students");

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connectivity.isConnectedToInternet(getContext())) {
                    final String name = userFullName.getText().toString();
                    final String email = userEmail.getText().toString();
                    String pass = userPass.getText().toString();
                    String cnfrmPass = userCnfrmPass.getText().toString();

                    char ch = ' ';
                    String userName = "";
                    for (int i = 0; i < name.length(); i++) {
                        ch = name.charAt(i);
                        if (Character.isWhitespace(ch)) {
                            break;
                        } else {
                            userName += ch;
                        }
                    }

                    if (name.isEmpty() && email.isEmpty() && pass.isEmpty() && cnfrmPass.isEmpty()) {
                        userFullName.setError("Enter Valid UserName");
                        userEmail.setError("Enter Valid Email Address");
                        userPass.setError("Enter Valid Password");
                        userCnfrmPass.setError("Enter Valid Password");
                    } else if (name.isEmpty()) {
                        userFullName.setError("Enter Valid UserName");
                    } else if (email.isEmpty()) {
                        userEmail.setError("Enter Valid Email Address");
                    } else if (pass.isEmpty()) {
                        userPass.setError("Enter Valid Password");
                    } else if (cnfrmPass.isEmpty()) {
                        userCnfrmPass.setError("Enter Valid Password");
                    } else if (!pass.equals(cnfrmPass)) {
                        userCnfrmPass.setError("Password does not match");
                    } else {
                        final String uName = userName;
                        auth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                session.setLoggedIn(true);

                                FirebaseUser user = auth.getCurrentUser();
                                String uid = user.getUid();
                                editor.putString("uid", uid).commit();

                                User userInfo = new User();
                                userInfo.setUid(uid);
                                userInfo.setUserName(uName);
                                userInfo.setUserFullName(name);
                                userInfo.setUserEmail(email);

                                reference.child(uid).setValue(userInfo);
                                getFragmentManager().beginTransaction().replace(R.id.mainPage, new HomePageFragment()).commit();
                                Toast.makeText(getContext(), "Welcome", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}