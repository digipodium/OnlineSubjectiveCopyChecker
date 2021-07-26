/*
 * Copyright (c) 2021. rogergcc
 */

package com.appsnipp.education.ui.student_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.appsnipp.education.Constants;
import com.appsnipp.education.R;
import com.appsnipp.education.Utils;
import com.appsnipp.education.databinding.FragmentFileViewBinding;
import com.appsnipp.education.ui.model.Pdf;
import com.appsnipp.education.ui.model.Request;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class FileViewFragment extends Fragment {

    private FragmentFileViewBinding bind;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind = FragmentFileViewBinding.bind(view);
        Pdf pdf = FileViewFragmentArgs.fromBundle(getArguments()).getPdf();
        bind.fileSubject.setText(pdf.subject);
        bind.fileTeacher.setText(pdf.teacher);
        bind.fileMarks.setText("Marks:" + pdf.marks);
        bind.btnSend.setOnClickListener(view1 -> {
            String msg = bind.requestMsg.getText().toString().trim();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(Constants.REQUESTS).add(new Request(FirebaseAuth.getInstance().getCurrentUser().getUid(), pdf.name, msg)).addOnSuccessListener(documentReference -> {
                Utils.showDialog(getActivity(), "Success", "your request is sent to the admin", "ok");
                NavHostFragment.findNavController(this).navigate(R.id.action_fileViewFragment_to_studenthome);
            }).addOnFailureListener(e -> {
                Utils.showDialog(getActivity(), "Failure", "could not send the request " + e.getMessage(), "retry");
            });
        });
    }
}