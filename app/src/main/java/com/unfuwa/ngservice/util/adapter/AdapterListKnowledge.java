package com.unfuwa.ngservice.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.model.KnowledgeBase;
import com.unfuwa.ngservice.model.Notification;

import java.util.ArrayList;

public class AdapterListKnowledge extends RecyclerView.Adapter<AdapterListKnowledge.ViewHolder> {

    private final Context context;
    private final ArrayList<KnowledgeBase> listKnowledgeBase;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imagePDF;
        private final EditText themePDF;
        private final EditText shortDescriptionPDF;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imagePDF = itemView.findViewById(R.id.image_pdf);
            themePDF = itemView.findViewById(R.id.field_theme_pdf);
            shortDescriptionPDF = itemView.findViewById(R.id.field_short_description_pdf);
        }

        public ImageView getImagePDF() {
            return imagePDF;
        }

        public EditText getThemePDF() {
            return themePDF;
        }

        public EditText getShortDescriptionPDF() {
            return shortDescriptionPDF;
        }
    }

    public AdapterListKnowledge(Context context, ArrayList<KnowledgeBase> listKnowledgeBase) {
        this.context = context;
        this.listKnowledgeBase = listKnowledgeBase;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_knowledge_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterListKnowledge.ViewHolder holder, int position) {
        holder.themePDF.setText(listKnowledgeBase.get(position).getTheme());
        holder.shortDescriptionPDF.setText(listKnowledgeBase.get(position).getShortDescription());
    }

    @Override
    public int getItemCount() {
        return listKnowledgeBase.size();
    }
}
