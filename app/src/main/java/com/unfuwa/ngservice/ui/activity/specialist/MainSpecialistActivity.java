package com.unfuwa.ngservice.ui.activity.specialist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jakewharton.rxbinding4.widget.RxTextView;
import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.dao.ClientDao;
import com.unfuwa.ngservice.dao.EquipmentDao;
import com.unfuwa.ngservice.dao.GraphWorkDao;
import com.unfuwa.ngservice.dao.KnowledgeBaseDao;
import com.unfuwa.ngservice.dao.NotificationDao;
import com.unfuwa.ngservice.dao.RegServiceDao;
import com.unfuwa.ngservice.dao.ServiceDao;
import com.unfuwa.ngservice.dao.SubcategoryDao;
import com.unfuwa.ngservice.dao.TaskWorkDao;
import com.unfuwa.ngservice.dao.TypeEquipmentDao;
import com.unfuwa.ngservice.extendedmodel.EquipmentClient;
import com.unfuwa.ngservice.extendedmodel.GraphTaskWork;
import com.unfuwa.ngservice.extendedmodel.RegServiceExtended;
import com.unfuwa.ngservice.extendedmodel.SpecialistUser;
import com.unfuwa.ngservice.extendedmodel.SubcategoryExtended;
import com.unfuwa.ngservice.model.Category;
import com.unfuwa.ngservice.model.Equipment;
import com.unfuwa.ngservice.model.KnowledgeBase;
import com.unfuwa.ngservice.model.Notification;
import com.unfuwa.ngservice.model.RegService;
import com.unfuwa.ngservice.model.Service;
import com.unfuwa.ngservice.model.Subcategory;
import com.unfuwa.ngservice.model.TaskWork;
import com.unfuwa.ngservice.model.TypeEquipment;
import com.unfuwa.ngservice.ui.activity.general.AuthorizationActivity;
import com.unfuwa.ngservice.ui.fragment.specialist.AddEquipmentFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.EquipmentDetailFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.GraphWorkFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.CategoryKnowledgeFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.KnowledgeDetailFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.KnowledgeFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.SubcategoryKnowledgeFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.MainSpecialistFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.RegServiceEquipmentFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.ServiceEquipmentFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.TaskWorkDetailFragment;
import com.unfuwa.ngservice.ui.fragment.specialist.TasksWorkFragment;
import com.unfuwa.ngservice.util.adapter.AdapterListEquipment;
import com.unfuwa.ngservice.util.adapter.AdapterListKnowledge;
import com.unfuwa.ngservice.util.adapter.AdapterListRegService;
import com.unfuwa.ngservice.util.adapter.AdapterListServices;
import com.unfuwa.ngservice.util.adapter.AdapterListSubcategories;
import com.unfuwa.ngservice.util.adapter.AdapterListTasksWork;
import com.unfuwa.ngservice.util.adapter.AdapterListTasksWorkToday;
import com.unfuwa.ngservice.util.adapter.AdapterNotifications;
import com.unfuwa.ngservice.util.database.DatabaseApi;
import com.unfuwa.ngservice.util.email.GMailSender;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
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

    private Category category;
    private Subcategory subcategory;
    private KnowledgeBase knowledgeBase;
    private ArrayList<SubcategoryExtended> listSubcategories;
    private ArrayList<KnowledgeBase> listKnowledgeBase;
    private AdapterListSubcategories adapterListSubcategories;
    private AdapterListKnowledge adapterListKnowledge;
    private ListView listViewSubcategories;
    private RecyclerView recyclerViewKnowledgeBase;
    private AutoCompleteTextView fieldSearchSubcategories;
    private SearchView fieldSearchKnowledge;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ImageView imageKnowledgeBase;
    private TextView idKnowledgeBase;
    private TextView categoryKnowledgeBase;
    private TextView subcategoryKnowledgeBase;
    private TextView themeKnowledgeBase;
    private EditText fieldFullDescriptionKnowledgeBase;

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
    private Button buttonUpdateDescriptionProblem;
    private EquipmentClient equipmentClient;
    private double sumPriceEmailSend = 0.0;
    private int countPricesEmailSend = 0;

    private boolean isValidDescriptionProblemDetail;

    private EditText fieldIdEquipmentAddService;
    private AutoCompleteTextView fieldAddService;
    private ImageView iconDownListServices;
    private EditText fieldDescriptionAddService;
    private Button buttonAddService;
    private ArrayList<Service> listServices;
    private ArrayList<RegServiceExtended> listRegServices;
    private AdapterListServices adapterListServices;
    private AdapterListRegService adapterListRegServices;
    private ListView listViewRegService;
    private TextView sumPriceRegService;
    private double sumPrice = 0.0;
    private int countServices = 0;
    private Service service;
    private RegService regService;

    private boolean isValidAddService;
    private boolean isValidDescriptionService;

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
    private CategoryKnowledgeFragment categoryKnowledgeFragment;
    private SubcategoryKnowledgeFragment subcategoryKnowledgeFragment;
    private KnowledgeFragment knowledgeFragment;
    private KnowledgeDetailFragment knowledgeDetailFragment;
    private TasksWorkFragment tasksWorkFragment;
    private TaskWorkDetailFragment taskWorkDetailFragment;
    private GraphWorkFragment graphWorkFragment;
    private ServiceEquipmentFragment serviceEquipmentFragment;
    private EquipmentDetailFragment equipmentDetailFragment;
    private RegServiceEquipmentFragment regServiceEquipmentFragment;
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
        categoryKnowledgeFragment = new CategoryKnowledgeFragment();
        subcategoryKnowledgeFragment = new SubcategoryKnowledgeFragment();
        knowledgeFragment = new KnowledgeFragment();
        knowledgeDetailFragment = new KnowledgeDetailFragment();
        tasksWorkFragment = new TasksWorkFragment();
        taskWorkDetailFragment = new TaskWorkDetailFragment();
        graphWorkFragment = new GraphWorkFragment();
        serviceEquipmentFragment = new ServiceEquipmentFragment();
        equipmentDetailFragment = new EquipmentDetailFragment();
        regServiceEquipmentFragment = new RegServiceEquipmentFragment();
        addEquipmentFragment = new AddEquipmentFragment();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.bringToFront();
        navigationViewBottom.bringToFront();

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, mainSpecialistFragment, "MainSpecialist")
                .add(R.id.fragment_container, categoryKnowledgeFragment, "CategoryKnowledge")
                .add(R.id.fragment_container, subcategoryKnowledgeFragment, "SubcategoryKnowledge")
                .add(R.id.fragment_container, knowledgeFragment, "Knowledge")
                .add(R.id.fragment_container, knowledgeDetailFragment, "KnowledgeDetail")
                .add(R.id.fragment_container, tasksWorkFragment, "TasksWork")
                .add(R.id.fragment_container, taskWorkDetailFragment, "TaskWorkDetail")
                .add(R.id.fragment_container, graphWorkFragment, "GraphWork")
                .add(R.id.fragment_container, serviceEquipmentFragment, "ServiceEquipment")
                .add(R.id.fragment_container, equipmentDetailFragment, "EquipmentDetail")
                .add(R.id.fragment_container, regServiceEquipmentFragment, "RegServiceEquipment")
                .add(R.id.fragment_container, addEquipmentFragment, "AddEquipment")
                .show(mainSpecialistFragment)
                .hide(categoryKnowledgeFragment)
                .hide(subcategoryKnowledgeFragment)
                .hide(knowledgeFragment)
                .hide(knowledgeDetailFragment)
                .hide(tasksWorkFragment)
                .hide(taskWorkDetailFragment)
                .hide(graphWorkFragment)
                .hide(serviceEquipmentFragment)
                .hide(equipmentDetailFragment)
                .hide(regServiceEquipmentFragment)
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

        if (listTasksWorkToday.size() != 0) {
            Toast.makeText(getApplicationContext(), "Успешно сформирован список рабочих задач на сегодня!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Список рабочих задач на сегодня пуст!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMessageErrorTasksWorkToday() {
        Toast.makeText(getApplicationContext(), "Возникла ошибка при формировании списка рабочий задач на сегодня!", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    public void getTaskDetails(View view) {
        if (taskWork != null) {
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
        } else {
            Toast.makeText(getApplicationContext(), "Вы не выбрали задачу из списка!", Toast.LENGTH_SHORT).show();
        }
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
                .show(categoryKnowledgeFragment)
                .commit();

        activeFragment = categoryKnowledgeFragment;

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

        if (listTasksWork.size() != 0) {
            Toast.makeText(getApplicationContext(), "Успешно сформирован список рабочих задач!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Список рабочих задач пуст!", Toast.LENGTH_SHORT).show();
        }
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
                nameFragment.setTextSize(13);

                idEquipmentDetail = equipmentDetailFragment.getView().findViewById(R.id.id_equipment_detail);
                fieldEmailClientDetail = findViewById(R.id.field_email_client_detail);
                fieldTypeEquipmentDetail = findViewById(R.id.field_type_equipment_detail);
                fieldNameEquipmentDetail = findViewById(R.id.field_name_equipment_detail);
                fieldCharactersEquipmentDetail = findViewById(R.id.field_characters_detail);
                fieldDescriptionProblemDetail = findViewById(R.id.field_description_problem_detail);
                buttonUpdateDescriptionProblem = findViewById(R.id.button_update_description_problem_equipment);

                equipmentClient = adapterListEquipment.getItem(position);

                idEquipmentDetail.setText("Оборудование № " + Integer.toString(equipmentClient.getEquipment().getId()));
                fieldEmailClientDetail.setText(equipmentClient.getClient().getEmail());
                fieldTypeEquipmentDetail.setText(equipmentClient.getEquipment().getNameType());
                fieldNameEquipmentDetail.setText(equipmentClient.getEquipment().getName());
                fieldCharactersEquipmentDetail.setText(equipmentClient.getEquipment().getCharacters());
                fieldDescriptionProblemDetail.setText(equipmentClient.getEquipment().getDescriptionProblem());

                Observable<String> descriptionProblemEquipmentField = RxTextView.textChanges(fieldDescriptionProblemDetail)
                        .skip(1)
                        .map(CharSequence::toString)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .distinctUntilChanged();

                Disposable disposable = descriptionProblemEquipmentField
                        .map(this::isValidationFieldsEquipmentDetailFragment)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::enabledUpdateDescriptionProblem);

                compositeDisposable.add(disposable);
            }

            private void enabledUpdateDescriptionProblem(boolean validFields) {
                if (validFields) {
                    buttonUpdateDescriptionProblem.setEnabled(true);
                } else {
                    buttonUpdateDescriptionProblem.setEnabled(false);
                }
            }

            private boolean isValidationFieldsEquipmentDetailFragment(String descriptionProblemEquipmentField) {
                if (descriptionProblemEquipmentField.isEmpty()) {
                    fieldDescriptionProblemDetail.setError("Вы не ввели значение описания проблемы!");
                    isValidDescriptionProblemDetail = false;
                } else if (descriptionProblemEquipmentField.length() > 400) {
                    fieldDescriptionProblem.setError("Количество символов превышает отметку в 400 знаков!");
                    isValidDescriptionProblem = false;
                } else {
                    isValidDescriptionProblemDetail = true;
                }

                return isValidDescriptionProblemDetail;
            }
        });

        Toast.makeText(getApplicationContext(), "Успешно сформирован список оборудования на учете!", Toast.LENGTH_SHORT).show();
    }

    private void showMessageErrorListEquipments() {
        Toast.makeText(getApplicationContext(), "Возникла ошибка при формировании списка оборудования на учете!", Toast.LENGTH_SHORT).show();
    }

    public void setStatusRepairCompleteEquipment(View view) {
        RegServiceDao regServiceDao = dbApi.regServiceDao();
        EquipmentDao equipmentDao = dbApi.equipmentDao();
        GMailSender gMailSender = new GMailSender();

        equipmentClient.getEquipment().setStatusRepair(true);

        Disposable disposable = equipmentDao.update(equipmentClient.getEquipment())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showMessageCompleteRepairEquipment, throwable -> showMessageErrorCompleteRepairEquipment());

        Disposable disposable1 = regServiceDao.getRegServiceByEquipment(equipmentClient.getEquipment().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::startCalculationSumPriceEmail, Throwable::printStackTrace);

        Disposable disposable2 = Completable.fromAction(new Action() {
                    @Override
                    public void run() throws Throwable {
                        gMailSender.sendMail(
                        "Оповещение об выполнении сервисного обслуживания оборудования" + " " + Integer.toString(equipmentClient.getEquipment().getId()),
                        "Здравствуйте " + equipmentClient.getClient().getFName() + " " + equipmentClient.getClient().getIName() + " " + equipmentClient.getClient().getOName() + " "
                                + " мы рады вас уведомить, что оборудование с идентификатором" + " " + Integer.toString(equipmentClient.getEquipment().getId()) + " " + "полностью исправно!" + "\n"
                                + "Сумма оплаты составляет: " + Double.toString(sumPriceEmailSend) + "руб. Ждем вас в сервисном центре. Спасибо, что выбираете нас!",
                        "ru.unfuwa.ngservice@gmail.com", equipmentClient.getClient().getEmail());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showMessageCompleteRepairEquipment, throwable -> showMessageErrorCompleteRepairEquipment());

        compositeDisposable.add(disposable);
        compositeDisposable.add(disposable1);
        compositeDisposable.add(disposable2);
    }

    private void startCalculationSumPriceEmail(List<RegServiceExtended> list) {
        ArrayList<Double> listPricesEmail = new ArrayList<>();
        ArrayList<RegServiceExtended> listRegServiceEmail = new ArrayList<>(list);

        for(RegServiceExtended regServiceExtended : listRegServiceEmail) {
            listPricesEmail.add(regServiceExtended.getService().getPrice());
        }

        Disposable disposable = Observable.fromIterable(listPricesEmail)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::calculationSumPriceEmail, Throwable::printStackTrace);

        compositeDisposable.add(disposable);
    }

    @SuppressLint("SetTextI18n")
    private void calculationSumPriceEmail(Double price) {
        sumPriceEmailSend += price;
        countPricesEmailSend++;

        if (countPricesEmailSend == 0) {
            sumPriceEmailSend = 0;
        }
    }

    private void showMessageCompleteRepairEquipment() {
        Toast.makeText(getApplicationContext(), "Ремонт обрудования был успешно завершен!", Toast.LENGTH_SHORT).show();
    }

    private void showMessageErrorCompleteRepairEquipment() {
        Toast.makeText(getApplicationContext(), "Возникла ошибка при завершении ремонта оборудования!", Toast.LENGTH_SHORT).show();
    }

    public void updateDescriptionProblemEquipment(View view) {
        EquipmentDao equipmentDao = dbApi.equipmentDao();

        equipmentClient.getEquipment().setDescriptionProblem(fieldDescriptionProblemDetail.getText().toString());

        Disposable disposable = equipmentDao.update(equipmentClient.getEquipment())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showMessageUpdateDescriptionEquipment, throwable -> showMessageErrorUpdateDescriptionEquipment());

        compositeDisposable.add(disposable);
    }

    private void showMessageUpdateDescriptionEquipment() {
        Toast.makeText(getApplicationContext(), "Описания пробелмы обрудования было успешно изменено!", Toast.LENGTH_SHORT).show();
    }

    private void showMessageErrorUpdateDescriptionEquipment() {
        Toast.makeText(getApplicationContext(), "Возникла ошибка при изменении описания проблемы оборудования!", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    public void getListRegServiceByEquipment(View view) {
        RegServiceDao regServiceDao = dbApi.regServiceDao();
        ServiceDao serviceDao = dbApi.serviceDao();

        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(regServiceEquipmentFragment)
                .commit();

        activeFragment = regServiceEquipmentFragment;

        nameFragment.setText("Услуги оборудования");
        nameFragment.setTextSize(16);

        fieldIdEquipmentAddService = findViewById(R.id.field_id_equipment_add_service);
        fieldAddService = findViewById(R.id.field_add_service);
        fieldAddService.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                service = adapterListServices.getItem(position);
                fieldAddService.setText(service.getName() + ", " + Double.toString(service.getPrice()) + " руб.");
            }
        });
        iconDownListServices = findViewById(R.id.ic_down_list_services);
        fieldDescriptionAddService = findViewById(R.id.field_description_add_service);
        listViewRegService = findViewById(R.id.list_reg_service_equipment);
        sumPriceRegService = regServiceEquipmentFragment.getView().findViewById(R.id.sum_price_reg_service_equipment);
        buttonAddService = findViewById(R.id.button_add_item_list_reg_service);

        fieldIdEquipmentAddService.setText(Integer.toString(equipmentClient.getEquipment().getId()));

        Disposable disposable = regServiceDao.getRegServiceByEquipment(equipmentClient.getEquipment().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::addRegService, throwable -> showMessageErrorRegService());

        Disposable disposable1 = serviceDao.getServices()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::createListServices, throwable -> showMessageErrorListServices());

        Observable<String> addServiceField = RxTextView.textChanges(fieldAddService)
                .skip(1)
                .map(CharSequence::toString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged();

        Observable<String> descriptionAddServiceField = RxTextView.textChanges(fieldDescriptionAddService)
                .skip(1)
                .map(CharSequence::toString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged();

        Disposable disposable2 = Observable.combineLatest(addServiceField, descriptionAddServiceField, this::isValidationFieldsAddService)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::enabledAddService);

        compositeDisposable.add(disposable);
        compositeDisposable.add(disposable1);
        compositeDisposable.add(disposable2);
    }

    private void enabledAddService(boolean validFields) {
        if (validFields) {
            buttonAddService.setEnabled(true);
        } else {
            buttonAddService.setEnabled(false);
        }
    }

    private boolean isValidationFieldsAddService(String addServiceField, String descriptionAddServiceField) {
        if (addServiceField.isEmpty()) {
            fieldAddService.setError("Вы не выбрали услугу!");
            isValidAddService = false;
        } else {
            isValidAddService = true;
        }

        if (descriptionAddServiceField.length() > 400) {
            fieldDescriptionAddService.setError("Количество символов превышает отметку в 400 знаков!");
            isValidDescriptionService = false;
        } else {
            isValidDescriptionService = true;
        }

        return isValidAddService && isValidDescriptionService;
    }

    private void createListServices(List<Service> list) {
        listServices = new ArrayList<>(list);

        adapterListServices = new AdapterListServices(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, listServices);
        fieldAddService.setAdapter(adapterListServices);

        if (listServices.size() != 0) {
            Toast.makeText(getApplicationContext(), "Успешно сформирован список услуг!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Cписок услуг пуст!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMessageErrorListServices() {
        Toast.makeText(getApplicationContext(), "Возникла ошибка при формировании списка услуг!", Toast.LENGTH_SHORT).show();
    }

    public void showListServices(View view) {
        fieldAddService.showDropDown();
    }

    private void addRegService(List<RegServiceExtended> regServicesExtended) {
        sumPrice = 0.0;
        countServices = 0;

        ArrayList<Double> listPrices = new ArrayList<>();

        listRegServices = new ArrayList<>(regServicesExtended);

        for(RegServiceExtended regServiceExtended : listRegServices) {
            listPrices.add(regServiceExtended.getService().getPrice());
        }

        Disposable disposable = Observable.fromIterable(listPrices)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showMessageSuccessSumPrice, throwable -> showMessageErrorSumPrice());

        compositeDisposable.add(disposable);

        adapterListRegServices = new AdapterListRegService(getApplicationContext(), R.layout.list_reg_services, listRegServices);
        listViewRegService.setAdapter(adapterListRegServices);

        if (listRegServices.size() != 0) {
            Toast.makeText(getApplicationContext(), "Успешно сформирован список оказанных услуг!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Список оказанных услуг пуст!", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    private void showMessageSuccessSumPrice(Double price) {
        sumPrice += price;
        countServices++;

        if (listRegServices.size() == countServices) {
            sumPriceRegService.setText("ИТОГО: " + Double.toString(sumPrice) + " руб.");

            Toast.makeText(getApplicationContext(), "Успешно вычислена сумма оплаты по всему списку оказанных услуг!", Toast.LENGTH_SHORT).show();
        } else if (listRegServices.size() == 0) {
            sumPriceRegService.setText("ИТОГО: 0 руб.");

            Toast.makeText(getApplicationContext(), "Cписок оказанных услуг пуст!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMessageErrorSumPrice() {
        Toast.makeText(getApplicationContext(), "Не удалось вычислить сумму оплаты по всему списку оказанных услуг!", Toast.LENGTH_SHORT).show();
    }

    private void showMessageErrorRegService() {
        Toast.makeText(getApplicationContext(), "Возникла ошибка при формировании списка оказанных услуг!", Toast.LENGTH_SHORT).show();
    }

    public void addItemListRegService(View view) {
        RegServiceDao regServiceDao = dbApi.regServiceDao();

        if (!fieldDescriptionAddService.getText().toString().isEmpty()) {
            Disposable disposable = regServiceDao.addRegServiceByEquipment(
                    regService = new RegService(
                            equipmentClient.getEquipment().getId(),
                            specialist.getSpecialist().getLogin(),
                            service.getName(),
                            fieldDescriptionAddService.getText().toString()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::addService, throwable -> showMessageErrorAddRegService());

            compositeDisposable.add(disposable);
        } else {
            Disposable disposable = regServiceDao.addRegServiceByEquipment(
                    regService = new RegService(
                            equipmentClient.getEquipment().getId(),
                            specialist.getSpecialist().getLogin(),
                            service.getName(),
                            null))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::addService, throwable -> showMessageErrorAddRegService());

            compositeDisposable.add(disposable);
        }
    }

    @SuppressLint("SetTextI18n")
    private void addService() {
        listRegServices.add(new RegServiceExtended(regService, service));

        //sumPrice += service.getPrice();

        //sumPriceRegService.setText("ИТОГО: " + Double.toString(sumPrice) + " руб.");

        Toast.makeText(getApplicationContext(), "Успешно обновлена сумма оплаты по всему списку оказанных услуг!", Toast.LENGTH_SHORT).show();

        adapterListRegServices = new AdapterListRegService(getApplicationContext(), R.layout.list_reg_services, listRegServices);
        listViewRegService.setAdapter(adapterListRegServices);

        Toast.makeText(getApplicationContext(), "Успешно обновлен список оказанных услуг!", Toast.LENGTH_SHORT).show();
    }

    private void showMessageErrorAddRegService() {
        Toast.makeText(getApplicationContext(), "Возникла ошибка при добавлении услуги!", Toast.LENGTH_SHORT).show();
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

        ArrayAdapter<String> adapterTypesEquipment = new ArrayAdapter<>(getApplicationContext(), R.layout.list_subcategories_autocomplete, listTypesEquipment);
        fieldTypeEquipment.setAdapter(adapterTypesEquipment);

        if (listTypesEquipment.size() != 0) {
            Toast.makeText(getApplicationContext(), "Успешно сформирован список типов оборудования!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Список типов оборудования пуст!", Toast.LENGTH_SHORT).show();
        }
    }

    public void showListTypesEquipment(View view) {
        fieldTypeEquipment.showDropDown();
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
        } else if (emailClientField.substring(emailClientField.indexOf("@"), emailClientField.length() - 1).length() < 1) {
            fieldEmailClient.setError("Вы не ввели домен почты!");
            isValidEmailClient = false;
        } else if (emailClientField.substring(0, emailClientField.indexOf("@")).length() < 4) {
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
            fieldNameEquipment.setError("Вы не ввели значение наименования оборудования!");
            isValidNameEquipment = false;
        } else if (nameEquipmentField.length() > 200) {
            fieldNameEquipment.setError("Наименование оборудования не должно превышать 200 символов!");
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
        } else if (descriptionProblemField.length() > 400) {
            fieldDescriptionProblem.setError("Количество символов превышает отметку в 400 знаков!");
            isValidDescriptionProblem = false;
        } else {
            isValidDescriptionProblem = true;
        }

        return isValidEmailClient && isValidTypeEquipment && isValidNameEquipment && isValidCharactersEquipment && isValidDescriptionProblem;
    }

    public void addEquipment(View view) {
        EquipmentDao equipmentDao = dbApi.equipmentDao();

        Equipment equipment;

        if (!fieldCharactersEquipment.getText().toString().isEmpty()) {
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

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    public void selectCategory(View view) {
        SubcategoryDao subcategoryDao = dbApi.subcategoryDao();

        switch (view.getId()) {
            case R.id.item_defects_category:
                category = new Category("Неполадки");
                break;
            case R.id.item_safety_engineering_category:
                category = new Category("Техника безопасности");
                break;
            case R.id.item_maintenance_procedure_category:
                category = new Category("Порядок технического обслуживания");
                break;
            case R.id.item_other_category:
                category = new Category("Дополнительные материалы");
                break;
            default:
                category = null;
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(subcategoryKnowledgeFragment)
                .commit();

        activeFragment = subcategoryKnowledgeFragment;

        fieldSearchSubcategories = findViewById(R.id.field_search_subcategories);
        listViewSubcategories = findViewById(R.id.list_subcategories);
        listViewSubcategories.setOnItemClickListener(new AdapterView.OnItemClickListener()  {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                KnowledgeBaseDao knowledgeBaseDao = dbApi.knowledgeBaseDao();

                subcategory = adapterListSubcategories.getItem(position).getSubcategory();

                drawerLayout.closeDrawer(GravityCompat.START);

                fragmentManager.beginTransaction()
                        .hide(activeFragment)
                        .show(knowledgeFragment)
                        .commit();

                activeFragment = knowledgeFragment;

                storage = FirebaseStorage.getInstance();
                storageReference = storage.getReference();

                recyclerViewKnowledgeBase = knowledgeFragment.getView().findViewById(R.id.recycler_view_knowledge);
                fieldSearchKnowledge = findViewById(R.id.field_search_knowledge);

                if (subcategory != null) {
                    nameFragment.setText("Справочник\n(" + subcategory.getName() + ")");
                    nameFragment.setTextSize(14);
                } else {
                    nameFragment.setText("Справочник\n(Неопредлено)");
                    Toast.makeText(getApplicationContext(), "Возникла ошибка при определении подкатегории!", Toast.LENGTH_SHORT).show();
                }

                Disposable disposable = knowledgeBaseDao.getListKnowledgeBase(subcategory.getName())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::createListKnowledgeBase, throwable -> showMessageErrorListKnowledgeBase());

                compositeDisposable.add(disposable);
            }

            private void createListKnowledgeBase(List<KnowledgeBase> list) {
                listKnowledgeBase = new ArrayList<>(list);

                adapterListKnowledge = new AdapterListKnowledge(getApplicationContext(), listKnowledgeBase, storage, new AdapterListKnowledge.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        adapterListKnowledge.notifyItemChanged(position);

                        knowledgeBase = listKnowledgeBase.get(position);

                        drawerLayout.closeDrawer(GravityCompat.START);

                        fragmentManager.beginTransaction()
                                .hide(activeFragment)
                                .show(knowledgeDetailFragment)
                                .commit();

                        activeFragment = knowledgeDetailFragment;

                        imageKnowledgeBase = findViewById(R.id.image_knowledge_base);
                        idKnowledgeBase = findViewById(R.id.id_knowledge_base);
                        categoryKnowledgeBase = findViewById(R.id.category_knowledge_base);
                        subcategoryKnowledgeBase = findViewById(R.id.subcategory_knowledge_base);
                        themeKnowledgeBase = findViewById(R.id.theme_knowledge_base);
                        fieldFullDescriptionKnowledgeBase = findViewById(R.id.field_full_description_knowledge_base);

                        Disposable disposable = Flowable.just(knowledgeBase)
                                .doOnNext(knowledgeBase -> {
                                    StorageReference requestRef = storageReference.child("full_content_knowledgebase/image/" + knowledgeBase.getURLImage());

                                    requestRef.getBytes(1024*1024)
                                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                @Override
                                                public void onSuccess(byte [] bytes) {
                                                    //progressDialog.dismiss();
                                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                    imageKnowledgeBase.setImageBitmap(bitmap);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    //progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "Возникла ошибка во время загрузки обложки!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(this::showMessageSuccessRequest, Throwable::printStackTrace);

                        compositeDisposable.add(disposable);

                        idKnowledgeBase.setText("ID: " + Integer.toString(knowledgeBase.getId()));
                        categoryKnowledgeBase.setText("Категория: " + category.getName());
                        subcategoryKnowledgeBase.setText("Подкатегория: " + knowledgeBase.getNameSubcategory());
                        themeKnowledgeBase.setText("Тема: " + knowledgeBase.getTheme());
                        fieldFullDescriptionKnowledgeBase.setText(knowledgeBase.getFullDescription());
                    }

                    private void showMessageSuccessRequest(KnowledgeBase knowledgeBase) {
                        Toast.makeText(getApplicationContext(), "Обложка успешно загружена!", Toast.LENGTH_SHORT).show();
                    }
                });
                recyclerViewKnowledgeBase.setAdapter(adapterListKnowledge);
                recyclerViewKnowledgeBase.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerViewKnowledgeBase.setHasFixedSize(true);
                fieldSearchKnowledge.setQueryHint("Поиск");
                fieldSearchKnowledge.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        //Disposable disposable = Completable.fromAction(new Action() {
                                //@Override
                                //public void run() throws Throwable {
                                    //adapterListKnowledge.getFilter().filter(newText);
                                //}
                            //})
                                //.delay(5000, TimeUnit.MILLISECONDS)
                                //.subscribeOn(Schedulers.io())
                                //.observeOn(AndroidSchedulers.mainThread())
                                //.subscribe(this::showMessageCompleteSearch, this::showMessageErrorSearch);

                        //compositeDisposable.add(disposable);

                        adapterListKnowledge.getFilter().filter(newText);

                        return true;
                    }

                    private void showMessageCompleteSearch() {
                        //Toast.makeText(getApplicationContext(), "Успешно сформированы результаты поиска!", Toast.LENGTH_SHORT).show();
                    }

                    private void showMessageErrorSearch(Throwable e) {
                        Toast.makeText(getApplicationContext(), "По результатам поиска нечего не найдено!", Toast.LENGTH_SHORT).show();
                    }
                });
                if (listKnowledgeBase.size() != 0) {
                    Toast.makeText(getApplicationContext(), "Успешно сформирован список справочных материалов по подкатегории " + subcategory.getName() + "!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Список справочных материалов по подкатегории " + subcategory.getName() + " пуст!", Toast.LENGTH_SHORT).show();
                }
            }

            private void showMessageErrorListKnowledgeBase() {
                Toast.makeText(getApplicationContext(), "Возникла ошибка при формировании списка справочных материалов по подкатегории " + subcategory.getName() + "!", Toast.LENGTH_SHORT).show();
            }
        });

        if (category != null) {
            nameFragment.setText("Справочник\n(" + category.getName() + ")");

            Disposable disposable = subcategoryDao.getSubcategoriesByCategory(category.getName())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::createListSubcategories, throwable -> showMessageErrorListSubcategories());

            compositeDisposable.add(disposable);
        } else {
            nameFragment.setText("Справочник\n(Неопредлено)");
            Toast.makeText(getApplicationContext(), "Возникла ошибка при определении категории!", Toast.LENGTH_SHORT).show();
        }

        nameFragment.setTextSize(14);
    }

    private void createListSubcategories(List<SubcategoryExtended> list) {
        listSubcategories = new ArrayList<>(list);

        adapterListSubcategories = new AdapterListSubcategories(getApplicationContext(), R.layout.list_subcategory_item, listSubcategories);
        fieldSearchSubcategories.setAdapter(adapterListSubcategories);
        listViewSubcategories.setAdapter(adapterListSubcategories);

        if (listSubcategories.size() != 0) {
            Toast.makeText(getApplicationContext(), "Успешно сформирован список подкатегорий!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Список подкатегорий пуст!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMessageErrorListSubcategories() {
        Toast.makeText(getApplicationContext(), "Возникла ошибка при формировании списка подкатегорий!", Toast.LENGTH_SHORT).show();
    }

    public void downloadFullContent(View view) {
        Disposable disposable = Flowable.just(knowledgeBase)
                .doOnNext(knowledgeBase -> {
                    StorageReference requestRef = storageReference.child("full_content_knowledgebase/pdf/" + knowledgeBase.getURL());

                    requestRef.getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //progressDialog.dismiss();
                                    DownloadManager downloadManager = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                                    Uri uriDownload = Uri.parse(uri.toString());
                                    DownloadManager.Request request = new DownloadManager.Request(uriDownload);

                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, knowledgeBase.getURL());

                                    downloadManager.enqueue(request);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Возникла ошибка во время скачивания файла! (Отсутствие подключения к Интернету)", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showMessageSuccessDownload, Throwable::printStackTrace);

        compositeDisposable.add(disposable);

    }

    private void showMessageSuccessDownload(KnowledgeBase knowledgeBase) {
        Toast.makeText(getApplicationContext(), "Успешно окончено скачивание файла " + knowledgeBase.getURL() + "!", Toast.LENGTH_SHORT).show();
    }
}