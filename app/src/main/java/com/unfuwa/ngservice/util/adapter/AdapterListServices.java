package com.unfuwa.ngservice.util.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.extendedmodel.RegServiceExtended;
import com.unfuwa.ngservice.model.Service;

import java.util.ArrayList;
import java.util.List;

public class AdapterListServices extends ArrayAdapter<Service> {

    private TextView textView;

    private Context context;
    private int resource;
    private ArrayList<Service> listServices;
    private ArrayList<Service> tempListServices;
    private ArrayList<Service> suggestions;

    public AdapterListServices(@NonNull Context context, int resource, ArrayList<Service> listServices) {
        super(context, R.layout.support_simple_spinner_dropdown_item, listServices);
        this.context = context;
        this.resource = resource;
        this.listServices = listServices;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(resource, parent, false);

        textView = convertView.findViewById(android.R.id.text1);
        textView.setText(getItem(position).getName() + ", " + Double.toString(getItem(position).getPrice()) + " руб.");

        return convertView;
    }

    @Nullable
    @Override
    public Service getItem(int position) {
        return listServices.get(position);
    }

    @Override
    public int getCount() {
        return listServices.size();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {

            private final Object lock = new Object();

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (tempListServices == null) {
                    synchronized (lock) {
                        tempListServices = new ArrayList<>(listServices);
                    }
                }

                if (constraint != null) {
                    suggestions = new ArrayList<>();

                    //String filterPattern = constraint.toString().toLowerCase().trim().substring(0, constraint.toString().indexOf(","));
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (Service service : tempListServices) {
                        if (service.getName().toLowerCase().contains(filterPattern)) {
                            suggestions.add(service);
                        } else {
                            Toast.makeText(context, "По результатам поиска нечего не найдено!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    results.values = suggestions;
                    results.count = suggestions.size();
                } else {
                    synchronized (lock) {
                        results.values = tempListServices;
                        results.count = tempListServices.size();
                    }
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null) {
                    listServices = (ArrayList<Service>) results.values;
                } else {
                    listServices = null;
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
                return ((Service) resultValue).getName();
            }
        };
    }
}
