package com.unfuwa.ngservice.ui.activity.general;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.rxbinding4.widget.RxTextView;
import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.dao.ClientDao;
import com.unfuwa.ngservice.dao.SpecialistDao;
import com.unfuwa.ngservice.dao.UserDao;
import com.unfuwa.ngservice.extendedmodel.ClientUser;
import com.unfuwa.ngservice.extendedmodel.SpecialistUser;
import com.unfuwa.ngservice.model.User;
import com.unfuwa.ngservice.ui.activity.client.MainClientActivity;
import com.unfuwa.ngservice.ui.activity.specialist.MainSpecialistActivity;
import com.unfuwa.ngservice.ui.dialog.LoadingDialog;
import com.unfuwa.ngservice.ui.dialog.VerifactionEmailDialog;
import com.unfuwa.ngservice.util.database.DatabaseApi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AuthorizationActivity extends AppCompatActivity {

    private TextInputLayout textInputLayoutPassword;
    private TextInputEditText fieldLogin;
    private TextInputEditText fieldPassword;
    private Button buttonSignIn;
    private final LoadingDialog loadingDialog = new LoadingDialog(this);
    private final VerifactionEmailDialog verifactionEmailDialog = new VerifactionEmailDialog(this);
    private boolean isVerificationEmail = false;
    private Thread threadEmailVerify;

    private static final String CACHE_NAME = "token.txt";
    private String CACHE_PATH;

    private DatabaseApi dbApi;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private Observable<Boolean> validFields;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private boolean isValidLogin;
    private boolean isValidPassword;

    private void initComponents() {
        textInputLayoutPassword = findViewById(R.id.text_input_layout_password);
        fieldLogin = findViewById(R.id.field_login);
        fieldPassword = findViewById(R.id.field_password);
        buttonSignIn = findViewById(R.id.button_signin);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorization_activity);

        initComponents();

        firebaseAuth = FirebaseAuth.getInstance();

        CACHE_PATH = getApplicationContext().getDataDir().getPath() + "/" + CACHE_NAME;

        dbApi = DatabaseApi.getInstance(getApplicationContext());

        Observable<String> loginField = RxTextView.textChanges(fieldLogin)
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

        Disposable disposable = validFields.combineLatest(loginField, passwordField, this::isValidationFields)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::enabledSignIn);

        compositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        compositeDisposable.dispose();
    }

    private void enabledSignIn(boolean validFields) {
        if (validFields) {
            buttonSignIn.setEnabled(true);
        } else {
            buttonSignIn.setEnabled(false);
        }
    }

    private boolean isValidationFields(String loginField, String passwordField) {
        if (loginField.isEmpty()) {
            fieldLogin.setError("Вы не ввели значение логина или эл.почты!");
            isValidLogin = false;
        } else if (loginField.length() < 4) {
            fieldLogin.setError("Логин (имя почтового ящика) должен состоять из 4 или более символов!");
            isValidLogin = false;
        } else if (loginField.length() > 45) {
            fieldLogin.setError("Логин (эл.почты) не должен превышать 45 символов!");
            isValidLogin = false;
        } else {
            isValidLogin = true;
        }

        if (passwordField.isEmpty()) {
            fieldPassword.setError("Вы не ввели значение пароля!");
            textInputLayoutPassword.setPasswordVisibilityToggleEnabled(false);
            isValidPassword = false;
        } else if (passwordField.length() < 6) {
            fieldPassword.setError("Пароль должен состоять из 6 или более символов!");
            textInputLayoutPassword.setPasswordVisibilityToggleEnabled(false);
            isValidPassword = false;
        } else if (passwordField.length() > 25 ) {
            fieldPassword.setError("Пароль не должен превышать 25 символов!");
            textInputLayoutPassword.setPasswordVisibilityToggleEnabled(false);
            isValidPassword = false;
        } else {
            textInputLayoutPassword.setPasswordVisibilityToggleEnabled(true);
            isValidPassword = true;
        }

        return isValidLogin && isValidPassword;
    }

    public void signIn(View view) {
        User userIn = new User(fieldLogin.getText().toString(), fieldPassword.getText().toString());

        UserDao userDao = dbApi.userDao();

        Disposable disposable = userDao.getUserByLogin(userIn.getLogin())
                .filter(user -> (user.getPassword().equals(userIn.getPassword())))
                .toSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::startNextActivity, throwable -> showErrorMessage());

        compositeDisposable.add(disposable);
    }

    public void startNextActivity(User user) {
        Disposable disposable;

        File file = new File(CACHE_PATH);

        try {
            file.createNewFile();

            FileWriter writeFile = new FileWriter(file);

            writeFile.append(user.getToken() + System.lineSeparator());

            writeFile.close();

            switch (user.getNameAccessRight()) {
                case "Client":
                    ClientDao clientDao = dbApi.clientDao();

                    disposable = clientDao.getClientByEmail(user.getLogin())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::startMainClientActivity, throwable -> showErrorMessage());

                    compositeDisposable.add(disposable);
                    break;
                case "Specialist":
                    SpecialistDao specialistDao = dbApi.specialistDao();

                    disposable = specialistDao.getSpecialistByUser(user.getLogin())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::startMainSpecialistActivity, throwable -> showErrorMessage());

                    compositeDisposable.add(disposable);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startMainClientActivity(ClientUser client) {
        firebaseAuth.signInWithEmailAndPassword(client.getUser().getLogin(), client.getUser().getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        firebaseUser = task.getResult().getUser();

                        if (task.isSuccessful() && firebaseUser.isEmailVerified()) {
                            Toast.makeText(getApplicationContext(), "Авторизация прошла успешно, получен доступ клиента!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainClientActivity.class);
                            intent.putExtra("client", client);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Адрес электронный почты не был подтвержден, выполнена повторная отправка!", Toast.LENGTH_SHORT).show();

                            firebaseUser.sendEmailVerification()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Disposable disposable = Flowable.just(task1)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .doOnSubscribe(subscription -> verifactionEmailDialog.showVerification())
                                                    .subscribe(this::verificationEmail, Throwable::printStackTrace);

                                            compositeDisposable.add(disposable);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Возникла ошибка во время отправки подтверждения эл. почты! (Невалидный адрес)", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    public void verificationEmail(Task<Void> voidTask) {
                        AtomicBoolean flag = new AtomicBoolean(true);
                        firebaseUser = firebaseAuth.getCurrentUser();
                        firebaseUser.reload();

                        try {
                            threadEmailVerify = new Thread(()->{
                                while(flag.get()) {
                                    firebaseUser.reload();

                                    if (firebaseUser.isEmailVerified()) {
                                        flag.set(false);
                                        isVerificationEmail = true;
                                    } else {
                                        flag.set(true);
                                    }

                                    try {
                                        Thread.sleep(3000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            threadEmailVerify.start();
                            threadEmailVerify.join();

                            if (isVerificationEmail) {
                                startNextActivity();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    public void startNextActivity() {
                        if (isVerificationEmail) {
                            verifactionEmailDialog.hideVerification();

                            Toast.makeText(getApplicationContext(), "Адрес электронной почты был подтвержден!", Toast.LENGTH_SHORT).show();

                            File file = new File(CACHE_PATH);

                            try {
                                if (file.exists()) {
                                    file.delete();
                                }

                                file.createNewFile();

                                FileWriter writeFile = new FileWriter(file);

                                writeFile.append(client.getUser().getToken() + System.lineSeparator());

                                writeFile.close();

                                startMainClientActivity(client);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Адрес электронной почты был не подтвержден!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    private void startMainClientActivity(ClientUser client) {
                        Toast.makeText(getApplicationContext(), "Авторизация прошла успешно, получен доступ клиента!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainClientActivity.class);
                        intent.putExtra("client", client);
                        startActivity(intent);
                    }
                });
    }

    private void startMainSpecialistActivity(SpecialistUser specialist) {
        Toast.makeText(getApplicationContext(), "Авторизация прошла успешно, получен доступ специалиста!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainSpecialistActivity.class);
        intent.putExtra("specialist", specialist);
        startActivity(intent);
    }

    public void showErrorMessage() {
        Toast.makeText(getApplicationContext(), "Вы ввели неверные данные от аккаунта!", Toast.LENGTH_SHORT).show();
    }

    public void startRegistrationActivity(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }
}