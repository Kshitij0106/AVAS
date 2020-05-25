package com.edu.avas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.edu.avas.Common.Connectivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ApplicationForm extends Fragment {
    private SwipeRefreshLayout swipeLayoutApplicationForm;
    private EditText formPhone, formFatherName, formAge, formSpouseName, formDOB, formScl, formCls, formCity, formReference;
    private RadioGroup radioGroupStudy, radioGroupMarriage;
    private RadioButton sRButton, uRButton, sId, mId;
    private DatabaseReference dRef;
    private FirebaseAuth auth;
    private Button submit;

    public ApplicationForm() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_application_form, container, false);

        Bundle bundle = this.getArguments();
        final String Cname = bundle.getString("Cname");

        swipeLayoutApplicationForm = view.findViewById(R.id.swipeLayoutApplicationForm);
        formPhone = view.findViewById(R.id.formPhone);
        formFatherName = view.findViewById(R.id.formFatherName);
        formAge = view.findViewById(R.id.formAge);
        formSpouseName = view.findViewById(R.id.formSpouseName);
        formDOB = view.findViewById(R.id.formDOB);
        formScl = view.findViewById(R.id.formScl);
        formCls = view.findViewById(R.id.formCls);
        formCity = view.findViewById(R.id.formCity);
        formReference = view.findViewById(R.id.formReference);
        sRButton = view.findViewById(R.id.sRadioButton);
        uRButton = view.findViewById(R.id.uRadioButton);
        radioGroupStudy = view.findViewById(R.id.radioGroupStudy);
        radioGroupMarriage = view.findViewById(R.id.radioGroupMarriage);
        submit = view.findViewById(R.id.submit);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String uid = user.getUid();

        dRef = FirebaseDatabase.getInstance().getReference("Students").child(uid);

        sRButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    formScl.setEnabled(true);
                    formCls.setEnabled(true);
                } else {
                    formScl.setEnabled(false);
                    formCls.setEnabled(false);
                }
            }
        });

        uRButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    formSpouseName.setEnabled(false);
                } else {
                    formSpouseName.setEnabled(true);
                }
            }
        });

        swipeLayoutApplicationForm.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                submitForm(view, Cname);
            }
        });
        submitForm(view, Cname);

        return view;
    }

    public void submitForm(final View view, final String cName) {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connectivity.isConnectedToInternet(getContext())) {
                    swipeLayoutApplicationForm.setRefreshing(false);
                    String fPhn = formPhone.getText().toString();
                    String fFatherName = formFatherName.getText().toString();
                    String fAge = formAge.getText().toString();
                    String fSpouseName = formSpouseName.getText().toString();
                    String fDOB = formDOB.getText().toString();
                    String fScl = formScl.getText().toString();
                    String fCls = formCls.getText().toString();
                    String fCity = formCity.getText().toString();
                    String fRef = formReference.getText().toString();

                    if (fPhn.isEmpty() && fFatherName.isEmpty() && fAge.isEmpty() && fDOB.isEmpty() && fCity.isEmpty() && fRef.isEmpty()) {
                        formPhone.setError("Enter Valid Details");
                        formFatherName.setError("Enter Valid Details");
                        formAge.setError("Enter Valid Details");
                        formDOB.setError("Enter Valid Details");
                        formCity.setError("Enter Valid Details");
                        formReference.setError("Enter Valid Details");
                    } else if (fPhn.isEmpty()) {
                        formPhone.setError("Enter Valid Details");
                    } else if (fFatherName.isEmpty()) {
                        formFatherName.setError("Enter Valid Details");
                    } else if (fAge.isEmpty()) {
                        formAge.setError("Enter Valid Details");
                    } else if (fDOB.isEmpty()) {
                        formDOB.setError("Enter Valid Details");
                    } else if (fCity.isEmpty()) {
                        formCity.setError("Enter Valid Details");
                    } else if (fRef.isEmpty()) {
                        formReference.setError("Enter Valid Details");
                    } else if (radioGroupStudy.getCheckedRadioButtonId() == -1) {
                        Toast.makeText(getActivity(), "Select One", Toast.LENGTH_SHORT).show();
                    } else if (radioGroupMarriage.getCheckedRadioButtonId() == -1) {
                        Toast.makeText(getActivity(), "Select One", Toast.LENGTH_SHORT).show();
                    } else if (formScl.isEnabled() && fScl.isEmpty()) {
                        formScl.setError("Enter Valid Details");
                    } else if (formCls.isEnabled() && fCls.isEmpty()) {
                        formCls.setError("Enter Valid Details");
                    } else if (formSpouseName.isEnabled() && fSpouseName.isEmpty()) {
                        formSpouseName.setError("Enter Valid Details");
                    } else {
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("userPhone", fPhn);
                        data.put("userFathersName", fFatherName);
                        data.put("userAge", fAge);
                        data.put("userDOB", fDOB);
                        data.put("userCity", fCity);
                        data.put("userReference", fRef);

                        int studyStatus = radioGroupStudy.getCheckedRadioButtonId();
                        sId = view.findViewById(studyStatus);
                        String s = sId.getText().toString();
                        if (s.equals("Studying")) {
                            data.put("userClass", fCls);
                            data.put("userSchool", fScl);
                        }
                        int martialStatus = radioGroupMarriage.getCheckedRadioButtonId();
                        mId = view.findViewById(martialStatus);
                        String m = mId.getText().toString();
                        if (m.equals("Married")) {
                            data.put("userSpouse", fSpouseName);
                        }
                        String status = "filled";
                        data.put("userFormStatus", status);
                        dRef.updateChildren(data);

                        Bundle courseNameBundle = new Bundle();
                        courseNameBundle.putString("Cname", cName);
                        CoursesTopicsFragment ctf = new CoursesTopicsFragment();
                        ctf.setArguments(courseNameBundle);
                        getFragmentManager().beginTransaction().replace(R.id.mainPage, ctf).commit();
                    }
                } else {
                    Toast.makeText(getContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}