package com.hanzhuang42.showme.util;

import android.content.Context;
import android.widget.Toast;

public class MyToast {

    private static Toast toast=null;

    public static void showToast(Context context, String content, int type) {
        if (toast == null) {
            toast = Toast.makeText(context,content,type);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

}
