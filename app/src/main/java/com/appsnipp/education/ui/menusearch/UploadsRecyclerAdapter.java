/*
 * Copyright (c) 2021. rogergcc
 */

package com.appsnipp.education.ui.menusearch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.appsnipp.education.databinding.ItemPopularCourseBinding;
import com.appsnipp.education.ui.model.Pdf;

import java.util.List;

public class UploadsRecyclerAdapter extends RecyclerView.Adapter<UploadsRecyclerAdapter.Holder> {

    private final Fragment fragment;
    private final int layout;
    private final List<Pdf> pdfList;
    private final LayoutInflater inflater;

    public UploadsRecyclerAdapter(Fragment fragment, int layout, List<Pdf> pdfList) {
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

        private final @NonNull
        ItemPopularCourseBinding bind;

        public Holder(@NonNull View itemView) {
            super(itemView);
            bind = ItemPopularCourseBinding.bind(itemView);
        }

        public void bindValues(Pdf pdf) {
            bind.fileName.setText(pdf.name);
            bind.marksGiven.setText("marks" + pdf.marks);
            bind.fileSubject.setText(pdf.subject);
        }
    }
}
