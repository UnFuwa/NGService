package com.unfuwa.ngservice.ui.fragment.client;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.unfuwa.ngservice.R;

public class RequestFragment extends Fragment {

    private View view;

    private TextInputEditText fieldAddress;
    private TextInputEditText fieldDescription;
    private TextInputEditText fieldDateArrive;
    private Button buttonSendRequest;

    private void initComponents() {
        fieldAddress = view.findViewById(R.id.field_address);
        fieldDescription = view.findViewById(R.id.field_description);
        fieldDateArrive = view.findViewById(R.id.field_date_arrive);
        buttonSendRequest = view.findViewById(R.id.button_send_request);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.request_fragment, container, false);
        initComponents();
        return view;
    }
}