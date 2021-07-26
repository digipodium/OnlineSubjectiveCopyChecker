/*
 * Copyright (c) 2021. rogergcc
 */

package com.appsnipp.education.ui.menucourses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.appsnipp.education.Constants;
import com.appsnipp.education.LoginActivity;
import com.appsnipp.education.R;
import com.appsnipp.education.Utils;
import com.appsnipp.education.databinding.FragmentCoursesStaggedBinding;
import com.appsnipp.education.ui.helpers.GridSpacingItemDecoration;
import com.appsnipp.education.ui.model.Pdf;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class AdminFragment extends Fragment {

    FragmentCoursesStaggedBinding binding;
    private Context context;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String uid;


    public AdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCoursesStaggedBinding.inflate(getLayoutInflater());
        context = this.getContext();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = auth.getCurrentUser().getUid();

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        binding.rvCourses.setLayoutManager(layoutManager);
        binding.rvCourses.setClipToPadding(false);
        binding.rvCourses.setHasFixedSize(true);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.horizontal_card);
        binding.rvCourses.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true, 0));
        List<Pdf> filesList = new ArrayList<>();
        AdminRecyclerAdapter adapter = new AdminRecyclerAdapter(this, R.layout.item_card, filesList);
        binding.rvCourses.setAdapter(adapter);
        db.collection(Constants.FILES).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                Utils.showDialog(getActivity(), "Warning", "You have not uploaded any files yes", "ok");
            } else {
                int size = queryDocumentSnapshots.size();
                for (QueryDocumentSnapshot item : queryDocumentSnapshots) {
                    filesList.add(item.toObject(Pdf.class));
                }
                adapter.notifyDataSetChanged();
            }

        }).addOnFailureListener(e -> {
            Utils.showDialog(getActivity(), "Error", e.getMessage(), "ok");
        });
        binding.textSignOutAdmin.setOnClickListener(view1 -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        });
    }


}