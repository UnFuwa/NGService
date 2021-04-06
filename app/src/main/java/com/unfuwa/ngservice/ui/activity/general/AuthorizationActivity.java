package com.unfuwa.ngservice.ui.activity.general;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding4.widget.RxTextView;
import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.dao.UserDao;
import com.unfuwa.ngservice.model.User;
import com.unfuwa.ngservice.ui.activity.client.MainClientActivity;
import com.unfuwa.ngservice.ui.dialog.LoadingDialog;
import com.unfuwa.ngservice.util.DatabaseApi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
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

    private static final String CACHE_NAME = "token.txt";
    private String CACHE_PATH;

    private DatabaseApi dbApi;

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
        String loginIn = fieldLogin.getText().toString();
        String passwordIn = fieldPassword.getText().toString();

        User userIn = new User(loginIn, passwordIn);

        UserDao userDao = dbApi.userDao();

        Disposable disposable = userDao.getUserByLogin(userIn.getLogin())
                .filter(user -> (user.getPassword().equals(userIn.getPassword())))
                .toSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable1 -> loadingDialog.showLoading())
                .doOnTerminate(loadingDialog::hideLoading)
                .subscribe(this::startNextActivity, throwable -> showErrorMessage());

        compositeDisposable.add(disposable);
    }

    public void startNextActivity(User user) {
        Intent intent;

        File file = new File(CACHE_PATH);

        try {
            file.createNewFile();

            FileWriter writeFile = new FileWriter(file);

            writeFile.append(user.getToken() + System.lineSeparator());

            writeFile.close();

            switch (user.getNameAccessRight()) {
                case "Client":
                    Toast.makeText(getApplicationContext(), "Авторизация прошла успешно, получен доступ клиента!", Toast.LENGTH_SHORT).show();
                    intent = new Intent(this, MainClientActivity.class);
                    startActivity(intent);
                    break;
                case "Specialist":
                    Toast.makeText(getApplicationContext(), "Авторизация прошла успешно, получен доступ специалиста!", Toast.LENGTH_SHORT).show();
                    //intent = new Intent(this, );
                    //startActivity(intent);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showErrorMessage() {
        Toast.makeText(getApplicationContext(), "Вы ввели неверные данные от аккаунта!", Toast.LENGTH_SHORT).show();
    }

    public void startRegistrationActivity(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }
}