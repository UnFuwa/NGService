package com.unfuwa.ngservice.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.rxbinding4.widget.RxTextView;
import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.dao.UserClientDao;
import com.unfuwa.ngservice.dao.UserDao;
import com.unfuwa.ngservice.model.Client;
import com.unfuwa.ngservice.model.User;
import com.unfuwa.ngservice.ui.dialog.LoadingDialog;
import com.unfuwa.ngservice.ui.dialog.VerifactionEmailDialog;
import com.unfuwa.ngservice.util.DatabaseApi;
import com.unfuwa.ngservice.util.TokenGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RegistrationActivity extends AppCompatActivity {

    private TextInputLayout textInputLayoutPassword;
    private TextInputEditText fieldEmail;
    private TextInputEditText fieldPassword;
    private TextInputEditText fieldFIO;
    private TextInputEditText fieldTelephone;
    private Button buttonSignUp;
    private final LoadingDialog loadingDialog = new LoadingDialog(RegistrationActivity.this);
    private final VerifactionEmailDialog verifactionEmailDialog = new VerifactionEmailDialog(RegistrationActivity.this);

    private static final String CACHE_NAME = "token.txt";
    private String CACHE_PATH;

    private DatabaseApi dbApi;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Observable<Boolean> verificationEmail;

    private String genToken;

    private Observable<Boolean> validFields;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private boolean isValidEmail;
    private boolean isValidPassword;
    private boolean isValidFIO;
    private boolean isValidTelephone;

    private void initComponents() {
        textInputLayoutPassword = findViewById(R.id.text_input_layout_password);
        fieldEmail = findViewById(R.id.field_email);
        fieldPassword = findViewById(R.id.field_password);
        fieldFIO = findViewById(R.id.field_fio);
        fieldTelephone = findViewById(R.id.field_telephone);
        buttonSignUp = findViewById(R.id.button_signup);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);

        initComponents();

        firebaseAuth = FirebaseAuth.getInstance();

        CACHE_PATH = getApplicationContext().getDataDir().getPath() + "/" + CACHE_NAME;

        Observable<String> emailField = RxTextView.textChanges(fieldEmail)
                .skip(1)
                .map(CharSequence::toString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged();

        Observable<String> passwordField = RxTextView.textChanges(fieldPassword)
                .skip(1)
                .map(CharSequence::toString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged();

        Observable<String> FIOField = RxTextView.textChanges(fieldFIO)
                .skip(1)
                .map(CharSequence::toString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged();

        Observable<String> telephoneField = RxTextView.textChanges(fieldTelephone)
                .skip(1)
                .map(CharSequence::toString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged();

        Disposable disposable = validFields.combineLatest(emailField, passwordField, FIOField, telephoneField, this::isValidationFields)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::enabledSignIn);

        compositeDisposable.add(disposable);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        compositeDisposable.dispose();
    }

    private void enabledSignIn(boolean validFields) {
        if (validFields) {
            buttonSignUp.setEnabled(true);
        } else {
            buttonSignUp.setEnabled(false);
        }
    }

    private boolean isValidationFields(String emailField, String passwordField, String FIOField, String telephoneField) {
        if (emailField.isEmpty()) {
            fieldEmail.setError("Вы не ввели значение эл.почты!");
            isValidEmail = false;
        } else if (!emailField.contains("@")) {
            fieldEmail.setError("Вы ввели неверное значение эл.почты!");
            isValidEmail = false;
        } else {
            isValidEmail = true;
        }

        if (passwordField.isEmpty()) {
            fieldPassword.setError("Вы не ввели значение пароля!");
            textInputLayoutPassword.setPasswordVisibilityToggleEnabled(false);
            isValidPassword = false;
        } else {
            isValidPassword = true;
        }

        if (FIOField.isEmpty()) {
            fieldFIO.setError("Вы не ввели значение ФИО!");
            isValidFIO = false;
        } else {
            isValidFIO = true;
        }

        if (telephoneField.isEmpty()) {
            fieldTelephone.setError("Вы не ввели значение номера телефона!");
            isValidTelephone = false;
        } else {
            isValidTelephone = true;
        }

        return isValidEmail && isValidPassword && isValidFIO && isValidTelephone;
    }

    public void signUp(View view) {
        String emailIn = fieldEmail.getText().toString();
        String passwordIn = fieldPassword.getText().toString();
        String [] FIOIn = fieldFIO.getText().toString().split(" ");
        String telephoneIn = fieldTelephone.getText().toString();
        genToken = TokenGenerator.generateNewToken();

        User userIn = new User(emailIn, "Client", passwordIn, genToken);
        Client clientIn = new Client(emailIn, FIOIn[0], FIOIn[1], FIOIn[2], telephoneIn);

        dbApi = DatabaseApi.getInstance(getApplicationContext());
        UserClientDao userClientDao = dbApi.userClientDao();

        firebaseAuth.createUserWithEmailAndPassword(userIn.getLogin(), userIn.getPassword())
                .addOnCompleteListener(this, task -> firebaseAuth.getCurrentUser().sendEmailVerification()
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Регистрация прошла успешно. Пожалуйста проверьте свой email для подтверждения!", Toast.LENGTH_SHORT).show();

                                Completable completableUser = userClientDao.insertUser(userIn)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread());

                                Completable completableClient = userClientDao.insertClient(clientIn)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread());

                                Disposable disposable = Completable.concatArray(completableUser, completableClient)
                                        .doOnSubscribe(disposable1 -> verifactionEmailDialog.showVerification())
                                        .doOnTerminate(this::verificationEmail)
                                        .subscribe(this::startNextActivity, Throwable::printStackTrace);

                                compositeDisposable.add(disposable);
                            } else {
                                Toast.makeText(getApplicationContext(), "Возникла ошибка во время регистрации!", Toast.LENGTH_SHORT).show();
                            }
                        })
                );
    }

    private void verificationEmail() {
        firebaseAuth.getCurrentUser().reload();

        Disposable disposable = Observable.just(firebaseAuth.getCurrentUser().isEmailVerified())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::isVerificationEmail);

        compositeDisposable.add(disposable);
    }

    private void isVerificationEmail(boolean verificationEmail) {
        if (verificationEmail) verifactionEmailDialog.hideVerification();
    }

    public void startNextActivity() {
        File file = new File(CACHE_PATH);

        try {
            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();

            FileWriter writeFile = new FileWriter(file);

            writeFile.append(genToken + System.lineSeparator());

            writeFile.close();

            Toast.makeText(getApplicationContext(), "Авторизация прошла успешно, получен доступ клиента!", Toast.LENGTH_SHORT).show();;
            Intent intent = new Intent(getApplicationContext(), MainClientActivity.class);
            startActivity(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showErrorMessage() {
        Toast.makeText(getApplicationContext(), "Возникла ошибка во время регистрации!", Toast.LENGTH_SHORT).show();
        verifactionEmailDialog.hideVerification();
    }

    public void startAuthorizationActivity(View view) {
        Intent intent = new Intent(this, AuthorizationActivity.class);
        startActivity(intent);
    }
}