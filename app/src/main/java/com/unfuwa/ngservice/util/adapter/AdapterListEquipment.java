package com.unfuwa.ngservice.util.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.extendedmodel.EquipmentClient;
import com.unfuwa.ngservice.model.TaskWork;

import java.io.PipedOutputStream;
import java.util.ArrayList;

public class AdapterListEquipment extends ArrayAdapter<EquipmentClient> {

    private EditText idEquipment;
    private EditText nameEquipment;
    private EditText emailClient;
    private EditText fioClient;

    private Context context;
    private int resource;
    private ArrayList<EquipmentClient> listEquipments;

    public AdapterListEquipment(@NonNull Context context, int resource, ArrayList<EquipmentClient> listEquipments) {
        super(context, R.layout.list_equipment, listEquipments);
        this.context = context;
        this.resource = resource;
        this.listEquipments = listEquipments;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(resource, parent, false);

        idEquipment = convertView.findViewById(R.id.field_id_equipment_list);
        nameEquipment = convertView.findViewById(R.id.field_name_equipment_list);
        emailClient = convertView.findViewById(R.id.field_email_client_list);
        fioClient = convertView.findViewById(R.id.field_fio_client_list);

        idEquipment.setText(Integer.toString(getItem(position).getEquipment().getId()));
        nameEquipment.setText(getItem(position).getEquipment().getName());
        emailClient.setText(getItem(position).getClient().getEmail());
        if (getItem(position).getClient().getOName() != null) {
            fioClient.setText(
                    getItem(position).getClient().getFName() + " "
                    + getItem(position).getClient().getIName() + " "
                    + getItem(position).getClient().getOName());
        } else {
            fioClient.setText(
                    getItem(position).getClient().getFName() + " "
                    + getItem(position).getClient().getIName());
        }

        return convertView;
    }
}
