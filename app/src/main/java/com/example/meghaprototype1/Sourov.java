package com.example.meghaprototype1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class Sourov {
    private Context context;
    AlertDialog progressDialog;
    Activity activity;

    public Sourov(Context context) {
        this.context = context;
        this.activity = (Activity) context;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View progressView = layoutInflater.inflate(R.layout.progress_bar, null);
        progressDialog = new AlertDialog.Builder(context).create();
        progressDialog.setView(progressView);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }

    public AlertDialog spinner() {
        return progressDialog;
    }
}
