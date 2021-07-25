/*
 * Copyright (c) 2021. rogergcc
 */

package com.appsnipp.education;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class Utils {

    public static void showDialog(Context c,String title, String message, String btn_text) {
        new AlertDialog.Builder(c).setTitle(title).setMessage(message).setPositiveButton(btn_text, (dialogInterface, i) -> {
        }).create().show();
    }


}
