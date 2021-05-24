package com.unfuwa.ngservice.ui.fragment.client;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.unfuwa.ngservice.R;
import com.unfuwa.ngservice.dao.FilialCityDao;
import com.unfuwa.ngservice.extendedmodel.FilialCity;
import com.unfuwa.ngservice.ui.activity.client.MainClientActivity;
import com.unfuwa.ngservice.ui.dialog.LoadingDialog;
import com.unfuwa.ngservice.util.database.DatabaseApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ServiceCentersFragment extends Fragment implements OnMapReadyCallback {

    private static final float ZOOM = 15;
    private final int ACCESS_LOCATION_PERMISSION = 2;

    private Context context;
    private View view;
    private GoogleMap googleMap;
    private MainClientActivity mainClientActivity;
    private LoadingDialog loadingDialog;

    private LatLng myLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private DatabaseApi dbApi;
    private FilialCityDao filialCityDao;

    private ArrayList<FilialCity> listFilials;
    private ArrayList<Marker> listMarkersNearNearbyFilials;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public ServiceCentersFragment(Context context, MainClientActivity mainClientActivity) {
        this.context = context;
        this.mainClientActivity = mainClientActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.service_centers_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        getMyLocation();
    }



    private void addServiceCenters(List<FilialCity> listAllItems) {
        if (googleMap != null && myLocation != null) {
            listFilials = new ArrayList<>(listAllItems);
            listMarkersNearNearbyFilials = new ArrayList<>();

            Geocoder geocoder = new Geocoder(context);
            List<Address> list;

            try {
                if (listFilials.size() != 0) {
                    Circle circle = googleMap.addCircle(new CircleOptions()
                            .center(myLocation)
                            .radius(20000)
                            .strokeColor(R.color.DarkBlack)
                            .fillColor(R.color.DarkGreenCyan));

                    for (FilialCity filialCity : listFilials) {
                        list = geocoder.getFromLocationName(
                                filialCity.getCity().get(0).getNameRegion()
                                        + "," + filialCity.getFilial().getNameCity()
                                        + "," + filialCity.getFilial().getNameStreet(), 1);

                        Address address = list.get(0);
                        LatLng location = new LatLng(address.getLatitude(), address.getLongitude());

                        double x0 = myLocation.latitude;
                        double y0 = myLocation.longitude;
                        double x = address.getLatitude();
                        double y = address.getLongitude();
                        double r = circle.getRadius();

                        //double distance1 = Math.pow((x - x0), 2) + Math.pow((y - y0), 2);
                        double distance1 = 2 * 6371000 * Math.asin(Math.sqrt(Math.pow((Math.sin((x0 * (3.14159 / 180) - x * (3.14159 / 180)) / 2)), 2) + Math.cos(x0 * (3.14159 / 180)) * Math.cos(x * (3.14159 / 180)) * Math.sin(Math.pow(((y0 * (3.14159 / 180) - y * (3.14159 / 180)) / 2), 2))));
                        double distance2 = r;

                        Log.d("FILIAL", String.valueOf(distance1));
                        Log.d("FILIAL", String.valueOf(distance2));

                        if (distance1 < distance2) {
                            listMarkersNearNearbyFilials.add(googleMap.addMarker(new MarkerOptions().position(location).title(address.getAddressLine(0))));
                        }
                    }
                } else {
                    Toast.makeText(context, "Получен пустой список сервисных центров!", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "Ошибка Google Services! (Отсутсвует Интернет подключение или не вкл. передача данных о местоположении)", Toast.LENGTH_SHORT).show();
        }
    }

    private void showErrorMessage() {
        Toast.makeText(context, "Возникла ошибка во время загрузки сервис центров!", Toast.LENGTH_SHORT).show();
    }

    public void getMyLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        try {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    Location currentLocation = (Location) task.getResult();
                    myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                    googleMap.clear();

                    googleMap.addMarker(new MarkerOptions().position(myLocation).title("Мое местоположение"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, ZOOM));

                    loadingDialog = new LoadingDialog(mainClientActivity);

                    dbApi = DatabaseApi.getInstance(context);
                    filialCityDao = dbApi.filialCityDao();

                    Disposable disposable = filialCityDao.getListFilials()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::addServiceCenters, throwable -> throwable.printStackTrace());

                    compositeDisposable.add(disposable);
                } else {
                    Toast.makeText(context, "Возникла ошибка при определении вашего местоположения!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }
}