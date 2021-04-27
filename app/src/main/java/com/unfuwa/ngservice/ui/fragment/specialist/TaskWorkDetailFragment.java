package com.unfuwa.ngservice.ui.fragment.specialist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.unfuwa.ngservice.R;

public class TaskWorkDetailFragment extends Fragment {

    private View view;

    private TextView idTaskWork;
    private EditText titleTaskWork;
    private EditText dateFromTaskWork;
    private EditText dateToTaskWork;
    private EditText difficultTaskWork;
    private EditText fullDescriptionTaskWork;

    private void initComponents() {
         idTaskWork = view.findViewById(R.id.id_task_work);
         titleTaskWork = view.findViewById(R.id.field_title_task_work);
         dateFromTaskWork = view.findViewById(R.id.field_date_from);
         dateToTaskWork = view.findViewById(R.id.field_date_to);
         difficultTaskWork = view.findViewById(R.id.field_difficult_task_work);
         fullDescriptionTaskWork = view.findViewById(R.id.field_full_description_task_work);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.task_work_detail_fragment, container, false);

        initComponents();

        return view;
    }
}