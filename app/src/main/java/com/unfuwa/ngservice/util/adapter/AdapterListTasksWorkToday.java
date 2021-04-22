package com.unfuwa.ngservice.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.model.TaskWork;

import java.util.ArrayList;

public class AdapterListTasksWorkToday extends ArrayAdapter<TaskWork> {

    private TextView titleTaskWorkToday;
    private TextView shortDescriptionTaskWorkToday;

    private Context context;
    private int resource;
    private ArrayList<TaskWork> listTasksWorkToday;

    public AdapterListTasksWorkToday(@NonNull Context context, int resource, ArrayList<TaskWork> listTasksWorkToday) {
        super(context, R.layout.list_tasks_work_today, listTasksWorkToday);
        this.context = context;
        this.resource = resource;
        this.listTasksWorkToday = listTasksWorkToday;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(resource, parent, false);

        titleTaskWorkToday = convertView.findViewById(R.id.title_task_work_today);
        shortDescriptionTaskWorkToday = convertView.findViewById(R.id.short_description_task_work_today);

        titleTaskWorkToday.setText(getItem(position).getTitle());
        shortDescriptionTaskWorkToday.setText(getItem(position).getShortDescription());

        return convertView;
    }
}
