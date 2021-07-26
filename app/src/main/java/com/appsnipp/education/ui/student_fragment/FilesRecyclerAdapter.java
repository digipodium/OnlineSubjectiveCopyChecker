/*
 * Copyright (c) 2021. rogergcc
 */

package com.appsnipp.education.ui.student_fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.appsnipp.education.databinding.CardStudentFilesBinding;
import com.appsnipp.education.ui.model.Pdf;

import java.util.List;

public class FilesRecyclerAdapter extends RecyclerView.Adapter<FilesRecyclerAdapter.Holder> {

    private final Fragment fragment;
    private final int layout;
    private final List<Pdf> pdfList;
    private final LayoutInflater inflater;

    public FilesRecyclerAdapter(Fragment fragment, int layout, List<Pdf> pdfList) {
        this.fragment = fragment;
        this.layout = layout;
        this.pdfList = pdfList;
        inflater = LayoutInflater.from(fragment.getContext());
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bindValues(pdfList.get(position));
    }

    @Override
    public int getItemCount() {
        return pdfList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private final CardStudentFilesBinding bind;

        public Holder(@NonNull View itemView) {
            super(itemView);
            bind = CardStudentFilesBinding.bind(itemView);
            bind.getRoot().setOnClickListener(view -> {
                HomeCoursesFragmentDirections.ActionStudenthomeToFileViewFragment dir = HomeCoursesFragmentDirections.actionStudenthomeToFileViewFragment(pdfList.get(getAdapterPosition()));
                NavHostFragment.findNavController(fragment).navigate(dir);
            });
        }

        public void bindValues(Pdf pdf) {
            bind.fileMarks.setText("Marks: " + pdf.marks);
            bind.fileSubject.setText("Subject: " + pdf.subject);
            bind.fileTeacher.setText("Teacher: " + pdf.teacher);
        }
    }
}
