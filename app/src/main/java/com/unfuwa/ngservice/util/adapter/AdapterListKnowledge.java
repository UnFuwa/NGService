package com.unfuwa.ngservice.util.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.extendedmodel.Photo;
import com.unfuwa.ngservice.extendedmodel.SubcategoryExtended;
import com.unfuwa.ngservice.model.KnowledgeBase;
import com.unfuwa.ngservice.model.Notification;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AdapterListKnowledge extends RecyclerView.Adapter<AdapterListKnowledge.ViewHolder> implements Filterable {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final FirebaseStorage storage;
    private final StorageReference storageReference;

    private final Context context;
    private static OnItemClickListener onItemClickListener;

    private ArrayList<KnowledgeBase> listKnowledgeBase;
    private ArrayList<KnowledgeBase> tempListKnowledgeBase;
    private ArrayList<KnowledgeBase> suggestions;

    private int i;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView imagePDF;
        private final EditText themePDF;
        private final EditText shortDescriptionPDF;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imagePDF = itemView.findViewById(R.id.image_pdf);
            themePDF = itemView.findViewById(R.id.field_theme_pdf);
            shortDescriptionPDF = itemView.findViewById(R.id.field_short_description_pdf);

            itemView.setOnClickListener(this);
        }

        public ImageView getImagePDF() {
            return imagePDF;
        }

        public EditText getThemePDF() {
            return themePDF;
        }

        public EditText getShortDescriptionPDF() {
            return shortDescriptionPDF;
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                onItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

    public AdapterListKnowledge(Context context, ArrayList<KnowledgeBase> listKnowledgeBase, FirebaseStorage storage, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.listKnowledgeBase = listKnowledgeBase;
        this.storage = storage;
        AdapterListKnowledge.onItemClickListener = onItemClickListener;
        storageReference = storage.getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_knowledge_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterListKnowledge.ViewHolder holder, int position) {
        i = 0;

        Disposable disposable = Flowable.just(listKnowledgeBase.get(position))
                .doOnNext(knowledgeBase -> {
                    StorageReference requestRef = storageReference.child("full_content_knowledgebase/image/" + knowledgeBase.getURLImage());

                    requestRef.getBytes(1024*1024)
                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte [] bytes) {
                                    //progressDialog.dismiss();
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    holder.imagePDF.setImageBitmap(bitmap);
                                    i++;
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //progressDialog.dismiss();
                                    Toast.makeText(context, "Возникла ошибка во время загрузки обложки!", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showMessageSuccessRequest, Throwable::printStackTrace);

        compositeDisposable.add(disposable);

        holder.themePDF.setText(listKnowledgeBase.get(position).getTheme());
        holder.shortDescriptionPDF.setText(listKnowledgeBase.get(position).getShortDescription());
    }

    private void showMessageSuccessRequest(KnowledgeBase knowledgeBase) {
        if (i == listKnowledgeBase.size()) {
            Toast.makeText(context, "Обложки успешно загружены!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return listKnowledgeBase.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            private final Object lock = new Object();

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (tempListKnowledgeBase == null) {
                    synchronized (lock) {
                        tempListKnowledgeBase = new ArrayList<>(listKnowledgeBase);
                    }
                }

                if (constraint != null) {
                    suggestions = new ArrayList<>();

                    //String filterPattern = constraint.toString().toLowerCase().trim().substring(0, constraint.toString().indexOf(","));
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (KnowledgeBase knowledgeBase : tempListKnowledgeBase) {
                        if (knowledgeBase.getTheme().toLowerCase().contains(filterPattern)) {
                            suggestions.add(knowledgeBase);
                        } else if (knowledgeBase.getFullDescription().toLowerCase().contains(filterPattern)) {
                            suggestions.add(knowledgeBase);
                        }
                    }

                    results.values = suggestions;
                    results.count = suggestions.size();
                } else {
                    synchronized (lock) {
                        results.values = tempListKnowledgeBase;
                        results.count = tempListKnowledgeBase.size();
                    }
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null) {
                    listKnowledgeBase = (ArrayList<KnowledgeBase>) results.values;
                } else {
                    listKnowledgeBase = null;
                }

                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "По результатам поиска нечего не найдено!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((KnowledgeBase) resultValue).getTheme();
            }
        };
    }
}
