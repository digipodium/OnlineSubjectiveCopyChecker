/*
 * Copyright (c) 2021. rogergcc
 */

package com.appsnipp.education.ui.student_fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appsnipp.education.Constants;
import com.appsnipp.education.LoginActivity;
import com.appsnipp.education.R;
import com.appsnipp.education.Utils;
import com.appsnipp.education.databinding.FragmentHomeCoursesBinding;
import com.appsnipp.education.ui.model.Pdf;
import com.appsnipp.education.ui.model.Profile;
import com.appsnipp.education.ui.model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeCoursesFragment extends Fragment {


    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private com.appsnipp.education.databinding.FragmentHomeCoursesBinding bind;
    private String uid;

    public HomeCoursesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_courses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        bind = FragmentHomeCoursesBinding.bind(view);
        uid = auth.getCurrentUser().getUid();
        db.collection(Constants.STUDENTS).document(uid).get().addOnSuccessListener(documentSnapshot -> {
            Student student = documentSnapshot.toObject(Student.class);
            bind.courseName.setText(String.format("%s yr- %s semester", student.course, student.semester));
            bind.rollno.setText(String.format("Roll no : %s", student.roll));
        });
        db.collection(Constants.PROFILE).document(uid).get().addOnSuccessListener(documentSnapshot -> {
            Profile profile = documentSnapshot.toObject(Profile.class);
            bind.textUsername.setText("Welcome, " + profile.username);
        });
        bind.filesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<Pdf> filesList = new ArrayList<>();
        FilesRecyclerAdapter adapter = new FilesRecyclerAdapter(this, R.layout.card_student_files, filesList);
        bind.filesRecycler.setAdapter(adapter);

        db.collection(Constants.FILES).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                Utils.showDialog(getActivity(), "Warning", "No files related to you found ", "ok");
            } else {
                for (QueryDocumentSnapshot item : queryDocumentSnapshots) {
                    filesList.add(item.toObject(Pdf.class));
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> {
            Utils.showDialog(getActivity(), "Error", e.getMessage(), "ok");
        });
        bind.textSignOut.setOnClickListener(view1 -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        });
    }

}