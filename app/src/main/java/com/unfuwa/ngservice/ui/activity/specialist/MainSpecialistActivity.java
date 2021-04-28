package com.unfuwa.ngservice.ui.activity.specialist;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.jakewharton.rxbinding4.widget.RxTextView;
import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.dao.ClientDao;
import com.unfuwa.ngservice.dao.EquipmentDao;
import com.unfuwa.ngservice.dao.GraphWorkDao;
import com.unfuwa.ngservice.dao.NotificationDao;
import com.unfuwa.ngservice.dao.TaskWorkDao;
import com.unfuwa.ngservice.dao.TypeEquipmentDao;
import com.unfuwa.ngservice.extendedmodel.ClientUser;
import com.unfuwa.ngservice.extendedmodel.EquipmentClient;
import com.unfuwa.ngservice.extendedmodel.GraphTaskWork;
import com.unfuwa.ngservice.extendedmodel.RegServiceExtended;
import com.unfuwa.ngservice.extendedmodel.SpecialistUser;
import com.unfuwa.ngservice.model.Client;
import com.unfuwa.ngservice.model.Equipment;
import com.unfuwa.ngservice.model.Notification;
import com.unfuwa.ngservice.model.Service;
import com.unfuwa.ngservice.model.TaskWork;
import com.unfuwa.ngservice.model.TypeEquipment;
import com.unfuwa.ngservice.ui.activity.general.AuthorizationActivity;
import com.unfuwa.ngservice.ui.fragment.specialist.AddEquipmentFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.EquipmentDetailFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.GraphWorkFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.KnowledgeFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.MainSpecialistFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.ServiceEquipmentFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.TaskWorkDetailFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.TasksWorkFragment;
import com.unfuwa.ngservice.util.adapter.AdapterListEquipment;
import com.unfuwa.ngservice.util.adapter.AdapterListRegService;
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
import io.reactivex.rxjava3.core.Observable;
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

    private ArrayList<EquipmentClient> listEquipments;
    private AdapterListEquipment adapterListEquipment;
    private ListView listViewEquipments;
    private TextView totalEquipments;

    private TextView idEquipmentDetail;
    private EditText fieldEmailClientDetail;
    private EditText fieldTypeEquipmentDetail;
    private EditText fieldNameEquipmentDetail;
    private EditText fieldCharactersEquipmentDetail;
    private EditText fieldDescriptionProblemDetail;
    private EquipmentClient equipmentClient;

    private ArrayList<Service> listServices;
    private ArrayList<RegServiceExtended> listRegServices;
    private AdapterListRegService adapterListRegServices;
    private ListView listViewRegService;

    private ArrayList<String> listTypesEquipment;
    private EditText fieldEmailClient;
    private AutoCompleteTextView fieldTypeEquipment;
    private ImageView iconDownListTypesEquipment;
    private EditText fieldNameEquipment;
    private EditText fieldCharactersEquipment;
    private EditText fieldDescriptionProblem;
    private Button buttonAddEquipment;

    private Observable<Boolean> validFieldsAddEquipment;

    private Integer isExistEmailClient;
    private boolean isValidEmailClient;
    private boolean isValidTypeEquipment;
    private boolean isValidNameEquipment;
    private boolean isValidCharactersEquipment;
    private boolean isValidDescriptionProblem;

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
    private EquipmentDetailFragment equipmentDetailFragment;
    private AddEquipmentFragment addEquipmentFragment;

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
        equipmentDetailFragment = new EquipmentDetailFragment();
        addEquipmentFragment = new AddEquipmentFragment();

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
                .add(R.id.fragment_container, equipmentDetailFragment, "EquipmentDetail")
                .add(R.id.fragment_container, addEquipmentFragment, "AddEquipment")
                .show(mainSpecialistFragment)
                .hide(knowledgeFragment)
                .hide(tasksWorkFragment)
                .hide(taskWorkDetailFragment)
                .hide(graphWorkFragment)
                .hide(serviceEquipmentFragment)
                .hide(equipmentDetailFragment)
                .hide(addEquipmentFragment)
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

                    Thread.sleep(20000);
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
        nameFragment.setTextSize(18);

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
        nameFragment.setTextSize(18);

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
        nameFragment.setTextSize(18);

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
        nameFragment.setTextSize(18);
    }

    public void changeFragmentServiceEquipment(MenuItem item) {
        EquipmentDao equipmentDao = dbApi.equipmentDao();

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

        nameFragment.setText("Сервис");
        nameFragment.setTextSize(18);

        listViewEquipments = findViewById(R.id.list_service_equipment);
        totalEquipments = findViewById(R.id.total_equipments);

        Disposable disposable = equipmentDao.getListEquipment()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::createListEquipments, throwable -> showMessageErrorListEquipments());

        compositeDisposable.add(disposable);
    }

    @SuppressLint("SetTextI18n")
    private void createListEquipments(List<EquipmentClient> list) {
        listEquipments = new ArrayList<>(list);

        totalEquipments.setText("ОБЩЕЕ КОЛИЧЕСТВО: " + listEquipments.size());

        adapterListEquipment = new AdapterListEquipment(getApplicationContext(), R.layout.list_equipment, listEquipments);
        listViewEquipments.setAdapter(adapterListEquipment);
        listViewEquipments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drawerLayout.closeDrawer(GravityCompat.START);

                fragmentManager.beginTransaction()
                        .hide(activeFragment)
                        .show(equipmentDetailFragment)
                        .commit();

                activeFragment = equipmentDetailFragment;

                nameFragment.setText("Подробнее об оборудовании");
                nameFragment.setTextSize(15);

                idEquipmentDetail = findViewById(R.id.id_equipment_detail);
                fieldEmailClientDetail = findViewById(R.id.field_email_client_detail);
                fieldTypeEquipmentDetail = findViewById(R.id.field_type_equipment_detail);
                fieldNameEquipmentDetail = findViewById(R.id.field_name_equipment_detail);
                fieldCharactersEquipmentDetail = findViewById(R.id.field_characters_detail);
                fieldDescriptionProblemDetail = findViewById(R.id.field_description_problem_detail);

                equipmentClient = adapterListEquipment.getItem(position);

                idEquipmentDetail.setText(Integer.toString(equipmentClient.getEquipment().getId()));
                fieldEmailClientDetail.setText(equipmentClient.getClient().getEmail());
                fieldTypeEquipmentDetail.setText(equipmentClient.getEquipment().getNameType());
                fieldNameEquipmentDetail.setText(equipmentClient.getEquipment().getName());
                fieldCharactersEquipmentDetail.setText(equipmentClient.getEquipment().getCharacters());
                fieldDescriptionProblemDetail.setText(equipmentClient.getEquipment().getDescriptionProblem());
            }
        });

        Toast.makeText(getApplicationContext(), "Успешно сформирован список оборудования на учете!", Toast.LENGTH_SHORT).show();
    }

    public void setStatusRepairCompleteEquipment(View view) {
        EquipmentDao equipmentDao = dbApi.equipmentDao();

        equipmentClient.getEquipment().setStatusRepair(true);

        Disposable disposable = equipmentDao.update(equipmentClient.getEquipment())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showMessageCompleteRepairEquipment, throwable -> showMessageErrorCompleteRepairEquipment());

        compositeDisposable.add(disposable);
    }

    private void showMessageCompleteRepairEquipment() {
        Toast.makeText(getApplicationContext(), "Ремонт обрудования был успешно завершен!", Toast.LENGTH_SHORT).show();
    }

    private void showMessageErrorCompleteRepairEquipment() {
        Toast.makeText(getApplicationContext(), "Возникла ошибка при завершении ремонта оборудования!", Toast.LENGTH_SHORT).show();
    }

    public void updateDescriptionProblemEquipment(View view) {
    }

    public void getListRegServiceByEquipment(View view) {
    }

    private void showMessageErrorListEquipments() {
        Toast.makeText(getApplicationContext(), "Возникла ошибка при формировании списка оборудования на учете!", Toast.LENGTH_SHORT).show();
    }

    public void putEquipmentOnRecord(View view) {
        TypeEquipmentDao typeEquipmentDao = dbApi.typeEquipmentDao();
        ClientDao clientDao = dbApi.clientDao();

        drawerLayout.closeDrawer(GravityCompat.START);

        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(addEquipmentFragment)
                .commit();

        activeFragment = addEquipmentFragment;

        nameFragment.setText("Поставка на учет");
        nameFragment.setTextSize(18);

        fieldEmailClient = findViewById(R.id.field_email_client);
        fieldTypeEquipment = findViewById(R.id.field_type_equipment);
        iconDownListTypesEquipment = findViewById(R.id.ic_down_list);
        fieldNameEquipment = findViewById(R.id.field_name_equipment);
        fieldCharactersEquipment = findViewById(R.id.field_characters);
        fieldDescriptionProblem = findViewById(R.id.field_description_problem);
        buttonAddEquipment = findViewById(R.id.button_add_equipment);

        isExistEmailClient = 0;

        Observable<String> emailClientField = RxTextView.textChanges(fieldEmailClient)
                .skip(1)
                .map(CharSequence::toString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged();

        Observable<Integer> isExistEmail = RxTextView.textChanges(fieldEmailClient)
                .skip(1)
                .map(CharSequence::toString)
                .flatMap(string -> clientDao.hasClientByEmail(fieldEmailClient.getText().toString())
                        .toObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged();

        Observable<String> typeEquipmentField = RxTextView.textChanges(fieldTypeEquipment)
                .skip(1)
                .map(CharSequence::toString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged();

        Observable<String> nameEquipmentField = RxTextView.textChanges(fieldNameEquipment)
                .skip(1)
                .map(CharSequence::toString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged();

        Observable<String> charactersEquipmentField = RxTextView.textChanges(fieldCharactersEquipment)
                .skip(1)
                .map(CharSequence::toString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged();

        Observable<String> descriptionProblemField = RxTextView.textChanges(fieldDescriptionProblem)
                .skip(1)
                .map(CharSequence::toString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged();

        Disposable disposable = validFieldsAddEquipment.combineLatest(emailClientField, typeEquipmentField, nameEquipmentField, charactersEquipmentField, descriptionProblemField, isExistEmail, this::isValidationFieldsAddEquipment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::enabledAddEquipment);

        Disposable disposable1 = typeEquipmentDao.getTypesEquipment()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::createListTypesEquipment, throwable -> showMessageErrorListTypesEquipment());

        compositeDisposable.add(disposable);
        compositeDisposable.add(disposable1);
    }

    private void createListTypesEquipment(List<TypeEquipment> list) {
        listTypesEquipment = new ArrayList<>();

        for (TypeEquipment typeEquipment : list) {
            listTypesEquipment.add(typeEquipment.getName());
        }

        ArrayAdapter<String> adapterTypesEquipment = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, listTypesEquipment);
        fieldTypeEquipment.setAdapter(adapterTypesEquipment);

        iconDownListTypesEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldTypeEquipment.showDropDown();
            }
        });

        Toast.makeText(getApplicationContext(), "Успешно сформирован список типов оборудования!", Toast.LENGTH_SHORT).show();
    }

    private void showMessageErrorListTypesEquipment() {
        Toast.makeText(getApplicationContext(), "Возникла ошибка при формировании списка типов оборудования!", Toast.LENGTH_SHORT).show();
    }

    private void enabledAddEquipment(boolean validFields) {
        if (validFields) {
            buttonAddEquipment.setEnabled(true);
        } else {
            buttonAddEquipment.setEnabled(false);
        }
    }

    private boolean isValidationFieldsAddEquipment(String emailClientField, String typeEquipmentField, String nameEquipmentField, String charactersEquipmentField, String descriptionProblemField, int isExistEmail) {
        if (emailClientField.isEmpty()) {
            fieldEmailClient.setError("Вы не ввели значение эл.почты!");
            isValidEmailClient = false;
        } else if (isExistEmail != 1) {
            fieldEmailClient.setError("Вы ввели несуществующий адрес эл. почты!");
            isValidEmailClient = false;
        } else if (!emailClientField.contains("@")) {
            fieldEmailClient.setError("Вы ввели неверное значение эл.почты! Отсутствует '@'.");
            isValidEmailClient = false;
        } else if (emailClientField.length() < 4) {
            fieldEmailClient.setError("Имя почтового ящика должно состоять из 4 или более символов!");
            isValidEmailClient = false;
        } else if (emailClientField.length() > 45) {
            fieldEmailClient.setError("Эл.почта не должна превышать 45 символов!");
            isValidEmailClient = false;
        } else {
            isValidEmailClient = true;
        }

        if (typeEquipmentField.isEmpty()) {
            fieldTypeEquipment.setError("Вы не ввели значение типа обрудования!");
            isValidTypeEquipment = false;
        } else {
            isValidTypeEquipment = true;
        }

        if (nameEquipmentField.isEmpty()) {
            fieldNameEquipment.setError("Вы не ввели значение наименование оборудования!");
            isValidNameEquipment = false;
        } else {
            isValidNameEquipment = true;
        }

        if (charactersEquipmentField.length() > 400) {
            fieldCharactersEquipment.setError("Описание тех. характеристик не должно превышать 400 символов!");
            isValidCharactersEquipment = false;
        } else {
            isValidCharactersEquipment = true;
        }

        if (descriptionProblemField.isEmpty()) {
            fieldDescriptionProblem.setError("Вы не ввели значение описания проблемы!");
            isValidDescriptionProblem = false;
        } else {
            isValidDescriptionProblem = true;
        }

        return isValidEmailClient && isValidTypeEquipment && isValidNameEquipment && isValidCharactersEquipment && isValidDescriptionProblem;
    }

    public void addEquipment(View view) {
        EquipmentDao equipmentDao = dbApi.equipmentDao();

        Equipment equipment;

        if (fieldCharactersEquipment.getText() != null) {
            equipment = new Equipment(
                    fieldTypeEquipment.getText().toString(),
                    fieldEmailClient.getText().toString(),
                    fieldNameEquipment.getText().toString(),
                    fieldCharactersEquipment.getText().toString(),
                    fieldDescriptionProblem.getText().toString(),
                    false);

            Disposable disposable = equipmentDao.insert(equipment)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::showMessageSuccessAddEquipment, throwable -> showMessageErrorAddEquipment());

            compositeDisposable.add(disposable);
        } else {
            equipment = new Equipment(
                    fieldTypeEquipment.getText().toString(),
                    fieldEmailClient.getText().toString(),
                    fieldNameEquipment.getText().toString(),
                    null,
                    fieldDescriptionProblem.getText().toString(),
                    false);

            Disposable disposable = equipmentDao.insert(equipment)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::showMessageSuccessAddEquipment, throwable -> showMessageErrorAddEquipment());

            compositeDisposable.add(disposable);
        }
    }

    private void showMessageSuccessAddEquipment() {
        Toast.makeText(getApplicationContext(), "Успешно добавлено оборудование и поставлено на учет!", Toast.LENGTH_SHORT).show();
    }

    private void showMessageErrorAddEquipment() {
        Toast.makeText(getApplicationContext(), "Возникла ошибка при добалении и постановке оборудования на учет!", Toast.LENGTH_SHORT).show();
    }
}