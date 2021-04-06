package com.unfuwa.ngservice.ui.fragment.client;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.unfuwa.ngservice.R;

public class MainClientFragment extends Fragment {

    private View view;

    private TextView addressMainCenterCompany;
    private TextView telephoneCompany;
    private TextView emailCompany;
    private TextView URLVKCompany;
    private TextView URLInstagramCompany;
    private TextView URLWhatsAppCompany;

    private static final String ADDRESS_MAIN_CENTER = "";
    private static final String TELEPHONE = "+7 (800) 555-35-35";
    private static final String EMAIL = "";
    private static final String URL_VK = "";
    private static final String URL_INSTAGRAM = "";
    private static final String URL_WHATS_APP = "";

    private void initComponents() {
        addressMainCenterCompany = view.findViewById(R.id.address_main_center_company);
        telephoneCompany = view.findViewById(R.id.telephone_company);
        emailCompany = view.findViewById(R.id.email_company);
        URLVKCompany = view.findViewById(R.id.url_vk_company);
        URLInstagramCompany = view.findViewById(R.id.url_instagram_company);
        URLWhatsAppCompany = view.findViewById(R.id.url_whats_app_company);

        addressMainCenterCompany.setText(addressMainCenterCompany.getText() + " " + ADDRESS_MAIN_CENTER);
        telephoneCompany.setText(telephoneCompany.getText() + " " + TELEPHONE);
        emailCompany.setText(emailCompany.getText() + " " + EMAIL);
        URLVKCompany.setText(URLVKCompany.getText() + " " + URL_VK);
        URLInstagramCompany.setText(URLInstagramCompany.getText() + " " + URL_INSTAGRAM);
        URLWhatsAppCompany.setText(URLWhatsAppCompany.getText() + " " + URL_WHATS_APP);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.main_client_fragment, container, false);
        initComponents();
        return view;
    }
}