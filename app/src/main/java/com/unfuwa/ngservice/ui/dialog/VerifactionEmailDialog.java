package com.unfuwa.ngservice.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.unfuwa.ngservice.R;

public class VerifactionEmailDialog {

    private Activity activity;
    private AlertDialog alertDialog;

    public VerifactionEmailDialog(Activity activity) {
        this.activity = activity;
    }

    public void showVerification() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.verification_email_dialog, null));
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();
    }

    public void hideVerification() {
        alertDialog.dismiss();
    }
}
