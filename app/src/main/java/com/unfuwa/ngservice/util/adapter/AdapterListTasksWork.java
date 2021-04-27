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
import com.unfuwa.ngservice.model.TaskWork;

import java.util.ArrayList;

public class AdapterListTasksWork extends ArrayAdapter<TaskWork> {

    private TextView idTaskWorkAdapter;
    private TextView titleTaskWorkAdapter;
    private TextView difficultTaskWorkAdapter;
    private TextView dateFromTaskWorkAdapter;
    private TextView dateToTaskWorkAdapter;
    private TextView fullDescriptionTaskWorkAdapter;

    private Context context;
    private int resource;
    private ArrayList<TaskWork> listTasksWork;

    public AdapterListTasksWork(@NonNull Context context, int resource, ArrayList<TaskWork> listTasksWork) {
        super(context, R.layout.list_tasks_work, listTasksWork);
        this.context = context;
        this.resource = resource;
        this.listTasksWork = listTasksWork;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(resource, parent, false);

        idTaskWorkAdapter = convertView.findViewById(R.id.task_work_number);
        titleTaskWorkAdapter = convertView.findViewById(R.id.title_task_work);
        difficultTaskWorkAdapter = convertView.findViewById(R.id.difficult_task_work);
        dateFromTaskWorkAdapter = convertView.findViewById(R.id.date_from_task_work);
        dateToTaskWorkAdapter = convertView.findViewById(R.id.date_to_task_work);
        fullDescriptionTaskWorkAdapter = convertView.findViewById(R.id.full_description_task);

        idTaskWorkAdapter.setText("Задача № " + Integer.toString(getItem(position).getId()));
        titleTaskWorkAdapter.setText(getItem(position).getTitle());
        difficultTaskWorkAdapter.setText(getItem(position).getDifficult());
        dateFromTaskWorkAdapter.setText(getItem(position).getDateFrom());
        dateToTaskWorkAdapter.setText(getItem(position).getDateTo());
        fullDescriptionTaskWorkAdapter.setText(getItem(position).getFullDescription());

        return convertView;
    }
}
