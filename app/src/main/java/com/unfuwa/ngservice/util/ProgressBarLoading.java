package com.unfuwa.ngservice.util;

import android.content.Context;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.unfuwa.ngservice.ui.activity.general.AuthorizationActivity;

public class ProgressBarLoading extends Animation {

    private Context context;

    private ProgressBar progressBar;
    private TextView textView;

    private float from;
    private float to;

    public ProgressBarLoading(Context context, ProgressBar progressBar, TextView textView, float from, float to) {
        this.context = context;
        this.progressBar = progressBar;
        this.textView = textView;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);

        float value = from + (to - from) * interpolatedTime;

        progressBar.setProgress((int)value);
        textView.setText(String.valueOf((int)value) + " " + "%");

        if (value == to) {
            Intent intent = new Intent(context, AuthorizationActivity.class);
            context.startActivity(intent);
        }
    }
}
