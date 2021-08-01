/*
 * Copyright (c) 2021. rogergcc
 */

package com.appsnipp.education.ui.menucourses;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.appsnipp.education.Constants;
import com.appsnipp.education.R;
import com.appsnipp.education.Utils;
import com.appsnipp.education.databinding.FragmentAdminViewBinding;
import com.appsnipp.education.ui.model.Pdf;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class AdminViewFragment extends Fragment {


    private FragmentAdminViewBinding bind;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind = FragmentAdminViewBinding.bind(view);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = auth.getCurrentUser().getUid();

        Pdf pdf = AdminViewFragmentArgs.fromBundle(getArguments()).getPdf();
        bind.FileDateAdmin.setText(pdf.date);
        bind.FileSubjectAdmin.setText(pdf.subject);
        bind.FileTeacherAdmin.setText(pdf.teacher);
        bind.fileNameAdmin.setText(pdf.name);
        try {
            bind.sliderMark.setProgress(Integer.parseInt(pdf.marks));
            bind.textMark.setText(Integer.parseInt(pdf.marks));
        } catch (Exception e) {
            bind.sliderMark.setProgress(0);
            bind.textMark.setText(pdf.marks);
        }
        try {
            new RetrivePDFfromUrl().execute(pdf.url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        bind.sliderMark.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                bind.textMark.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        bind.btnMarks.setOnClickListener(view1 -> {
            String marks = bind.textMark.getText().toString().trim();
            db.collection(Constants.FILES).whereEqualTo("name", pdf.name).whereEqualTo("url", pdf.url).get().addOnFailureListener(e -> {
                Utils.showDialog(getActivity(), "Error", "error occurred while uploading files", "ok");
            }).addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots.size() > 0) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    String id = documentSnapshot.getId();
                    Map<String, Object> data = new HashMap<>();
                    data.put("marks", marks);
                    data.put("ischecked", true);
                    db.collection(Constants.FILES).document(id).update(data).addOnSuccessListener(unused -> {
                        Utils.showDialog(getActivity(), "Success", "marks updated successfully", "ok");
                    });

                } else {
                    Utils.showDialog(getActivity(), "Error", "error finding the file", "ok");
                }
            });
        });
    }

    class RetrivePDFfromUrl extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            // we are using inputstream
            // for getting out PDF.
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                // below is the step where we are
                // creating our connection.
                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    // response is success.
                    // we are getting input stream from url
                    // and storing it in our variable.
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }

            } catch (IOException e) {
                // this is the method
                // to handle errors.
                e.printStackTrace();
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            // after the execution of our async
            // task we are loading our pdf in our pdf view.
            bind.pdfView.fromStream(inputStream).load();
        }
    }
}