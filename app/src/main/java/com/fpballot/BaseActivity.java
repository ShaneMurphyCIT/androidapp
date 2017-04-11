package com.fpballot;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;


public class BaseActivity extends AppCompatActivity {

    public static ProgressDialog dialog;

    public static void ShowProgressDialog(Activity activity, String message){
        if(dialog != null){
            dialog.dismiss();
        }
        try{
            dialog = new ProgressDialog(activity);
            dialog.setMessage(message);
            dialog.setCancelable(false);
            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void hideProgressDialog(){
        if(dialog != null && dialog.isShowing())
            dialog.dismiss();
    }
}
