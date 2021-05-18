package com.unfuwa.ngservice.ui.fragment.client;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ServiceCentersFragment extends Fragment implements OnMapReadyCallback {

    private static final float ZOOM = 15;

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

        loadingDialog = new LoadingDialog(mainClientActivity);

        dbApi = DatabaseApi.getInstance(context);
        filialCityDao = dbApi.filialCityDao();

        Disposable disposable = filialCityDao.getListFilials()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::addServiceCenters, throwable -> showErrorMessage());
    }

    private void addServiceCenters(List<FilialCity> listAllItems) {
        listFilials = new ArrayList<>(listAllItems);
        listMarkersNearNearbyFilials = new ArrayList<>();

        Geocoder geocoder = new Geocoder(context);
        List<Address> list;

        Circle circle = googleMap.addCircle(new CircleOptions()
                .center(myLocation)
                .radius(20000)
                .strokeColor(R.color.DarkBlack)
                .fillColor(R.color.DarkGreenCyan));

        try {
            if (listFilials.size() != 0) {
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

                    if ((Math.pow((x - x0), 2) + Math.pow((y - y0), 2)) <= (Math.pow(r, 2))) {
                        listMarkersNearNearbyFilials.add(googleMap.addMarker(new MarkerOptions().position(location).title(address.getAddressLine(0))));
                    }
                }
            } else {
                Toast.makeText(context, "Получен пустой список сервисных центров!", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showErrorMessage() {
        Toast.makeText(context, "Возникла ошибка во время загрузки сервис центров!", Toast.LENGTH_SHORT).show();
    }

    public void getMyLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        try {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location currentLocation = (Location) task.getResult();
                        myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                        googleMap.clear();

                        googleMap.addMarker(new MarkerOptions().position(myLocation).title("Мое местоположение"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, ZOOM));
                    } else {
                        Toast.makeText(context, "Возникла ошибка при определении вашего местоположения!", Toast.LENGTH_SHORT).show();
                    }
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