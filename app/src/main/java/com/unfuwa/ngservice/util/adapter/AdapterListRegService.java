package com.unfuwa.ngservice.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.extendedmodel.Photo;
import com.unfuwa.ngservice.extendedmodel.RegServiceExtended;

import java.util.ArrayList;

public class AdapterListRegService extends ArrayAdapter<RegServiceExtended> {

    private EditText fieldIdService;
    private EditText fieldNameService;
    private EditText fieldDescriptionService;
    private EditText fieldPriceService;

    private Context context;
    private int resource;
    private ArrayList<RegServiceExtended> listServices;

    public AdapterListRegService(@NonNull Context context, int resource, ArrayList<RegServiceExtended> listServices) {
        super(context, R.layout.list_reg_services, listServices);
        this.context = context;
        this.resource = resource;
        this.listServices = listServices;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(resource, parent, false);

        fieldIdService = convertView.findViewById(R.id.field_id_service);
        fieldNameService = convertView.findViewById(R.id.field_name_service);
        fieldDescriptionService = convertView.findViewById(R.id.field_description_service);
        fieldPriceService = convertView.findViewById(R.id.field_price_service);

        fieldIdService.setText(Integer.toString(getItem(position).getRegService().getId()));
        fieldNameService.setText(getItem(position).getService().getName());
        if (getItem(position).getRegService().getDescription() != null) {
            fieldDescriptionService.setText(getItem(position).getRegService().getDescription());
        } else {
            fieldDescriptionService.setText("Без описания");
        }
        fieldPriceService.setText(Double.toString(getItem(position).getService().getPrice()) + " руб.");

        return convertView;
    }
}
