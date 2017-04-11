package com.fpballot;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity {
    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)	!= PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }

        activity = LoginActivity.this;
        findViews();
    }

    private void findViews() {
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                if (Utils.isEmpty(edtEmail.getText().toString().trim()) || edtEmail.length() <= 0)
                    Utils.showAlert(activity, getString(R.string.app_name), getString(R.string.alert_email));
                else if (!Utils.isValidEmail(edtEmail.getText().toString().trim()))
                    Utils.showAlert(activity, getString(R.string.app_name), getString(R.string.alert_valid_email));
                else if (Utils.isEmpty(edtPassword.getText().toString().trim()) || edtPassword.length() <= 0)
                    Utils.showAlert(activity, getString(R.string.app_name), getString(R.string.alert_password));
                else {
                    if (Utils.isNetworkAvailable(activity, true, false)) {
                        ShowProgressDialog(LoginActivity.this, getString(R.string.please_wait));
                        login();
                    }
                }
                break;
        }
    }

    private void login() {
        StringRequest postRequest = new StringRequest(Request.Method.POST, (Utils.URL + "login.php"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            hideProgressDialog();
                            JSONObject jobj = new JSONObject(response);
                            String ResponseCode = jobj.getString("ResponseCode");
                            String ResponseMsg = jobj.getString("ResponseMsg");

                            if (ResponseCode.equalsIgnoreCase("1")) {
                                if (jobj.has("data")) {
                                    JSONObject obj = jobj.getJSONObject("data");
                                    Preferences.setUserId(obj.getString("user_id"));

                                    if (ActivityCompat.checkSelfPermission(activity,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                        Toast.makeText(activity, "Please make sure to enable read phone state permission from settings", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    String strManufacturer = Build.MANUFACTURER;
                                    if (strManufacturer.equalsIgnoreCase("samsung")) {
                                        Intent i = new Intent(activity, SamsungFingerPrintActivity.class);
                                        startActivity(i);
                                    } else {
                                        Intent i = new Intent(activity, FingerprintActivity.class);
                                        startActivity(i);
                                    }
                                }
                            } else {
                                Utils.showAlert(activity, getResources().getString(R.string.app_name), ResponseMsg);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("error..", ": " + e);
                            hideProgressDialog();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Errorlist", String.valueOf(error));
                        hideProgressDialog();
                    }
                }
        )

        {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_email", edtEmail.getText().toString());
                params.put("password", edtPassword.getText().toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }
}
