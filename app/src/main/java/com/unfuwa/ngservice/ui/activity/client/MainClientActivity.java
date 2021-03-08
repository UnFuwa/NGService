package com.unfuwa.ngservice.ui.activity.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.rxbinding4.widget.RxTextView;
import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.ui.activity.general.AuthorizationActivity;
import com.unfuwa.ngservice.ui.dialog.LoadingDialog;
import com.unfuwa.ngservice.ui.fragment.client.MainClientFragment;
import com.unfuwa.ngservice.ui.fragment.client.RequestFragment;
import com.unfuwa.ngservice.ui.fragment.client.ServiceCentersFragment;
import com.unfuwa.ngservice.ui.fragment.client.StatusRepairFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainClientActivity extends AppCompatActivity {

    private int CALL_PHONE_PERMISSION = 1;
    private static final String NUMBER_CALL_PHONE = "81234567890";

    private static final String CACHE_NAME = "token.txt";
    private String CACHE_PATH;
    private String token;
    private File file;

    private TextInputEditText fieldAddress;
    private TextInputEditText fieldDescription;
    private TextInputEditText fieldDateArrive;
    private Button buttonSendRequest;
    private final LoadingDialog loadingDialog = new LoadingDialog(this);
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private Observable<Boolean> validFields;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private boolean isValidAddress;
    private boolean isValidDescription;
    private boolean isValidDateArrive;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView navigationViewButtom;
    private Toolbar toolbar;

    private FrameLayout fragmentContainer;
    private Fragment fragment;

    private void initComponents() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_main_client);
        navigationViewButtom = findViewById(R.id.nav_main_client_bottom);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fragment = new MainClientFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_client_activity);

        CACHE_PATH = getApplicationContext().getDataDir().getPath() + "/" + CACHE_NAME;

        initComponents();

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
        navigationViewButtom.bringToFront();

        Observable<String> addressField = RxTextView.textChanges(address)
                .skip(1)
                .map(charSequence -> charSequence.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged();

        Observable<String> descriptionField = RxTextView.textChanges(description)
                .skip(1)
                .map(charSequence -> charSequence.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged();

        Observable<String> dateArriveField = RxTextView.textChanges(dateArrive)
                .skip(1)
                .map(charSequence -> charSequence.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged();

        Disposable disposable = validFields.combineLatest(addressField, descriptionField, dateArriveField, this::isValidationFields)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::enabledSendRequest);

        compositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        compositeDisposable.dispose();
    }

    private void enabledSendRequest(boolean validFields) {
        if (validFields) {
            sendRequest.setEnabled(true);
        } else {
            sendRequest.setEnabled(false);
        }
    }

    private boolean isValidationFields(String addressField, String descriptionField, String dateArriveField) {
        isValidAddress = !addressField.isEmpty();

        if (!isValidAddress) {
            address.setError("Вы не ввели значение адреса!");
        }

        isValidDescription = !descriptionField.isEmpty();

        if (!isValidDescription) {
            description.setError("Вы не ввели описание!");
        }

        isValidDateArrive = !dateArriveField.isEmpty();

        if (!isValidDateArrive) {
            dateArrive.setError("Вы не ввели значение даты оказания услуги!");
        }

        return isValidAddress && isValidDescription && isValidDateArrive;
    }

    public void runSendRequest(View view) {
        UserClientDao userClientDao = dbApi.userClientDao();

        Disposable disposable = userClientDao.getEmailClientByToken(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::sendRequest, error -> showMessageError());

        compositeDisposable.add(disposable);
    }

    private void sendRequest(Client client) {
        String emailClient = client.getEmail();
        String addressIn = address.getText().toString();
        String descriptionIn = description.getText().toString();
        String dateArriveIn = dateArrive.getText().toString();

        RequestDao requestDao = dbApi.requestDao();

        Date dateNow = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");

        String formatDate = dateFormat.format(dateNow) + " " + timeFormat.format(dateNow);

        Request request = new Request(emailClient, addressIn, descriptionIn, dateArriveIn, formatDate);

        Disposable disposable = requestDao.insert(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showMessageSuccess, error -> showMessageError());

        compositeDisposable.add(disposable);
    }

    private void showMessageSuccess() {
        Toast.makeText(getApplicationContext(), "Отправка заявки прошла успешно!", Toast.LENGTH_SHORT).show();
    }

    private void showMessageError() {
        Toast.makeText(getApplicationContext(), "Возникла ошибка во время отправки заявки!", Toast.LENGTH_SHORT).show();
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

        fragment = new MainClientFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void changeFragmentServiceCenters(MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        fragment = new ServiceCentersFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void changeFragmentStatusRepair(MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        fragment = new StatusRepairFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void changeFragmentRequest(MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        fragment = new RequestFragment();

        fieldAddress = fragment.getView().findViewById(R.id.field_address);
        fieldDescription = fragment.getView().findViewById(R.id.field_description);
        fieldDateArrive = fragment.getView().findViewById(R.id.field_date_arrive);
        buttonSendRequest = fragment.getView().findViewById(R.id.button_send_request);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
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

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                fieldDateArrive.setText(dayOfMonth + "." + month + "." + year);
            }
        }
    }
}