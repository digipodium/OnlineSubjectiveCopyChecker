/*
 * Copyright (c) 2021. rogergcc
 */

package com.appsnipp.education.ui.menusearch;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appsnipp.education.Constants;
import com.appsnipp.education.LoginActivity;
import com.appsnipp.education.R;
import com.appsnipp.education.Utils;
import com.appsnipp.education.databinding.FragmentMatchesCoursesBinding;
import com.appsnipp.education.ui.model.Pdf;
import com.appsnipp.education.ui.model.Profile;
import com.appsnipp.education.ui.model.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TeacherUploadFragment extends Fragment {

    static final int REQUEST_IMAGE_GET = 1;
    private static final String TAG = "MatchesCoursesFragment";
    FragmentMatchesCoursesBinding bind;
    Context context;
    private List<Pdf> data;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String uid;
    private Uri fileUri;
    private FirebaseStorage store;
    private Teacher teacher;
    private Profile profile;

    public TeacherUploadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentMatchesCoursesBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        context = this.getContext();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = auth.getCurrentUser().getUid();
        db.collection(Constants.TEACHERS).document(uid).get().addOnSuccessListener(documentSnapshot -> {
            teacher = documentSnapshot.toObject(Teacher.class);
        });
        db.collection(Constants.PROFILE).document(uid).get().addOnSuccessListener(documentSnapshot -> {
            profile = documentSnapshot.toObject(Profile.class);
            bind.textInfo.setText("Welcome, " + profile.username);
        });
        bind.uploadRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<Pdf> filesList = new ArrayList<>();
        UploadsRecyclerAdapter adapter = new UploadsRecyclerAdapter(this, R.layout.item_popular_course, filesList);
        bind.uploadRecycler.setAdapter(adapter);

        db.collection(Constants.FILES).whereEqualTo("tid", uid).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                Utils.showDialog(getActivity(), "Warning", "You have not uploaded any files yes", "ok");
            } else {
                int size = queryDocumentSnapshots.size();
                bind.filesUploaded.setText("Files Uploaded:" + size);
                for (QueryDocumentSnapshot item : queryDocumentSnapshots) {
                    filesList.add(item.toObject(Pdf.class));
                }
                adapter.notifyDataSetChanged();
            }

        }).addOnFailureListener(e -> {
            Utils.showDialog(getActivity(), "Error", e.getMessage(), "ok");
        });
        bind.textSignOutTeacher.setOnClickListener(view1 -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        });

        bind.uploadArea.upload.setOnClickListener(view1 -> {
            selectPdf();
        });
        bind.uploadArea.uploadtext.setOnClickListener(view1 -> {
            selectPdf();
        });
        bind.uploadArea.btnUpload.setOnClickListener(view1 -> {
            if (fileUri != null) {
                store = FirebaseStorage.getInstance();
                String filename = fileUri.getLastPathSegment();
                StorageReference fileStore = null;
                if (filename != null) {
                    fileStore = store.getReference(Constants.FILES).child(filename);

                    StorageReference finalFileStore = fileStore;
                    fileStore.putFile(fileUri).addOnSuccessListener(taskSnapshot -> {
                        finalFileStore.getDownloadUrl().addOnSuccessListener(uri -> {
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            Date date = new Date();
                            String subject = bind.uploadArea.editSubjectName.getText().toString().toLowerCase();
                            String roll = bind.uploadArea.editRollNo.getText().toString().toLowerCase();
                            if (subject.length() == 0) {
                                subject = "N/A";
                            }
                            Pdf data = new Pdf(uri.toString(), filename, uid, profile.username, formatter.format(date), "N/A", subject, roll, false);
                            db.collection(Constants.FILES).add(data).addOnSuccessListener(documentReference -> {
                                Utils.showDialog(getActivity(), "Success", "file successfully uploaded", "ok");
                            }).addOnFailureListener(e -> {
                                Utils.showDialog(getActivity(), "Error", e.getMessage(), "retry");
                            });
                        }).addOnFailureListener(e -> {
                            Utils.showDialog(getActivity(), "Error", e.getMessage(), "retry");
                        });
                    }).addOnFailureListener(e -> {
                        Utils.showDialog(getActivity(), "Error", e.getMessage(), "ok");
                    }).addOnProgressListener(snapshot -> {
                        long totalByteCount = snapshot.getTotalByteCount();
                        long bytesTransferred = snapshot.getBytesTransferred();
                        bind.pb.setMax(100);
                        try {
                            bind.pb.setProgress((int) (totalByteCount / bytesTransferred));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    Toast.makeText(context, "filename is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void selectPdf() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        } else {
            Toast.makeText(context, "no files app found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            fileUri = data.getData();
            bind.uploadArea.uploadtext.setText("Files selected, click button to upload");
        }
    }
}