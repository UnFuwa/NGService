package com.unfuwa.ngservice.util.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.extendedmodel.GraphTaskWork;
import com.unfuwa.ngservice.extendedmodel.SubcategoryExtended;
import com.unfuwa.ngservice.model.Service;

import java.util.ArrayList;

public class AdapterListSubcategories extends ArrayAdapter<SubcategoryExtended> {

    private TextView nameSubcategory;
    private TextView countResultsSubcategory;

    private Context context;
    private int resource;
    private ArrayList<SubcategoryExtended> listSubcategories;
    private ArrayList<SubcategoryExtended> tempListSubcategories;
    private ArrayList<SubcategoryExtended> suggestions;

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

    @Nullable
    @Override
    public SubcategoryExtended getItem(int position) {
        return listSubcategories.get(position);
    }

    @Override
    public int getCount() {
        return listSubcategories.size();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {

            private final Object lock = new Object();

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (tempListSubcategories == null) {
                    synchronized (lock) {
                        tempListSubcategories = new ArrayList<>(listSubcategories);
                    }
                }

                if (constraint != null) {
                    suggestions = new ArrayList<>();

                    //String filterPattern = constraint.toString().toLowerCase().trim().substring(0, constraint.toString().indexOf(","));
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (SubcategoryExtended subcategoryExtended : tempListSubcategories) {
                        if (subcategoryExtended.getSubcategory().getName().toLowerCase().contains(filterPattern)) {
                            suggestions.add(subcategoryExtended);
                        }
                    }

                    results.values = suggestions;
                    results.count = suggestions.size();
                } else {
                    synchronized (lock) {
                        results.values = tempListSubcategories;
                        results.count = tempListSubcategories.size();
                    }
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null) {
                    listSubcategories = (ArrayList<SubcategoryExtended>) results.values;
                } else {
                    listSubcategories = null;
                }

                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                    Toast.makeText(context, "По результатам поиска нечего не найдено!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((SubcategoryExtended) resultValue).getSubcategory().getName();
            }
        };
    }
}
