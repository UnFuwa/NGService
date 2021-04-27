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
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.dao.GraphWorkDao;
import com.unfuwa.ngservice.dao.NotificationDao;
import com.unfuwa.ngservice.dao.TaskWorkDao;
import com.unfuwa.ngservice.extendedmodel.GraphTaskWork;
import com.unfuwa.ngservice.extendedmodel.SpecialistUser;
import com.unfuwa.ngservice.model.Notification;
import com.unfuwa.ngservice.model.TaskWork;
import com.unfuwa.ngservice.ui.activity.general.AuthorizationActivity;
import com.unfuwa.ngservice.ui.fragment.specialist.GraphWorkFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.KnowledgeFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.MainSpecialistFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.ServiceEquipmentFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.TaskWorkDetailFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.TasksWorkFragment;
import com.unfuwa.ngservice.util.adapter.AdapterListTasksWork;
import com.unfuwa.ngservice.util.adapter.AdapterListTasksWorkToday;
import com.unfuwa.ngservice.util.adapter.AdapterNotifications;
import com.unfuwa.ngservice.util.database.DatabaseApi;
import com.unfuwa.ngservice.util.database.DateConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainSpecialistActivity extends AppCompatActivity {

    private Thread threadNotification;
    private volatile boolean lifeCycleThreadNotification;

    private SpecialistUser specialist;

    private DatabaseApi dbApi;
    private NotificationDao notificationDao;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private ArrayList<Notification> listNotifications;
    private RecyclerView recyclerViewNotifications;
    private AdapterNotifications adapterNotifications;
    private ArrayList<GraphTaskWork> listTasksWorkToday;
    private ListView listViewTasksWorkToday;
    private AdapterListTasksWorkToday adapterListTasksWorkToday;
    private int selectTaskWorkToday;

    private TextView idTaskWork;
    private EditText titleTaskWork;
    private EditText dateFromTaskWork;
    private EditText dateToTaskWork;
    private EditText difficultTaskWork;
    private EditText fullDescriptionTaskWork;

    private TaskWork taskWork;

    private ArrayList<TaskWork> listTasksWork;
    private ArrayList<TaskWork> listTasksWorkSeleсted;
    private TextView labelTasksWorkFrom;
    private TextView totalTasksWork;
    private ListView listViewTasksWork;
    private AdapterListTasksWork adapterListTasksWork;

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
    private TaskWorkDetailFragment taskWorkDetailFragment;
    private GraphWorkFragment graphWorkFragment;
    private ServiceEquipmentFragment serviceEquipmentFragment;

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
        taskWorkDetailFragment = new TaskWorkDetailFragment();
        graphWorkFragment = new GraphWorkFragment();
        serviceEquipmentFragment = new ServiceEquipmentFragment();

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
                .add(R.id.fragment_container, taskWorkDetailFragment, "TaskWorkDetail")
                .add(R.id.fragment_container, graphWorkFragment, "GraphWork")
                .add(R.id.fragment_container, serviceEquipmentFragment, "ServiceEquipment")
                .show(mainSpecialistFragment)
                .hide(knowledgeFragment)
                .hide(tasksWorkFragment)
                .hide(taskWorkDetailFragment)
                .hide(graphWorkFragment)
                .hide(serviceEquipmentFragment)
                .commit();

        activeFragment = mainSpecialistFragment;

        selectTaskWorkToday = -1;
        taskWork = null;
    }

    @Override
    protected void onStart() {
        super.onStart();

        recyclerViewNotifications = mainSpecialistFragment.getView().findViewById(R.id.recycler_view_notifications);
        listViewTasksWorkToday = findViewById(R.id.list_tasks_work_today);
        listViewTasksWorkToday.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!view.isSelected()) {
                    view.setSelected(true);
                    selectTaskWorkToday = position;
                    taskWork = adapterListTasksWorkToday.getItem(position).getTaskWork();
                    taskWork.setId(adapterListTasksWorkToday.getItem(position).getGraphWork().getIdTaskWork());
                } else {
                    view.setSelected(false);
                    selectTaskWorkToday = -1;
                    taskWork = null;
                }
            }
        });

        GraphWorkDao graphWorkDao = dbApi.graphWorkDao();

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String formatDate = dateFormat.format(new Date());

        Disposable disposable = graphWorkDao.getTasksWorkTodayBySpecialist(specialist.getSpecialist().getLogin(), formatDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::addTasksWorkToday, throwable -> showMessageErrorTasksWorkToday());

        compositeDisposable.add(disposable);

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

    private void updateNotifications() {
        adapterNotifications = new AdapterNotifications(getApplicationContext(), listNotifications);
        recyclerViewNotifications.setAdapter(adapterNotifications);
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
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

        selectTaskWorkToday = -1;
        taskWork = null;

        recyclerViewNotifications = mainSpecialistFragment.getView().findViewById(R.id.recycler_view_notifications);
        listViewTasksWorkToday = findViewById(R.id.list_tasks_work_today);
        listViewTasksWorkToday.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!view.isSelected()) {
                    view.setSelected(true);
                    selectTaskWorkToday = position;
                    taskWork = adapterListTasksWorkToday.getItem(position).getTaskWork();
                    taskWork.setId(adapterListTasksWorkToday.getItem(position).getGraphWork().getIdTaskWork());
                } else {
                    view.setSelected(false);
                    selectTaskWorkToday = -1;
                    taskWork = null;
                }
            }
        });

        GraphWorkDao graphWorkDao = dbApi.graphWorkDao();

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String formatDate = dateFormat.format(new Date());

        Disposable disposable = graphWorkDao.getTasksWorkTodayBySpecialist(specialist.getSpecialist().getLogin(), formatDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::addTasksWorkToday, throwable -> showMessageErrorTasksWorkToday());

        compositeDisposable.add(disposable);
    }

    private void addTasksWorkToday(List<GraphTaskWork> list) {
        listTasksWorkToday = new ArrayList<>(list);

        adapterListTasksWorkToday = new AdapterListTasksWorkToday(getApplicationContext(), R.layout.list_tasks_work_today, listTasksWorkToday);
        listViewTasksWorkToday.setAdapter(adapterListTasksWorkToday);

        Toast.makeText(getApplicationContext(), "Успешно сформирован список рабочих задач на сегодня!", Toast.LENGTH_SHORT).show();
    }

    private void showMessageErrorTasksWorkToday() {
        Toast.makeText(getApplicationContext(), "Возникла ошибка при формировании списка рабочий задач на сегодня!", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    public void getTaskDetails(View view) {
        drawerLayout.closeDrawer(GravityCompat.START);

        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(taskWorkDetailFragment)
                .commit();

        activeFragment = taskWorkDetailFragment;

        nameFragment.setText("Подробнее об задаче");

        idTaskWork = findViewById(R.id.id_task_work);
        titleTaskWork = findViewById(R.id.field_title_task_work);
        dateFromTaskWork = findViewById(R.id.field_date_from);
        dateToTaskWork = findViewById(R.id.field_date_to);
        difficultTaskWork = findViewById(R.id.field_difficult_task_work);
        fullDescriptionTaskWork = findViewById(R.id.field_full_description_task_work);

        idTaskWork.setText("Задача № " + Integer.toString(taskWork.getId()));
        titleTaskWork.setText(taskWork.getTitle());
        dateFromTaskWork.setText(taskWork.getDateFrom().toString());
        dateToTaskWork.setText(taskWork.getDateTo().toString());
        difficultTaskWork.setText(taskWork.getDifficult());
        fullDescriptionTaskWork.setText(taskWork.getFullDescription());
    }

    public void setStatusCompleteTaskWork(View view) {
        TaskWorkDao taskWorkDao = dbApi.taskWorkDao();

        taskWork.setFlagComplete(true);

        Disposable disposable = taskWorkDao.updateStatus(taskWork)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showMessageCompleteTask, throwable -> showMessageErrorCompleteTask());

        compositeDisposable.add(disposable);
    }

    private void showMessageCompleteTask() {
        Toast.makeText(getApplicationContext(), "Задача была успешно завершена!", Toast.LENGTH_SHORT).show();
    }

    private void showMessageErrorCompleteTask() {
        Toast.makeText(getApplicationContext(), "Возникла ошибка при завершении задачи!", Toast.LENGTH_SHORT).show();
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

        TaskWorkDao taskWorkDao = dbApi.taskWorkDao();

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String formatDate = dateFormat.format(new Date());

        listTasksWorkSeleсted = new ArrayList<>();

        labelTasksWorkFrom = findViewById(R.id.label_tasks_work_from);
        totalTasksWork = findViewById(R.id.total_tasks_work);
        listViewTasksWork = findViewById(R.id.list_tasks_work);
        listViewTasksWork.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!view.isSelected()) {
                    view.setSelected(true);
                    TaskWork taskWork = adapterListTasksWork.getItem(position);
                    taskWork.setFlagComplete(true);
                    listTasksWorkSeleсted.add(taskWork);
                } else {
                    view.setSelected(false);
                    listTasksWorkSeleсted.remove(adapterListTasksWork.getItem(position));
                }
            }
        });

        labelTasksWorkFrom.setText("Список рабочих задач от " + formatDate);

        Disposable disposable = taskWorkDao.getListTasksWork(formatDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::createListTasksWork, throwable -> showMessageErrorListTasksWork());

        compositeDisposable.add(disposable);
    }

    @SuppressLint("SetTextI18n")
    private void createListTasksWork(List<TaskWork> list) {
        listTasksWork = new ArrayList<>(list);

        totalTasksWork.setText("ОБЩЕЕ КОЛИЧЕСТВО: " + listTasksWork.size());

        adapterListTasksWork = new AdapterListTasksWork(getApplicationContext(), R.layout.list_tasks_work, listTasksWork);
        listViewTasksWork.setAdapter(adapterListTasksWork);

        Toast.makeText(getApplicationContext(), "Успешно сформирован список рабочих задач!", Toast.LENGTH_SHORT).show();
    }

    private void showMessageErrorListTasksWork() {
        Toast.makeText(getApplicationContext(), "Возникла ошибка при формировании списка рабочих задач!", Toast.LENGTH_SHORT).show();
    }

    public void setStatusCompleteTasksWork(View view) {
        TaskWorkDao taskWorkDao = dbApi.taskWorkDao();

        Disposable disposable = taskWorkDao.updateStatusList(listTasksWorkSeleсted)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showMessageSuccessStatusListTasksWork, throwable -> showMessageErrorStatusListTasksWork());

        compositeDisposable.add(disposable);
    }

    private void showMessageSuccessStatusListTasksWork() {
        for (TaskWork taskWork : listTasksWorkSeleсted) {
            listTasksWork.remove(taskWork);
        }

        adapterListTasksWork = new AdapterListTasksWork(getApplicationContext(), R.layout.list_tasks_work, listTasksWork);
        listViewTasksWork.setAdapter(adapterListTasksWork);

        Toast.makeText(getApplicationContext(), "Успешно завершены выбранные задачи!", Toast.LENGTH_SHORT).show();
    }

    private void showMessageErrorStatusListTasksWork() {
        Toast.makeText(getApplicationContext(), "Возникла ошибка при завершении выбранных задач!", Toast.LENGTH_SHORT).show();
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

    public void changeFragmentServiceEquipment(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
        } else {
            item.setChecked(false);
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(serviceEquipmentFragment)
                .commit();

        activeFragment = serviceEquipmentFragment;

        nameFragment.setText("Сервисное обслуживание");
    }
}