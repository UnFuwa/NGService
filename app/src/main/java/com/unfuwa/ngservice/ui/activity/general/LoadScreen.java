package com.unfuwa.ngservice.ui.activity.general;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.dao.ClientDao;
import com.unfuwa.ngservice.dao.SpecialistDao;
import com.unfuwa.ngservice.dao.UserDao;
import com.unfuwa.ngservice.extendedmodel.ClientUser;
import com.unfuwa.ngservice.extendedmodel.SpecialistUser;
import com.unfuwa.ngservice.model.User;
import com.unfuwa.ngservice.ui.activity.client.MainClientActivity;
import com.unfuwa.ngservice.ui.activity.specialist.MainSpecialistActivity;
import com.unfuwa.ngservice.util.ProgressBarLoading;
import com.unfuwa.ngservice.util.database.DatabaseApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoadScreen extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView percentLoad;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private ProgressBarLoading progressBarLoading;

    private static final String CACHE_NAME = "token.txt";
    private String CACHE_PATH;

    private DatabaseApi dbApi;

    private void initComponents() {
        progressBar = findViewById(R.id.progress_bar);
        percentLoad = findViewById(R.id.percent_load);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initComponents();

        CACHE_PATH = getApplicationContext().getDataDir().getPath() + "/" + CACHE_NAME;

        progressBar.setMax(100);

        File file = new File(CACHE_PATH);

        Disposable disposable = Single.just(file)
                .filter(File::exists)
                .map(file1 -> {
                    FileReader fileReader = new FileReader(file1);
                    BufferedReader readFile = new BufferedReader(fileReader);

                    String content;

                    while((content = readFile.readLine()) != null) {
                        return content;
                    }

                    readFile.close();

                    return " ";
                })
                .filter(s -> !s.equals(" "))
                .toSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::autoSignIn, error -> startAuthActivity());

        compositeDisposable.add(disposable);

        progressBarAnimation();
    }

    private void progressBarAnimation() {
        progressBarLoading = new ProgressBarLoading(this, progressBar, percentLoad, 0, 100);
        progressBarLoading.setDuration(5000);
        progressBar.setAnimation(progressBarLoading);
    }

    private void autoSignIn(String token) {
        dbApi = DatabaseApi.getInstance(getApplicationContext());
        UserDao userDao = dbApi.userDao();

        Disposable disposable = userDao.getUserByToken(token)
                .delaySubscription(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::startNextActivity, Throwable::printStackTrace);

        compositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    private void startNextActivity(User user) {
        Disposable disposable;

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
    }

    private void startMainClientActivity(ClientUser client) {
        Toast.makeText(getApplicationContext(), "Авторизация прошла успешно, получен доступ клиента!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainClientActivity.class);
        intent.putExtra("client", client);
        startActivity(intent);
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

    private void startAuthActivity() {
        Intent intent = new Intent(this, AuthorizationActivity.class);
        startActivity(intent);
    }
}