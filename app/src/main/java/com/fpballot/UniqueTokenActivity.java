package com.fpballot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import java.util.Random;

import static com.fpballot.R.id.btnLogin;
import static com.fpballot.R.id.edtEmail;
import static com.fpballot.R.id.edtPassword;

public class UniqueTokenActivity extends BaseActivity {
    private TextView tvToken;
    private Button btnDisplayToken;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unique_token);

        activity = UniqueTokenActivity.this;
        findViews();
    }

    private void findViews() {
        tvToken = (TextView) findViewById(R.id.tvToken);
        btnDisplayToken = (Button) findViewById(R.id.btnDisplayToken);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDisplayToken:
                Random r = new Random(System.currentTimeMillis());
                tvToken.setText(String.valueOf(10000 + r.nextInt(10000)));
                if (Utils.isNetworkAvailable(activity, true, false)) {
                    ShowProgressDialog(UniqueTokenActivity.this, getString(R.string.please_wait));
                    generateToken();
                }
                break;
        }
    }

    private void generateToken() {
        StringRequest postRequest = new StringRequest(Request.Method.POST, (Utils.URL+"sendNumber.php"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jobj = new JSONObject(response);
                            String ResponseMsg = jobj.getString("ResponseMsg");
                            Utils.showAlert(activity, getResources().getString(R.string.app_name), ResponseMsg);
                            hideProgressDialog();
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
                params.put("unique_number", tvToken.getText().toString());
                params.put("user_id", Preferences.getUserId());
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
