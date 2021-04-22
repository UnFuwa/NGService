package com.unfuwa.ngservice.util.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.extendedmodel.Photo;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AdapterListImages extends ArrayAdapter<Photo> {

    private ImageView imageView;
    private TextView nameImage;
    private TextView sizeImage;

    private Context context;
    private int resource;
    private ArrayList<Photo> listPhotos;

    public AdapterListImages(@NonNull Context context, int resource, ArrayList<Photo> listPhotos) {
        super(context, R.layout.list_images_item, listPhotos);
        this.context = context;
        this.resource = resource;
        this.listPhotos = listPhotos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(resource, parent, false);

        imageView = convertView.findViewById(R.id.image_item);
        nameImage = convertView.findViewById(R.id.name_item);
        sizeImage = convertView.findViewById(R.id.size_item);

        imageView.setImageURI(getItem(position).getUri());
        nameImage.setText(getItem(position).getName());
        sizeImage.setText(Long.toString(getItem(position).getSize()));

        return convertView;
    }
}
