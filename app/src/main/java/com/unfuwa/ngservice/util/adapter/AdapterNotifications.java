package com.unfuwa.ngservice.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.model.Notification;

import java.util.ArrayList;

public class AdapterNotifications extends RecyclerView.Adapter<AdapterNotifications.ViewHolder> {

    private final Context context;
    private final ArrayList<Notification> listNotifications;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView iconTypeNotification;
        private final TextView titleNotification;
        private final TextView contentNotification;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iconTypeNotification = itemView.findViewById(R.id.image_type_notification);
            titleNotification = itemView.findViewById(R.id.title_notification);
            contentNotification = itemView.findViewById(R.id.content_notification);
        }

        public ImageView getIconTypeNotification() {
            return iconTypeNotification;
        }

        public TextView getTitleNotification() {
            return titleNotification;
        }

        public TextView getContentNotification() {
            return contentNotification;
        }
    }

    public AdapterNotifications(Context context, ArrayList<Notification> listNotifications) {
        this.context = context;
        this.listNotifications = listNotifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_notifications_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterNotifications.ViewHolder holder, int position) {
        switch (listNotifications.get(position).getNameType()) {
            case "Неважно":
                holder.iconTypeNotification.setImageResource(R.drawable.ic_type_notification_no_matter);
                break;
            case "Важно":
                holder.iconTypeNotification.setImageResource(R.drawable.ic_type_notification_matter);
                break;
            case "Очень важно":
                holder.iconTypeNotification.setImageResource(R.drawable.ic_type_notification_very_matter);
                break;
        }

        holder.titleNotification.setText(listNotifications.get(position).getTitle());
        holder.contentNotification.setText(listNotifications.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return listNotifications.size();
    }
}
