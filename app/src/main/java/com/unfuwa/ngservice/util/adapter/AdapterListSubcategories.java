package com.unfuwa.ngservice.util.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.extendedmodel.GraphTaskWork;
import com.unfuwa.ngservice.extendedmodel.SubcategoryExtended;

import java.util.ArrayList;

public class AdapterListSubcategories extends ArrayAdapter<SubcategoryExtended> {

    private TextView nameSubcategory;
    private TextView countResultsSubcategory;

    private Context context;
    private int resource;
    private ArrayList<SubcategoryExtended> listSubcategories;

    public AdapterListSubcategories(@NonNull Context context, int resource, ArrayList<SubcategoryExtended> listSubcategories) {
        super(context, R.layout.list_subcategory_item, listSubcategories);
        this.context = context;
        this.resource = resource;
        this.listSubcategories = listSubcategories;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(resource, parent, false);

        nameSubcategory = convertView.findViewById(R.id.name_subcategory);
        countResultsSubcategory = convertView.findViewById(R.id.count_results_subcategory);

        nameSubcategory.setText(getItem(position).getSubcategory().getName());

        if (getItem(position).getListKnowledgeBase().size() != 0) {
            countResultsSubcategory.setText("Результатов: " + Integer.toString(getItem(position).getListKnowledgeBase().size()));
        } else {
            countResultsSubcategory.setText("Результатов: 0");
        }

        return convertView;
    }
}
