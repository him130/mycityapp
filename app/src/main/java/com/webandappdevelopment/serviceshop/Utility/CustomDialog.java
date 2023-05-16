package com.webandappdevelopment.serviceshop.Utility;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.webandappdevelopment.serviceshop.R;

public class CustomDialog {
    public CustomDialog(Context context, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_ServiceShop_AlertDialogTheme);
        builder.setTitle("Attention Required!!")
                .setMessage(message)
                .setCancelable(true)
                .setNegativeButton("OK", (dialog, which) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }
}
