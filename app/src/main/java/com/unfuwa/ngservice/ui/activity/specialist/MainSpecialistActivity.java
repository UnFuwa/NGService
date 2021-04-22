package com.unfuwa.ngservice.ui.activity.specialist;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.dao.NotificationDao;
import com.unfuwa.ngservice.extendedmodel.SpecialistUser;
import com.unfuwa.ngservice.model.Notification;
import com.unfuwa.ngservice.ui.activity.general.AuthorizationActivity;
import com.unfuwa.ngservice.ui.fragment.specialist.GraphWorkFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.KnowledgeFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.MainSpecialistFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.TasksWorkFragment;
import com.unfuwa.ngservice.util.adapter.AdapterNotifications;
import com.unfuwa.ngservice.util.database.DatabaseApi;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class MainSpecialistActivity extends AppCompatActivity {

    private Thread threadNotification;
    private volatile boolean lifeCycleThreadNotification;

    private SpecialistUser specialist;

    private DatabaseApi dbApi;
    private NotificationDao notificationDao;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private ArrayList<Notification> listNotifications;
    private RecyclerView recyclerView;
    private AdapterNotifications adapterNotifications;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView navigationViewBottom;
    private Toolbar toolbar;
    private TextView fioSpecialist;
    private TextView loginSpecialist;
    private TextView nameFragment;

    private Fragment activeFragment;
    private FragmentManager fragmentManager;
    private MainSpecialistFragment mainSpecialistFragment;
    private KnowledgeFragment knowledgeFragment;
    private TasksWorkFragment tasksWorkFragment;
    private GraphWorkFragment graphWorkFragment;

    private void initComponents() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_main_specialist);
        navigationViewBottom = findViewById(R.id.nav_main_specialist_bottom);
        toolbar = findViewById(R.id.toolbar);
        fioSpecialist = navigationView.getHeaderView(0).findViewById(R.id.fio_specialist);
        loginSpecialist = navigationView.getHeaderView(0).findViewById(R.id.login_specialist);
        nameFragment = findViewById(R.id.specialist_name_fragment);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_specialist_activity);

        dbApi = DatabaseApi.getInstance(getApplicationContext());
        notificationDao = dbApi.notificationDao();

        initComponents();

        if (getIntent().getSerializableExtra("specialist") != null) {
            this.specialist = (SpecialistUser) getIntent().getSerializableExtra("specialist");

            if (specialist.getSpecialist().getOName() != null) {
                fioSpecialist.setText(
                        specialist.getSpecialist().getFName() + " "
                                + specialist.getSpecialist().getIName() + " "
                                + specialist.getSpecialist().getOName());
            } else {
                fioSpecialist.setText(
                        specialist.getSpecialist().getFName() + " "
                                + specialist.getSpecialist().getIName());
            }

            loginSpecialist.setText(specialist.getSpecialist().getLogin());
        }

        mainSpecialistFragment = new MainSpecialistFragment();
        knowledgeFragment = new KnowledgeFragment();
        tasksWorkFragment = new TasksWorkFragment();
        graphWorkFragment = new GraphWorkFragment();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.bringToFront();
        navigationViewBottom.bringToFront();

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, mainSpecialistFragment, "MainSpecialist")
                .add(R.id.fragment_container, knowledgeFragment, "Knowledge")
                .add(R.id.fragment_container, tasksWorkFragment, "TasksWork")
                .add(R.id.fragment_container, graphWorkFragment, "GraphWork")
                .show(mainSpecialistFragment)
                .hide(knowledgeFragment)
                .hide(tasksWorkFragment)
                .hide(graphWorkFragment)
                .commit();

        activeFragment = mainSpecialistFragment;
    }

    @Override
    protected void onStart() {
        super.onStart();

        recyclerView = mainSpecialistFragment.getView().findViewById(R.id.recycler_view_notifications);

        lifeCycleThreadNotification = true;

        threadNotification = new Thread(()->{
            while(lifeCycleThreadNotification) {
                try {
                    listNotifications = new ArrayList<>(notificationDao.getLastNotifications(specialist.getSpecialist().getLogin()));
                    runOnUiThread(this::updateNotifications);

                    Thread.sleep(10000);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

        threadNotification.setDaemon(true);
        threadNotification.start();
    }

    private void updateNotifications(List<Notification> list) {
        this.listNotifications = new ArrayList<>(list);
        adapterNotifications = new AdapterNotifications(getApplicationContext(), listNotifications);
        recyclerView.setAdapter(adapterNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void updateNotifications() {
        adapterNotifications = new AdapterNotifications(getApplicationContext(), listNotifications);
        recyclerView.setAdapter(adapterNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        compositeDisposable.dispose();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void logout(MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        new AlertDialog.Builder(this)
                .setTitle("Подтверждение")
                .setMessage("Вы действительно хотите выйти из аккаунта?")
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        Toast.makeText(getApplicationContext(), "Выход из аккаунта успешно выполнен!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainSpecialistActivity.this, AuthorizationActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    public void changeFragmentHome(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
        } else {
            item.setChecked(false);
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(mainSpecialistFragment)
                .commit();

        activeFragment = mainSpecialistFragment;

        nameFragment.setText("Главная");
    }

    public void changeFragmentKnowledge(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
        } else {
            item.setChecked(false);
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(knowledgeFragment)
                .commit();

        activeFragment = knowledgeFragment;

        nameFragment.setText("Справочник");
    }

    public void changeFragmentTasksWork(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
        } else {
            item.setChecked(false);
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(tasksWorkFragment)
                .commit();

        activeFragment = tasksWorkFragment;

        nameFragment.setText("Рабочие задачи");
    }

    public void changeFragmentGraphWork(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
        } else {
            item.setChecked(false);
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(graphWorkFragment)
                .commit();

        activeFragment = graphWorkFragment;

        nameFragment.setText("График работы");
    }
}