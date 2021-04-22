package com.unfuwa.ngservice.ui.fragment.specialist;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.dao.NotificationDao;
import com.unfuwa.ngservice.extendedmodel.SpecialistUser;
import com.unfuwa.ngservice.model.Notification;
import com.unfuwa.ngservice.model.Specialist;
import com.unfuwa.ngservice.util.adapter.AdapterNotifications;
import com.unfuwa.ngservice.util.database.DatabaseApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainSpecialistFragment extends Fragment {

    private View view;

    private RecyclerView recyclerView;

    private void initComponents() {
        recyclerView = view.findViewById(R.id.recycler_view_notifications);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_specialist_fragment, container, false);

        initComponents();

        return view;
    }
}