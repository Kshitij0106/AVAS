package com.edu.avas;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.avas.Common.Connectivity;
import com.edu.avas.Common.Session;
import com.edu.avas.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private SwipeRefreshLayout swipeLayoutProfile;
    private TextView profileUserName, profileFullName, profilePhone,
            profileEmail, profileDOB, profileAge, profileFather, profileScl, profileCls, profileCity;
    private CircleImageView profilePic;
    private ImageView editFullName, editPhone, editAge, editScl, editCls;
    private SharedPreferences pref;
    private DatabaseReference databaseReference;
    private StorageReference sRef;
    private Uri filepath;
    private ConstraintLayout showProfileConstraintLayout;
    private TextView logInToSeeProfile, goToLogInFromProfile;
    private Session session;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        swipeLayoutProfile = view.findViewById(R.id.swipeLayoutProfile);
        profileUserName = view.findViewById(R.id.profileUserName);
        profilePic = view.findViewById(R.id.profilePic);
        profileFullName = view.findViewById(R.id.profileFullName);
        editFullName = view.findViewById(R.id.editFullName);
        profilePhone = view.findViewById(R.id.profilePhone);
        editPhone = view.findViewById(R.id.editPhone);
        profileEmail = view.findViewById(R.id.profileEmail);
        profileDOB = view.findViewById(R.id.profileDOB);
        profileAge = view.findViewById(R.id.profileAge);
        editAge = view.findViewById(R.id.editAge);
        profileFather = view.findViewById(R.id.profileFather);
        profileScl = view.findViewById(R.id.profileScl);
        editScl = view.findViewById(R.id.editSchool);
        profileCls = view.findViewById(R.id.profileCls);
        editCls = view.findViewById(R.id.editCls);
        profileCity = view.findViewById(R.id.profileCity);
        showProfileConstraintLayout = view.findViewById(R.id.showProfileConstraintLayout);
        logInToSeeProfile = view.findViewById(R.id.logInToSeeProfile);
        goToLogInFromProfile = view.findViewById(R.id.goToLogInFromProfile);
        session = new Session(getContext());

        sRef = FirebaseStorage.getInstance().getReference("ProfilePics");

        swipeLayoutProfile.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkLogInStatus();
            }
        });
        checkLogInStatus();

        return view;
    }

    public void checkLogInStatus() {
        if (session.isLoggedIn()) {
            showProfile();
            editFullName.setOnClickListener(this);
            profilePic.setOnClickListener(this);
            editPhone.setOnClickListener(this);
            editAge.setOnClickListener(this);
            editScl.setOnClickListener(this);
            editCls.setOnClickListener(this);
        } else {
            swipeLayoutProfile.setRefreshing(false);
            showProfileConstraintLayout.setVisibility(View.GONE);
            logInToSeeProfile.setVisibility(View.VISIBLE);
            goToLogInFromProfile.setVisibility(View.VISIBLE);
            goToLogInFromProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getFragmentManager().beginTransaction().replace(R.id.mainPage, new LogInFragment()).addToBackStack(" ").commit();
                }
            });
        }
    }

    public void selectPic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
            Picasso.get().load(filepath).fit().centerCrop().into(profilePic);

            AlertDialog.Builder upload = new AlertDialog.Builder(getActivity());
            upload.setTitle("Upload Pic");
            upload.setCancelable(false);
            upload.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    uploadPic();
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            upload.show();
        } else {
            Toast.makeText(getActivity(), "Try Again", Toast.LENGTH_SHORT).show();
        }
    }

    public String getFileExtension(Uri filepath) {
        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mm = MimeTypeMap.getSingleton();
        return mm.getExtensionFromMimeType(cr.getType(filepath));
    }

    public void uploadPic() {
        if (Connectivity.isConnectedToInternet(getContext())) {
            if (filepath != null) {
                final StorageReference ref = sRef.child(System.currentTimeMillis() + "." + getFileExtension(filepath));
                ref.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri uri = uriTask.getResult();
                        String url = uri.toString();
                        HashMap<String, Object> hm = new HashMap<>();
                        hm.put("userPic", url);

                        databaseReference.updateChildren(hm);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(getContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void showProfile() {
        if (Connectivity.isConnectedToInternet(getContext())) {
            swipeLayoutProfile.setRefreshing(false);

            editFullName.setVisibility(View.VISIBLE);
            editPhone.setVisibility(View.VISIBLE);
            editAge.setVisibility(View.VISIBLE);
            editScl.setVisibility(View.VISIBLE);
            editCls.setVisibility(View.VISIBLE);

            pref = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
            String uid = pref.getString("uid", "");

            databaseReference = FirebaseDatabase.getInstance().getReference("Students").child(uid);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    profileUserName.setText(user.getUserName());
                    profileFullName.setText(user.getUserFullName());
                    profilePhone.setText(user.getUserPhone());
                    profileEmail.setText(user.getUserEmail());
                    profileDOB.setText(user.getUserDOB());
                    profileAge.setText(user.getUserAge());
                    profileFather.setText(user.getUserFathersName());
                    profileScl.setText(user.getUserSchool());
                    profileCls.setText(user.getUserClass());
                    profileCity.setText(user.getUserCity());

                    try {
                        String url = user.getUserPic();
                        if (!url.isEmpty()) {
                            Picasso.get().load(user.getUserPic()).fit().centerCrop().into(profilePic);
                        }
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            editFullName.setVisibility(View.INVISIBLE);
            editPhone.setVisibility(View.INVISIBLE);
            editAge.setVisibility(View.INVISIBLE);
            editScl.setVisibility(View.INVISIBLE);
            editCls.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void showAlertDialog(final String key) {
        AlertDialog.Builder edit = new AlertDialog.Builder(getContext());
        edit.setCancelable(true);
        edit.setTitle("Edit " + key);
        edit.setMessage("Enter new " + key);
        edit.setIcon(R.drawable.ic_edit);
        final EditText change = new EditText(getContext());
        change.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        change.setLayoutParams(lp);
        edit.setView(change);
        edit.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String updatedKey = change.getText().toString();
                if (!updatedKey.isEmpty()) {
                    HashMap<String, Object> hm = new HashMap<>();
                    hm.put(key, updatedKey);

                    databaseReference.updateChildren(hm).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Enter Valid Details", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        edit.show();
    }

    @Override
    public void onClick(View v) {
        if (v == editFullName) {
            showAlertDialog("userFullName");
        } else if (v == editPhone) {
            showAlertDialog("userPhone");
        } else if (v == profilePic) {
            selectPic();
        } else if (v == editAge) {
            showAlertDialog("userAge");
        } else if (v == editScl) {
            showAlertDialog("userSchool");
        } else if (v == editCls) {
            showAlertDialog("userClass");
        }
    }
}