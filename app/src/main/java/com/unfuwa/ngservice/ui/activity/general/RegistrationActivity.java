package com.unfuwa.ngservice.ui.activity.general;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.rxbinding4.widget.RxTextView;
import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.dao.ClientDao;
import com.unfuwa.ngservice.dao.UserClientDao;
import com.unfuwa.ngservice.extendedmodel.ClientUser;
import com.unfuwa.ngservice.model.Client;
import com.unfuwa.ngservice.model.User;
import com.unfuwa.ngservice.ui.activity.client.MainClientActivity;
import com.unfuwa.ngservice.ui.dialog.VerifactionEmailDialog;
import com.unfuwa.ngservice.util.database.DatabaseApi;
import com.unfuwa.ngservice.util.TokenGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TELEPHONE_CODE = "+7";
    private MaskImpl maskF;
    private MaskImpl maskI;
    private MaskImpl maskO;
    private MaskImpl maskTelephone;

    private User userIn;

    private TextInputLayout textInputLayoutPassword;
    private TextInputEditText fieldEmail;
    private TextInputEditText fieldPassword;
    private TextInputEditText fieldF;
    private TextInputEditText fieldI;
    private TextInputEditText fieldO;
    private TextInputEditText fieldTelephone;
    private Button buttonSignUp;

    private final VerifactionEmailDialog verifactionEmailDialog = new VerifactionEmailDialog(RegistrationActivity.this);
    private Button buttonConfirm;
    private boolean isVerificationEmail = false;
    private Thread threadEmailVerify;

    private static final String CACHE_NAME = "token.txt";
    private String CACHE_PATH;

    private DatabaseApi dbApi;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private String genToken;

    private Observable<Boolean> validFields;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private boolean isValidEmail;
    private boolean isValidPassword;
    private boolean isValidF;
    private boolean isValidI;
    private boolean isValidO;
    private boolean isValidTelephone;

    private void initComponents() {
        textInputLayoutPassword = findViewById(R.id.text_input_layout_password);
        fieldEmail = findViewById(R.id.field_email);
        fieldPassword = findViewById(R.id.field_password);
        fieldF = findViewById(R.id.field_f);
        fieldI = findViewById(R.id.field_i);
        fieldO = findViewById(R.id.field_o);
        fieldTelephone = findViewById(R.id.field_telephone);

        MaskImpl maskTelephone = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER);
        maskTelephone.setForbidInputWhenFilled(true);
        FormatWatcher formatWatcherTelephone = new MaskFormatWatcher(maskTelephone);
        formatWatcherTelephone.installOn(fieldTelephone);

        buttonSignUp = findViewById(R.id.button_signup);
        buttonConfirm = verifactionEmailDialog.getButtonConfirm();
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

        Observable<String> FField = RxTextView.textChanges(fieldF)
                .skip(1)
                .map(CharSequence::toString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged();

        Observable<String> IField = RxTextView.textChanges(fieldI)
                .skip(1)
                .map(CharSequence::toString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged();

        Observable<String> OField = RxTextView.textChanges(fieldO)
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

        Disposable disposable = validFields.combineLatest(emailField, passwordField, FField, IField, OField, telephoneField, this::isValidationFields)
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

    private boolean isValidationFields(String emailField, String passwordField, String FField, String IField, @Nullable String OField, String telephoneField) {
        if (emailField.isEmpty()) {
            fieldEmail.setError("Вы не ввели значение эл.почты!");
            isValidEmail = false;
        } else if (!emailField.contains("@")) {
            fieldEmail.setError("Вы ввели неверное значение эл.почты! Отсутствует '@'.");
            isValidEmail = false;
        } else if (emailField.substring(emailField.indexOf("@"), emailField.length() - 1).length() < 1) {
            fieldEmail.setError("Вы не ввели домен почты!");
            isValidEmail = false;
        } else if (emailField.substring(0, emailField.indexOf("@")).length() < 4) {
            fieldEmail.setError("Имя почтового ящика должно состоять из 4 или более символов!");
            isValidEmail = false;
        } else if (emailField.length() > 45) {
            fieldEmail.setError("Эл.почта не должна превышать 45 символов!");
            isValidEmail = false;
        } else {
            isValidEmail = true;
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
        } else if (!checkUpperCasePassword(passwordField)) {
            fieldPassword.setError("Пароль должен содержать хотя бы 1 заглавную букву!");
            textInputLayoutPassword.setPasswordVisibilityToggleEnabled(true);
            isValidPassword = false;
        } else {
            textInputLayoutPassword.setPasswordVisibilityToggleEnabled(true);
            isValidPassword = true;
        }

        if (FField.isEmpty()) {
            fieldF.setError("Вы не ввели значение фамилии!");
            isValidF = false;
        } else {
            isValidF = true;
        }

        if (IField.isEmpty()) {
            fieldI.setError("Вы не ввели значение имени!");
            isValidI = false;
        } else {
            isValidI = true;
        }

        if (telephoneField.isEmpty()) {
            fieldTelephone.setError("Вы не ввели значение номера телефона!");
            isValidTelephone = false;
        } else if (telephoneField.length() < 18) {
            fieldTelephone.setError("Номер телефона должен состоять из 10 значений цифр!");
            isValidTelephone = false;
        } else {
            isValidTelephone = true;
        }

        return isValidEmail && isValidPassword && isValidF && isValidI && isValidTelephone;
    }

    private boolean checkUpperCasePassword(String s) {
        char ch;
        boolean upperCaseExist = false;

        for (int i = 0; i < s.length(); i++) {
            ch = s.charAt(i);

            if (Character.isUpperCase(ch)) {
                upperCaseExist = true;
            }

            if (upperCaseExist) {
                return true;
            }
        }

        return false;
    }

    public void signUp(View view) {
        String emailIn = fieldEmail.getText().toString();
        String passwordIn = fieldPassword.getText().toString();
        String fName = fieldF.getText().toString();
        String iName = fieldI.getText().toString();
        String telephoneIn = fieldTelephone.getText().toString();
        String oName;

        if (fieldO.getText() == null) {
            oName = null;
        } else {
            oName = fieldO.getText().toString();
        }

        genToken = TokenGenerator.generateNewToken();

        userIn = new User(emailIn, "Client", passwordIn, genToken);
        Client clientIn = new Client(emailIn, fName, iName, oName, telephoneIn);

        dbApi = DatabaseApi.getInstance(getApplicationContext());
        UserClientDao userClientDao = dbApi.userClientDao();

        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            firebaseAuth.createUserWithEmailAndPassword(userIn.getLogin(), userIn.getPassword())
                    .addOnCompleteListener(
                            this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful() && task.isComplete()) {
                                        task.getResult().getUser().sendEmailVerification()
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful() && task1.isComplete()) {
                                                        Toast.makeText(getApplicationContext(), "Регистрация прошла успешно. Пожалуйста проверьте свой email для подтверждения!", Toast.LENGTH_SHORT).show();

                                                        Completable completableUser = userClientDao.insertUser(userIn)
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread());

                                                        Completable completableClient = userClientDao.insertClient(clientIn)
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread());

                                                        Disposable disposable = Completable.concatArray(completableUser, completableClient)
                                                                .doOnSubscribe(disposable1 -> verifactionEmailDialog.showVerification())
                                                                .subscribe(this::verificationEmail, throwable -> showMessageErrorRegClient());

                                                        compositeDisposable.add(disposable);
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Возникла ошибка во время отправки подтверждения эл. почты! (Невалидный адрес эл. почты)", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Аккаунт с данной эл. почтой уже существует!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                private void showMessageErrorRegClient() {
                                    Toast.makeText(getApplicationContext(), "Возникла ошибка во регистрации пользователя! (Неудача выполнение транзакции)", Toast.LENGTH_SHORT).show();
                                }

                                public void verificationEmail() {
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

                                            writeFile.append(genToken + System.lineSeparator());

                                            writeFile.close();

                                            ClientDao clientDao = dbApi.clientDao();

                                            Disposable disposable = clientDao.getClientByEmail(userIn.getLogin())
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(this::startMainClientActivity, throwable -> showErrorMessage());

                                            compositeDisposable.add(disposable);
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
                            }
                    );
        } else {
            Toast.makeText(getApplicationContext(), "Отсутствует подключения к Интернету!", Toast.LENGTH_SHORT).show();
        }
    }

    public void verificationEmail(View view) {

    }

    public void showErrorMessage() {
        Toast.makeText(getApplicationContext(), "Возникла ошибка во время регистрации!", Toast.LENGTH_SHORT).show();
    }

    public void startAuthorizationActivity(View view) {
        Intent intent = new Intent(this, AuthorizationActivity.class);
        startActivity(intent);
    }
}