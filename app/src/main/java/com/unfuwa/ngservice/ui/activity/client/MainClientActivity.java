package com.unfuwa.ngservice.ui.activity.client;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jakewharton.rxbinding4.widget.RxTextView;
import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.dao.EquipmentDao;
import com.unfuwa.ngservice.dao.RegServiceDao;
import com.unfuwa.ngservice.dao.RequestDao;
import com.unfuwa.ngservice.extendedmodel.ClientUser;
import com.unfuwa.ngservice.extendedmodel.Photo;
import com.unfuwa.ngservice.extendedmodel.RegServiceExtended;
import com.unfuwa.ngservice.model.Equipment;
import com.unfuwa.ngservice.model.Request;
import com.unfuwa.ngservice.ui.activity.general.AuthorizationActivity;
import com.unfuwa.ngservice.ui.dialog.LoadingDialog;
import com.unfuwa.ngservice.ui.fragment.client.GoogleMapsFragment;
import com.unfuwa.ngservice.ui.fragment.client.MainClientFragment;
import com.unfuwa.ngservice.ui.fragment.client.RegServiceFragment;
import com.unfuwa.ngservice.ui.fragment.client.RequestFragment;
import com.unfuwa.ngservice.ui.fragment.client.ServiceCentersFragment;
import com.unfuwa.ngservice.ui.fragment.client.StatusRepairFragment;
import com.unfuwa.ngservice.util.adapter.AdapterListImages;
import com.unfuwa.ngservice.util.adapter.AdapterListRegService;
import com.unfuwa.ngservice.util.database.DatabaseApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainClientActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private final int CALL_PHONE_PERMISSION = 1;
    private final int ACCESS_LOCATION_PERMISSION = 1;

    private static final String NUMBER_CALL_PHONE = "81234567890";

    private static final String CACHE_NAME = "token.txt";
    private String CACHE_PATH;
    private String token;
    private File file;

    private ClientUser client;

    private DatabaseApi dbApi;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private EditText fieldAddress;
    private EditText fieldDescription;
    private EditText fieldDateArrive;
    private ListView listViewRequest;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Button buttonSendRequest;
    private final LoadingDialog loadingDialog = new LoadingDialog(this);

    private static final int IMAGE_REQUEST_CODE = 2;
    private final ArrayList<Uri> listUriPhotos = new ArrayList<>();
    private final ArrayList<Photo> listPhotos = new ArrayList<>();
    private final ArrayList<Photo> listPhotosDeletePositions = new ArrayList<>();
    private AdapterListImages adapterListImages;
    private Uri imageURI;
    private String keyImageForRequest;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private EditText fieldSearch;
    private Place place;
    private PlacesClient placesClient;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private List<Place.Field> fields;

    private ImageView iconMyLocation;
    private Address address;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private Observable<Boolean> validFieldsRequestFragment;

    private boolean isValidAddress;
    private boolean isValidDescription;
    private boolean isValidDateArrive;

    private ImageView iconStatusRepair;
    private TextView labelStatusRepair;
    private EditText fieldIdEquipment;
    private EditText fieldNameEquipment;
    private EditText fieldTypeEquipment;
    private EditText fieldDescriptionStatus;
    private TextView labelSumPriceRegService;
    private ListView listViewRegService;
    private Button buttonUpdateStatusRepair;
    private Button buttonListRegService;

    private Observable<Boolean> validFieldsStatusRepairFragment;

    private boolean isValidIdEquipment;

    private double sumPrice = 0.0;
    private int countServices = 0;
    private AdapterListRegService adapterListRegService;
    private ArrayList<RegServiceExtended> listServices;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView navigationViewBottom;
    private Toolbar toolbar;
    private TextView fioCLient;
    private TextView emailClient;
    private TextView nameFragment;

    private Fragment activeFragment;
    private FragmentManager fragmentManager;
    private MainClientFragment mainClientFragment;
    private RequestFragment requestFragment;
    private GoogleMapsFragment googleMapsFragment;
    private ServiceCentersFragment serviceCentersFragment;
    private StatusRepairFragment statusRepairFragment;
    private RegServiceFragment regServiceFragment;

    private void initComponents() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_main_client);
        navigationViewBottom = findViewById(R.id.nav_main_client_bottom);
        toolbar = findViewById(R.id.toolbar);
        fioCLient = navigationView.getHeaderView(0).findViewById(R.id.fio_client);
        emailClient = navigationView.getHeaderView(0).findViewById(R.id.email_client);
        nameFragment = findViewById(R.id.client_name_fragment);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_client_activity);

        CACHE_PATH = getApplicationContext().getDataDir().getPath() + "/" + CACHE_NAME;

        dbApi = DatabaseApi.getInstance(getApplicationContext());

        initComponents();

        if (getIntent().getSerializableExtra("client") != null) {
            this.client = (ClientUser) getIntent().getSerializableExtra("client");

            if (client.getClient().getOName() != null) {
                fioCLient.setText(
                        client.getClient().getFName() + " "
                                + client.getClient().getIName() + " "
                                + client.getClient().getOName());
            } else {
                fioCLient.setText(
                        client.getClient().getFName() + " "
                                + client.getClient().getIName());
            }

            emailClient.setText(client.getClient().getEmail());
        }

        mainClientFragment = new MainClientFragment();
        requestFragment = new RequestFragment();
        googleMapsFragment = new GoogleMapsFragment(getApplicationContext(), this);
        serviceCentersFragment = new ServiceCentersFragment(getApplicationContext(), this);
        statusRepairFragment = new StatusRepairFragment();
        regServiceFragment = new RegServiceFragment();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        file = new File(CACHE_PATH);

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader readFile = new BufferedReader(fileReader);

            while((token = readFile.readLine()) != null) readFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.bringToFront();
        navigationViewBottom.bringToFront();

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, mainClientFragment, "MainClient")
                .add(R.id.fragment_container, requestFragment, "Request")
                .add(R.id.fragment_container, googleMapsFragment, "GoogleMaps")
                .add(R.id.fragment_container, serviceCentersFragment, "ServiceCenters")
                .add(R.id.fragment_container, statusRepairFragment, "StatusRepair")
                .add(R.id.fragment_container, regServiceFragment, "RegServiceFragment")
                .show(mainClientFragment)
                .hide(requestFragment)
                .hide(googleMapsFragment)
                .hide(serviceCentersFragment)
                .hide(statusRepairFragment)
                .hide(regServiceFragment)
                .commit();

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(getApplicationContext());
        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        activeFragment = mainClientFragment;
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

    public void changeFragmentHome(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
        } else {
            item.setChecked(false);
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(mainClientFragment)
                .commit();

        activeFragment = mainClientFragment;

        nameFragment.setText("Главная");
    }

    public void changeFragmentServiceCenters(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
        } else {
            item.setChecked(false);
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(serviceCentersFragment)
                .commit();

        activeFragment = serviceCentersFragment;

        nameFragment.setText("Сервис центры");
    }

    public void changeFragmentStatusRepair(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
        } else {
            item.setChecked(false);
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(statusRepairFragment)
                .commit();

        activeFragment = statusRepairFragment;

        nameFragment.setText("Статус ремонта");

        iconStatusRepair = findViewById(R.id.icon_status_repair);
        labelStatusRepair = findViewById(R.id.output_status_repair);
        fieldIdEquipment = findViewById(R.id.field_id_equipment);
        fieldNameEquipment = findViewById(R.id.field_name_equipment);
        fieldTypeEquipment = findViewById(R.id.field_type_equipment);
        fieldDescriptionStatus = findViewById(R.id.field_description_status);
        buttonUpdateStatusRepair = findViewById(R.id.button_update_status_repair);
        buttonListRegService = findViewById(R.id.button_list_regservice);

        Observable<String> idEquipmentField = RxTextView.textChanges(fieldIdEquipment)
                .skip(1)
                .map(CharSequence::toString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged();

        Disposable disposable = idEquipmentField
                .map(this::isValidationFieldsStatusRepairFragment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::enabledUpdateStatusRepair);

        compositeDisposable.add(disposable);
    }

    private void enabledUpdateStatusRepair(boolean validFields) {
        if (validFields) {
            buttonUpdateStatusRepair.setEnabled(true);
            buttonListRegService.setEnabled(true);
        } else {
            buttonUpdateStatusRepair.setEnabled(false);
            buttonListRegService.setEnabled(false);
        }
    }

    private boolean isValidationFieldsStatusRepairFragment(String idEquipmentField) {
        if (idEquipmentField.isEmpty()) {
            fieldIdEquipment.setError("Вы не ввели значение идентификатора оборудования!");
            isValidIdEquipment = false;
        } else {
            isValidIdEquipment = true;
        }

        return isValidIdEquipment;
    }

    public void getStatusRepairEquipment(View view) {
        EquipmentDao equipmentDao = dbApi.equipmentDao();

        Disposable disposable = equipmentDao.getEquipmentById(Integer.parseInt(fieldIdEquipment.getText().toString()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable1 -> loadingDialog.showLoading())
                .doOnTerminate(loadingDialog::hideLoading)
                .subscribe(this::showMessageSuccessStatusRepair, throwable -> showMessageErrorStatusRepair());

        compositeDisposable.add(disposable);
    }

    private void showMessageSuccessStatusRepair(Equipment equipment) {
        if (equipment.isStatusRepair()) {
            iconStatusRepair.setImageResource(R.drawable.ic_status_repair_is_done);
            labelStatusRepair.setText("Исправлено");
        } else {
            iconStatusRepair.setImageResource(R.drawable.ic_status_repair_is_not_done);
            labelStatusRepair.setText("Неисправлено");
        }

        fieldNameEquipment.setText(equipment.getName());
        fieldTypeEquipment.setText(equipment.getNameType());
        fieldDescriptionStatus.setText(equipment.getDescriptionProblem());

        Toast.makeText(getApplicationContext(), "Статус ремонта обрудования успешно определен!", Toast.LENGTH_SHORT).show();
    }

    private void showMessageErrorStatusRepair() {
        Toast.makeText(getApplicationContext(), "Возникла ошибка во время определения статуса ремонта оборудования!", Toast.LENGTH_SHORT).show();
    }

    public void changeFragmentRequest(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
        } else {
            item.setChecked(false);
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(requestFragment)
                .commit();

        activeFragment = requestFragment;

        nameFragment.setText("Заявка");

        fieldAddress = findViewById(R.id.field_address);
        fieldDescription = findViewById(R.id.field_description);
        fieldDateArrive = findViewById(R.id.field_date_arrive);
        listViewRequest = findViewById(R.id.list_images);
        listViewRequest.setOnItemClickListener(this);
        buttonSendRequest = findViewById(R.id.button_send_request);

        Observable<String> addressField = RxTextView.textChanges(fieldAddress)
                .skip(1)
                .map(CharSequence::toString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged();

        Observable<String> descriptionField = RxTextView.textChanges(fieldDescription)
                .skip(1)
                .map(CharSequence::toString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged();

        Observable<String> dateArriveField = RxTextView.textChanges(fieldDateArrive)
                .skip(1)
                .map(CharSequence::toString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged();

        Disposable disposable = validFieldsRequestFragment.combineLatest(addressField, descriptionField, dateArriveField, this::isValidationFieldsRequestFragment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::enabledSendRequest);

        compositeDisposable.add(disposable);
    }

    public void addItemListPhoto(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

    public void deleteItemListPhoto(View view) {
        for (Photo photo : listPhotosDeletePositions) {
            listPhotos.remove(photo);
        }

        adapterListImages = new AdapterListImages(getApplicationContext(), R.layout.list_images_item, listPhotos);
        listViewRequest.setAdapter(adapterListImages);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ColorDrawable colorDrawable = (ColorDrawable)view.getBackground();

        if (colorDrawable.getColor() != getColor(R.color.Red)) {
            view.setBackgroundColor(getColor(R.color.Red));
            listPhotosDeletePositions.add(adapterListImages.getItem(position));
        } else if (colorDrawable.getColor() == getColor(R.color.Red)) {
            view.setBackgroundColor(getColor(R.color.DarkGreenCyan));
            listPhotosDeletePositions.remove(adapterListImages.getItem(position));
        }
    }

    private void enabledSendRequest(boolean validFields) {
        if (validFields) {
            buttonSendRequest.setEnabled(true);
        } else {
            buttonSendRequest.setEnabled(false);
        }
    }

    private boolean isValidationFieldsRequestFragment(String addressField, String descriptionField, String dateArriveField) {
        if (addressField.isEmpty()) {
            fieldAddress.setError("Вы не ввели значение адреса!");
            isValidAddress = false;
        } else if (addressField.length() > 200) {
            fieldAddress.setError("Количество символов превышает отметку в 200 знаков!");
            isValidAddress = false;
        } else {
            isValidAddress = true;
        }

        if (descriptionField.isEmpty()) {
            fieldDescription.setError("Вы не ввели описание!");
            isValidDescription = false;
        } else if (descriptionField.length() > 400) {
            fieldDescription.setError("Количество символов превышает отметку в 400 знаков!");
            isValidDescription = false;
        } else {
            isValidDescription = true;
        }

        if (dateArriveField.isEmpty()) {
            fieldDateArrive.setError("Вы не ввели значение даты оказания услуги!");
            isValidDateArrive = false;
        } else {
            isValidDateArrive = true;
        }

        return isValidAddress && isValidDescription && isValidDateArrive;
    }

    public void sendRequest(View view) {
        RequestDao requestDao = dbApi.requestDao();

        Date dateNow = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        String formatDate = dateFormat.format(dateNow);

        Request request = new Request(
                client.getClient().getEmail(),
                fieldAddress.getText().toString(),
                fieldDescription.getText().toString(),
                fieldDateArrive.getText().toString(),
                formatDate);

        if (listPhotos.isEmpty()) {
            Disposable disposable = requestDao.insert(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(disposable1 -> loadingDialog.showLoading())
                    .doOnTerminate(loadingDialog::hideLoading)
                    .subscribe(this::showMessageSuccessRequest, Throwable::printStackTrace);

            compositeDisposable.add(disposable);
        } else if (!listPhotos.isEmpty()) {
            Disposable disposable = requestDao.insert(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(disposable1 -> loadingDialog.showLoading())
                    .doOnTerminate(loadingDialog::hideLoading)
                    .subscribe(this::uploadPhotos, Throwable::printStackTrace);

            compositeDisposable.add(disposable);
        }
    }

    private void uploadPhotos(Long id) {
        /*ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setTitle("Загрузка фотографии...");
        progressDialog.show();*/

        Disposable disposable = Observable.fromIterable(listPhotos)
                .doOnNext(photo -> {
                    String keyRequest = id.toString() + "_" + UUID.randomUUID().toString();
                    StorageReference requestRef = storageReference.child("photo_requests/" + keyRequest);

                    requestRef.putFile(photo.getUri())
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    //rogressDialog.dismiss();
                                    Snackbar.make(findViewById(android.R.id.content), "Фотография загружена...", Snackbar.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Возникла ошибка во время загрузки фотографии!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                    //double percentProgress = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                    //progressDialog.setMessage((int)percentProgress + "%");
                                }
                            });
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable1 -> loadingDialog.showLoading())
                .doOnTerminate(loadingDialog::hideLoading)
                .subscribe(this::showMessageSuccessRequest, Throwable::printStackTrace);

        compositeDisposable.add(disposable);
    }

    private void showMessageSuccessRequest(Photo photo) {
        Toast.makeText(getApplicationContext(), "Отправка заявки прошла успешно! Фотографии успешно загружены!", Toast.LENGTH_SHORT).show();
    }

    private void showMessageSuccessRequest(Long id) {
        Toast.makeText(getApplicationContext(), "Отправка заявки прошла успешно!", Toast.LENGTH_SHORT).show();
    }

    public void logout(MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        new AlertDialog.Builder(this)
                .setTitle("Подтверждение")
                .setMessage("Вы действительно хотите выйти из аккаунта?")
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        file.delete();

                        if (file.exists()) {
                            file.delete();
                        }

                        dialog.dismiss();

                        Toast.makeText(getApplicationContext(), "Выход из аккаунта успешно выполнен!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainClientActivity.this, AuthorizationActivity.class);
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

    public void callOperator(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Разрешение приложению было предоставлено!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + NUMBER_CALL_PHONE));
            startActivity(intent);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                new AlertDialog.Builder(this)
                        .setTitle("Разрешения приложения")
                        .setMessage("Это разрешение необходимо для выполнения данной функциональности!")
                        .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainClientActivity.this, new String[] {Manifest.permission.CALL_PHONE}, CALL_PHONE_PERMISSION);
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
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CALL_PHONE}, CALL_PHONE_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CALL_PHONE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Разрешение получено!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Разрешение отклонено!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == ACCESS_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Разрешение получено!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Разрешение отклонено!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void selectDate(View view) {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);

        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();

        dateSetListener = (view1, year1, month1, dayOfMonth) -> {
            if (dayOfMonth < 10 && month1 < 10) {
                fieldDateArrive.setText("0" + dayOfMonth + "." + "0" + month1 + "." + year1);
            } else if (dayOfMonth < 10) {
                fieldDateArrive.setText("0" + dayOfMonth + "." + month1 + "." + year1);
            } else if (month1 < 10) {
                fieldDateArrive.setText(dayOfMonth + "." + "0" + month1 + "." + year1);
            } else {
                fieldDateArrive.setText(dayOfMonth + "." + month1 + "." + year1);
            }
        };
    }

    public void selectPlace(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Разрешение приложению было предоставлено!", Toast.LENGTH_SHORT).show();

            fragmentManager.beginTransaction()
                    .hide(activeFragment)
                    .show(googleMapsFragment)
                    .commit();

            fieldSearch = findViewById(R.id.field_search_maps);
            iconMyLocation = findViewById(R.id.ic_my_location);

            googleMap = googleMapsFragment.getGoogleMap();

            activeFragment = googleMapsFragment;
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Разрешения приложения")
                        .setMessage("Это разрешение необходимо для выполнения данной функциональности!")
                        .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainClientActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_LOCATION_PERMISSION);
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
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_LOCATION_PERMISSION);
            }
        }
    }

    public void searchLocation(View view) {
        Geocoder geocoder = new Geocoder(getApplicationContext());
        List<Address> list;

        try {
            list = geocoder.getFromLocationName(fieldSearch.getText().toString(), 1);

            address = list.get(0);
            LatLng location = new LatLng(address.getLatitude(), address.getLongitude());

            googleMap.clear();

            googleMap.addMarker(new MarkerOptions().position(location).title(address.getAddressLine(0)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getMyLocation(View view) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        try {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();
                        LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                        googleMap.clear();

                        googleMap.addMarker(new MarkerOptions().position(myLocation).title("Мое местоположение"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                    } else {
                        Toast.makeText(getApplicationContext(), "Возникла ошибка при определении вашего местоположения!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void selectLocation(View view) {
        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(requestFragment)
                .commit();

        fieldAddress.setText(address.getAddressLine(0));

        activeFragment = googleMapsFragment;
    }

    public void startAutoCompletePlace(View view) {
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(getApplicationContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageURI = data.getData();
            listUriPhotos.add(imageURI);
            listPhotos.add(new Photo(getApplicationContext(), imageURI));
            adapterListImages = new AdapterListImages(getApplicationContext(), R.layout.list_images_item, listPhotos);
            listViewRequest.setAdapter(adapterListImages);
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = Autocomplete.getPlaceFromIntent(data);
                fieldSearch.setText(place.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    public void getListRegServiceByEquipment(View view) {
        RegServiceDao regServiceDao = dbApi.regServiceDao();

        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(regServiceFragment)
                .commit();

        listViewRegService = findViewById(R.id.list_reg_service);
        labelSumPriceRegService = findViewById(R.id.sum_price_reg_service);

        activeFragment = regServiceFragment;

        Disposable disposable = regServiceDao.getRegServiceByEquipment(Integer.parseInt(fieldIdEquipment.getText().toString()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::addRegService, throwable -> showMessageErrorRegService());

        compositeDisposable.add(disposable);
    }

    private void addRegService(List<RegServiceExtended> regServicesExtended) {
        ArrayList<Double> listPrices = new ArrayList<>();

        //AtomicReference<Double> sumPrice = new AtomicReference<>(0.0);
        listServices = new ArrayList<>(regServicesExtended);

        for(RegServiceExtended regServiceExtended : listServices) {
            listPrices.add(regServiceExtended.getService().getPrice());
        }

        Disposable disposable = Observable.fromIterable(listPrices)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showMessageSuccessSumPrice, throwable -> showMessageErrorSumPrice());

        compositeDisposable.add(disposable);

        adapterListRegService = new AdapterListRegService(getApplicationContext(), R.layout.list_reg_services, listServices);
        listViewRegService.setAdapter(adapterListRegService);

        Toast.makeText(getApplicationContext(), "Успешно сформирован список оказанных услуг!", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    private void showMessageSuccessSumPrice(Double price) {
        sumPrice += price;
        countServices++;

        if (listServices.size() == countServices) {
            labelSumPriceRegService.setText("ИТОГО: " + Double.toString(sumPrice) + " руб.");

            Toast.makeText(getApplicationContext(), "Успешно вычислена сумма оплаты по всему списку оказанных услуг!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMessageErrorSumPrice() {
        Toast.makeText(getApplicationContext(), "Не удалось вычислить сумму оплаты по всему списку оказанных услуг!", Toast.LENGTH_SHORT).show();
    }

    private void showMessageErrorRegService() {
        Toast.makeText(getApplicationContext(), "Возникла ошибка при формировании списка оказанных услуг!", Toast.LENGTH_SHORT).show();
    }
}