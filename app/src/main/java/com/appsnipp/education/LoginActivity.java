/*
 * Copyright (c) 2021. rogergcc
 */

package com.appsnipp.education;


import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.appsnipp.education.databinding.ActivityLoginBinding;
import com.appsnipp.education.ui.model.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    String[] perms = new String[]{WRITE_EXTERNAL_STORAGE,};
    private ActivityLoginBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private int userInt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        binding.register.btnRegister.setOnClickListener(view -> {
            String email = binding.register.editEmail.getText().toString().trim();
            String mobile = binding.register.editMobile.getText().toString().trim();
            String username = binding.register.editName.getText().toString().trim();
            String password = binding.register.editPassword.getText().toString().trim();
            String usertype = "student";
            if (binding.register.teacher.isChecked()) {
                usertype = "teacher";
                userInt = 0;
            } else if (binding.register.student.isChecked()) {
                usertype = "student";
                userInt = 1;
            }

            String finalUsertype = usertype;
            auth.createUserWithEmailAndPassword(email, password).addOnFailureListener(e -> {
                Utils.showDialog(this, "error", e.getMessage(), "close");
            }).addOnSuccessListener(authResult -> {
                String uid = authResult.getUser().getUid();
                createProfile(new Profile(uid, username, email, mobile, finalUsertype));
            });
        });

        binding.register.textSignIn.setOnClickListener(view -> {
            binding.flipper.setDisplayedChild(1);
        });

        binding.login.textPasswordForgot.setOnClickListener(view -> {
            showForgotDiaglog();
        });
        binding.login.cirLoginButton.setOnClickListener(view -> {
            String email = binding.login.editUserEmail.getText().toString().trim();
            String password = binding.login.editUserPassword.getText().toString().trim();
            String userType = "student";
            if (binding.login.teacher.isChecked()) {
                userType = "teacher";
                userInt = 0;
            } else if (binding.login.student.isChecked()) {
                userType = "student";
                userInt = 1;
            } else if (binding.login.admin.isChecked()) {
                userType = "admin";
                userInt = 2;
            }
            String finalUserType = userType;
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
                String uid = authResult.getUser().getUid();

                if (finalUserType.equals("admin")) {
                    moveToAdminDashboard();
                } else {
                    db.collection(Constants.PROFILE).document(uid).get().addOnSuccessListener(documentSnapshot -> {
                        Profile profile = documentSnapshot.toObject(Profile.class);
                        if (profile.uid.equals(uid)) {
                            if (profile.usertype.equals("teacher") && finalUserType.equals("teacher")) {
                                moveToTeacherDashboard();
                            } else if (profile.usertype.equals("student") && finalUserType.equals("student")) {
                                moveToStudentDashboard();
                            } else {
                                auth.signOut();
                                Utils.showDialog(this, "wrong user type selected", "Your account is not of correct usertype", "ok");
                            }
                        }
                    }).addOnFailureListener(e -> {
                        Utils.showDialog(this, "Invalid credentails", "email and password do not match the database", "retry");
                    });
                }
            }).addOnFailureListener(e -> {
                Utils.showDialog(this, "Invalid credentails", e.getMessage(), "retry");
            });
        });

        binding.login.textViewLogin.setOnClickListener(view -> {
            binding.flipper.setDisplayedChild(0);
        });

        binding.iconQuit.setOnClickListener(view -> {
            finish();
        });

    }

    private void showForgotDiaglog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Have you forgotten your password ");

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.forgot_dialog, null);
        builder.setView(customLayout);


        builder.setPositiveButton("OK", (dialog, which) -> {
            EditText editText = customLayout.findViewById(R.id.editEmail);
            String email = editText.getText().toString();
            auth.sendPasswordResetEmail(email).addOnFailureListener(e -> {
                Utils.showDialog(this, "Error", "Your email is not registered to any account", "ok");
            }).addOnSuccessListener(unused -> {
                Utils.showDialog(this, "Success", "We have sent you an email to reset your password! use a computer to reset your password.", "ok");
            });
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void moveToStudentDashboard() {
        Toast.makeText(this, "loading student page", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, StudentActivity.class);
        startActivity(intent);

        finish();
    }

    private void moveToAdminDashboard() {
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
        finish();
    }

    private void moveToTeacherDashboard() {
        Intent intent = new Intent(this, TeacherActivity.class);
        startActivity(intent);
        finish();
    }

    private void moveToDetail() {
        Intent intent = new Intent(this, ProfileDetailActivity.class);
        intent.putExtra("userType", userInt);
        startActivity(intent);
        finish();
    }

    private void createProfile(Profile profile) {
        db.collection(Constants.PROFILE).document(profile.uid).set(profile).addOnSuccessListener(unused -> {
            moveToDetail();
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            auth.getCurrentUser().delete();
            Utils.showDialog(this, "Error", e.getMessage(), "retry");
        });
    }
}
