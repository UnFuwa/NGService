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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.jakewharton.rxbinding4.widget.RxTextView;
import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.dao.EquipmentDao;
import com.unfuwa.ngservice.dao.RequestDao;
import com.unfuwa.ngservice.dao.UserClientDao;
import com.unfuwa.ngservice.extendedmodel.ClientUser;
import com.unfuwa.ngservice.model.Client;
import com.unfuwa.ngservice.model.Equipment;
import com.unfuwa.ngservice.model.Request;
import com.unfuwa.ngservice.ui.activity.general.AuthorizationActivity;
import com.unfuwa.ngservice.ui.dialog.LoadingDialog;
import com.unfuwa.ngservice.ui.fragment.client.GoogleMapsFragment;
import com.unfuwa.ngservice.ui.fragment.client.MainClientFragment;
import com.unfuwa.ngservice.ui.fragment.client.RequestFragment;
import com.unfuwa.ngservice.ui.fragment.client.ServiceCentersFragment;
import com.unfuwa.ngservice.ui.fragment.client.StatusRepairFragment;
import com.unfuwa.ngservice.util.DatabaseApi;

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

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.collections.ArraysKt;

public class MainClientActivity extends AppCompatActivity {

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
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Button buttonSendRequest;
    private final LoadingDialog loadingDialog = new LoadingDialog(this);

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
    private Button buttonUpdateStatusRepair;
    private Button buttonListRegService;

    private Observable<Boolean> validFieldsStatusRepairFragment;

    private boolean isValidIdEquipment;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView navigationViewBottom;
    private Toolbar toolbar;
    private TextView nameFragment;

    private Fragment activeFragment;
    private FragmentManager fragmentManager;
    private MainClientFragment mainClientFragment;
    private RequestFragment requestFragment;
    private GoogleMapsFragment googleMapsFragment;
    private ServiceCentersFragment serviceCentersFragment;
    private StatusRepairFragment statusRepairFragment;

    private void initComponents() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_main_client);
        navigationViewBottom = findViewById(R.id.nav_main_client_bottom);
        toolbar = findViewById(R.id.toolbar);
        nameFragment = findViewById(R.id.client_name_fragment);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mainClientFragment = new MainClientFragment();
        requestFragment = new RequestFragment();
        googleMapsFragment = new GoogleMapsFragment(getApplicationContext(), this);
        serviceCentersFragment = new ServiceCentersFragment(getApplicationContext(), this);
        statusRepairFragment = new StatusRepairFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_client_activity);

        CACHE_PATH = getApplicationContext().getDataDir().getPath() + "/" + CACHE_NAME;

        dbApi = DatabaseApi.getInstance(getApplicationContext());

        initComponents();

        file = new File(CACHE_PATH);

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader readFile = new BufferedReader(fileReader);

            while((token = readFile.readLine()) != null) readFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (getIntent().getSerializableExtra("client") != null) {
            client = (ClientUser) getIntent().getSerializableExtra("client");
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.bringToFront();
        navigationViewBottom.bringToFront();
    }

    @Override
    protected void onStart() {
        super.onStart();

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, mainClientFragment, "MainClient")
                .add(R.id.fragment_container, requestFragment, "Request")
                .add(R.id.fragment_container, googleMapsFragment, "GoogleMaps")
                .add(R.id.fragment_container, serviceCentersFragment, "ServiceCenters")
                .add(R.id.fragment_container, statusRepairFragment, "StatusRepair")
                .show(mainClientFragment)
                .hide(requestFragment)
                .hide(googleMapsFragment)
                .hide(serviceCentersFragment)
                .hide(statusRepairFragment)
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
        drawerLayout.closeDrawer(GravityCompat.START);

        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(mainClientFragment)
                .commit();

        activeFragment = mainClientFragment;

        nameFragment.setText("Главная");
    }

    public void changeFragmentServiceCenters(MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(serviceCentersFragment)
                .commit();

        activeFragment = serviceCentersFragment;

        nameFragment.setText("Сервис центры");
    }

    public void changeFragmentStatusRepair(MenuItem item) {
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
                .subscribe(this::showMessageSuccessStatusRepair, throwable -> showMessageErrorStatusRepair());;
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

    public void startSendRequest(View view) {
        UserClientDao userClientDao = dbApi.userClientDao();

        Disposable disposable = userClientDao.getEmailClientByToken(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::sendRequest, throwable -> showMessageErrorRequest());

        compositeDisposable.add(disposable);
    }

    private void sendRequest(Client client) {
        RequestDao requestDao = dbApi.requestDao();

        Date dateNow = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        String formatDate = dateFormat.format(dateNow);

        Request request = new Request(
                client.getEmail(),
                fieldAddress.getText().toString(),
                fieldDescription.getText().toString(),
                fieldDateArrive.getText().toString(), formatDate);

        Disposable disposable = requestDao.insert(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable1 -> loadingDialog.showLoading())
                .doOnTerminate(loadingDialog::hideLoading)
                .subscribe(this::showMessageSuccessRequest, throwable -> showMessageErrorRequest());

        compositeDisposable.add(disposable);
    }

    private void showMessageSuccessRequest() {
        Toast.makeText(getApplicationContext(), "Отправка заявки прошла успешно!", Toast.LENGTH_SHORT).show();
    }

    private void showMessageErrorRequest() {
        Toast.makeText(getApplicationContext(), "Возникла ошибка во время отправки заявки!", Toast.LENGTH_SHORT).show();
    }

    public void logout(MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        new AlertDialog.Builder(this)
                .setTitle("Подтверждение")
                .setMessage("Вы действительно хотите выйти из аккаунта?")
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadingDialog.showLoading();
                        file.delete();

                        if (file.exists()) {
                            file.delete();
                        }

                        dialog.dismiss();
                        loadingDialog.hideLoading();

                        Toast.makeText(getApplicationContext(), "Выход из аккаунта успешно выполнен!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainClientActivity.this, AuthorizationActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        loadingDialog.hideLoading();
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

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = Autocomplete.getPlaceFromIntent(data);
                fieldSearch.setText(place.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("STATUS", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    public void getListRegServiceByEquipment(View view) {
    }
}