package com.unfuwa.ngservice.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.unfuwa.ngservice.R;

public class LoadingDialog {

    private Activity activity;
    private AlertDialog alertDialog;

    public LoadingDialog (Activity activity) {
        this.activity = activity;
    }

    public void showLoading() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_dialog, null));
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();
    }

    public void hideLoading() {
        alertDialog.dismiss();
    }
}
