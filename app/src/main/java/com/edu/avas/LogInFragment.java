package com.edu.avas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.avas.Common.Connectivity;
import com.edu.avas.Common.Session;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInFragment extends Fragment {
    private ImageView logo;
    private EditText userLogInEmail, userLogInPass;
    private TextView forgotPassword, createAccount;
    private Button logInButton;
    private FirebaseAuth auth;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Session session;

    public LogInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);

        logo = view.findViewById(R.id.logo);
        userLogInEmail = view.findViewById(R.id.userLogInEmail);
        userLogInPass = view.findViewById(R.id.userLogInPass);
        logInButton = view.findViewById(R.id.logInButton);
        forgotPassword = view.findViewById(R.id.forgotPassword);
        createAccount = view.findViewById(R.id.createAccount);
        auth = FirebaseAuth.getInstance();
        pref = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editor = pref.edit();
        session = new Session(getActivity());

        resetPassword();
        logIn();

        return view;
    }

    public void logIn() {
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connectivity.isConnectedToInternet(getContext())) {
                    String email = userLogInEmail.getText().toString();
                    String pass = userLogInPass.getText().toString();

                    if (email.isEmpty() && pass.isEmpty()) {
                        userLogInEmail.setError("Please Enter Valid Email Address");
                        userLogInPass.setError("Please Enter Correct Password");
                    } else if (email.isEmpty()) {
                        userLogInEmail.setError("Please Enter Valid Email Address");
                    } else if (pass.isEmpty()) {
                        userLogInPass.setError("Please Enter Correct Password");
                    } else {
                        auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                session.setLoggedIn(true);

                                FirebaseUser user = auth.getCurrentUser();
                                String uid = user.getUid();
                                editor.putString("uid", uid).commit();

                                getFragmentManager().beginTransaction().replace(R.id.mainPage, new HomePageFragment()).commit();
                                Toast.makeText(getContext(), "Welcome", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                userLogInPass.setText("");
                            }
                        });
                        userLogInEmail.setText("");
                        userLogInPass.setText("");
                    }
                } else {
                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.mainPage, new SignUpFragment()).addToBackStack("").commit();
            }
        });
    }

    public void resetPassword() {
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connectivity.isConnectedToInternet(getContext())) {
                    final AlertDialog.Builder reset = new AlertDialog.Builder(getContext());
                    reset.setCancelable(false);
                    reset.setIcon(R.drawable.user_mail);
                    reset.setTitle("Reset password link will be sent to Your Inbox");
                    reset.setMessage("Enter Your Email Id");
                    final EditText resetEmail = new EditText(getContext());
                    resetEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    resetEmail.setLayoutParams(lp);
                    reset.setView(resetEmail);
                    reset.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            String id = resetEmail.getText().toString();
                            if (id.isEmpty()) {
                                Toast.makeText(getContext(), "Can't be Empty", Toast.LENGTH_LONG).show();
                            } else {
                                auth.sendPasswordResetEmail(id).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "An Email has been sent to you", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                        } else {
                                            Toast.makeText(getContext(), "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    reset.show();
                } else {
                    Toast.makeText(getContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}