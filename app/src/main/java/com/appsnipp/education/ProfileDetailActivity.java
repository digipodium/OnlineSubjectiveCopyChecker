/*
 * Copyright (c) 2021. rogergcc
 */

package com.appsnipp.education;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.appsnipp.education.databinding.ActivityProfileDetailBinding;
import com.appsnipp.education.ui.model.Student;
import com.appsnipp.education.ui.model.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileDetailActivity extends AppCompatActivity {

    private ActivityProfileDetailBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        binding = ActivityProfileDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        uid = auth.getCurrentUser().getUid();
        int userType = getIntent().getIntExtra("userType", 0);
        Toast.makeText(this, String.valueOf(userType), Toast.LENGTH_SHORT).show();
        if (userType == 1) {
            showStudent();
        }
        Toast.makeText(this, "fill in your details", Toast.LENGTH_SHORT).show();
        binding.btnRegister.setOnClickListener(view -> {
            if (userType == 1) {
                saveStudentProfile();
            } else {
                saveTeacherProfile();
            }
        });
    }

    public void saveStudentProfile() {
        int id = binding.gender.getCheckedRadioButtonId();
        String gender = id == R.id.male ? "m" : "f";
        String course = binding.editCourse.getText().toString().trim();
        String semester = binding.editSemester.getText().toString().trim();
        String roll = binding.editRoll.getText().toString().trim();
        Student std = new Student(uid, course, semester, roll, gender);
        db.collection(Constants.STUDENTS).document(uid).set(std).addOnFailureListener(e -> {
            Utils.showDialog(this, "Error occured", e.getMessage(), "retry");
        }).addOnSuccessListener(unused -> {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
            moveToStudentDashboard();
        });
    }

    public void saveTeacherProfile() {
        String subjects = binding.editSubject.getText().toString().trim();
        String dept = binding.editDepartment.getText().toString().trim();
        int id = binding.gender.getCheckedRadioButtonId();
        String gender = id == R.id.male ? "m" : "f";

        Teacher teacher = new Teacher(uid, subjects, dept, gender);
        db.collection(Constants.TEACHERS).document(uid).set(teacher).addOnFailureListener(e -> {
            Utils.showDialog(this, "Error occured", e.getMessage(), "retry");
        }).addOnSuccessListener(unused -> {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
            moveToTeacherDashboard();
        });
    }

    private void moveToTeacherDashboard() {
        Intent intent = new Intent(this, TeacherActivity.class);
        startActivity(intent);
        finish();
    }

    private void moveToStudentDashboard() {
        Intent intent = new Intent(this, StudentActivity.class);
        startActivity(intent);
        finish();
    }

    public void showStudent() {
        binding.editCourseWrap.setVisibility(View.VISIBLE);
        binding.editSemWrap.setVisibility(View.VISIBLE);
        binding.editRollWrap.setVisibility(View.VISIBLE);
        binding.editDeptWrap.setVisibility(View.GONE);
        binding.editSubjectWrap.setVisibility(View.GONE);
    }
}