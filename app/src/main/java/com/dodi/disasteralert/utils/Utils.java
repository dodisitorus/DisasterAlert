package com.dodi.disasteralert.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Utils {

    public static void hideSoftKey(View view) {
        if (view.getContext() != null) {
            InputMethodManager iml = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (iml != null) {
                iml.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}