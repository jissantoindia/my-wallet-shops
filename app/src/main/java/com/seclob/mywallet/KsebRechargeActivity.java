package com.seclob.mywallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class KsebRechargeActivity extends AppCompatActivity {

    TextView ksebPlanDetails,ksebData;
    TextInputEditText ksebBillAmount,consumerNumber;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kseb_recharge);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("KSEB Bill Payment");
        ksebPlanDetails = findViewById(R.id.ksebPlanDetails);
        ksebData = findViewById(R.id.ksebData);
        ksebBillAmount = findViewById(R.id.ksebBillAmount);
        consumerNumber = findViewById(R.id.consumerNumber);
        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);

        consumerNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length()==13)
                {


                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return true;
    }

    void Loader(Boolean status)
    {
        LinearLayout loader = findViewById(R.id.loader_ksebReachrge);
        if(status)
            loader.setVisibility(View.VISIBLE);
        else
            loader.setVisibility(View.GONE);
    }

    public void KsebRechargeApi()
    {
        Loader(true);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.recharge_api)+"electricreachargeadd";
        StringRequest request = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {


                        Log.i("VOLLEYES", response);
                        Loader(false);
                        try {
                            JSONObject Res=new JSONObject(response);
                            String sts    = Res.getString("sts");
                            String msg    = Res.getString("msg");

                            if(sts.equalsIgnoreCase("01"))
                            {

                                new SweetAlertDialog(KsebRechargeActivity.this)
                                        .setTitleText("Bill Payment Successful!")
                                        .show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 3000);
//                                finish();
//                                Toast.makeText(getApplicationContext(), "Bill Payment Successful!", Toast.LENGTH_LONG).show();

                            }else
                            {
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            }

                        }catch (Exception e){
                            Log.e("catcherror",e+"d");

                            Toast.makeText(getApplicationContext(), "Catch Error :"+e, Toast.LENGTH_SHORT).show();

                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if(response != null && response.data != null){
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            Loader(false);
                            Toast.makeText(getApplicationContext(), "Network Error :"+errorString, Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("state","Kerala");
                params.put("board","KSEB");
                params.put("consumernumber",consumerNumber.getText().toString());
                params.put("billamount",ksebBillAmount.getText().toString());
                params.put("paidamount",ksebBillAmount.getText().toString());
                params.put("customername","N/A");
                params.put("mobilenumber","N/A");
                params.put("shop_id",sharedPreferences.getString("shop_id",""));

                Log.i("loginp ", params.toString());

                return params;
            }

        };


        // Add the realibility on the connection.
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));

        // Start the request immediately
        queue.add(request);

    }

    public void RechangeNowBtn(View view)
    {
        if(consumerNumber.length()>0 && ksebBillAmount.length()>0)
        {
            KsebRechargeApi();
        }else
        {
            Toast.makeText(this, "Some required Fields are empty!", Toast.LENGTH_SHORT).show();
        }
    }

}