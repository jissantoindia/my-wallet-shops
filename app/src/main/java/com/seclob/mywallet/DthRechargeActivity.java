package com.seclob.mywallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DthRechargeActivity extends AppCompatActivity {

    TextInputEditText customerId,dthCustomerName,dthCustomerMobileNumber,dthrechargeAmount;
    AutoCompleteTextView providerList;
    ArrayAdapter<String> providerAdapter;
    String provider,pcode;
    int proid;
    TextView DthPlanDetails,DthCurrentBalance,DthGetCustomerDetails;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dth_recharge);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("DTH Recharge");
        providerList = findViewById(R.id.DthproviderList);
        customerId = findViewById(R.id.customerId);
        dthCustomerName = findViewById(R.id.dthCustomerName);
        dthCustomerMobileNumber = findViewById(R.id.dthCustomerMobileNumber);
        dthrechargeAmount = findViewById(R.id.dthrechargeAmount);
        DthPlanDetails = findViewById(R.id.DthPlanDetails);
        DthCurrentBalance = findViewById(R.id.DthCurrentBalance);
        DthGetCustomerDetails = findViewById(R.id.DthGetCustomerDetails);

        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);


        try {
            Intent intent = getIntent();
            provider=intent.getStringExtra("provider");
            pcode=intent.getStringExtra("pcode");
            proid = Integer.parseInt(provider);
            providerList.setText(ProviderList[proid]);

        }catch (Exception e)
        {
        }

        customerId.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length() >5)
                {
                    DthGetCustomerDetails.setVisibility(View.VISIBLE);
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

        providerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, ProviderList);

    }

    private static final String[] ProviderList = new String[] {
            "TataSky", "Sun Direct", "Dish TV", "VideoCon D2H", "Airtel DTH"
    };

    private static final String[] ProviderCode = new String[] {
            "TSK", "SUN", "DSH", "VDH", "ART"
    };

    void Loader(Boolean status)
    {
        LinearLayout loader = findViewById(R.id.loader_dthReachrge);
        if(status)
            loader.setVisibility(View.VISIBLE);
        else
            loader.setVisibility(View.GONE);
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

    public void GetCustomerDetails(View view)
    {
        GetCustomerInfo();
    }

    public void GetCustomerInfo()
    {
        Loader(true);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.dthcusinfo)+"&operator_code="+ProviderCode[proid]+"&customer_id="+customerId.getText().toString();
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {


                        Log.i("VOLLEYES", response);

                        try {
                            Loader(false);
                            JSONObject Res=new JSONObject(response);
                            if(Res.getString("status").equalsIgnoreCase("true")) {
                                String data = Res.getString("data");

                                JSONObject rst = new JSONObject(data);
                                DthPlanDetails.setVisibility(View.VISIBLE);
                                DthCurrentBalance.setVisibility(View.VISIBLE);
                                DthPlanDetails.setText("Current Plan: "+ rst.getString("customer_plan_name")+"("+rst.getString("customer_plan_amount")+")");
                                DthCurrentBalance.setText("Current Balance: "+rst.getString("customer_balance"));
                                dthCustomerName.setText(rst.getString("customer_name"));

                            }

                        }catch (Exception e){
                            Log.e("catcherror",e+"d");

                            Toast.makeText(getApplicationContext(), "Network Error: "+e, Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(getApplicationContext(), "Network Error: "+errorString, Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username","username");
                params.put("password","password");

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
        if(providerList.length()>0 && customerId.length()>0 && dthrechargeAmount.length()>0)
        {
            DthRechargeApi();
        }else
        {
            Toast.makeText(this, "Some required Fields are empty!", Toast.LENGTH_SHORT).show();
        }
    }

    public void DthRechargeApi()
    {
        Loader(true);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"shop/recharge/mobile?type=DTH&user_id="+sharedPreferences.getString("id","")+"&circle=&mobile="+customerId.getText().toString()+"&operator="+pcode+"&amount="+dthrechargeAmount.getText().toString();
        StringRequest request = new StringRequest(Request.Method.GET, URL,
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

//                                new SweetAlertDialog( DthRechargeActivity.this)
//                                        .setTitleText("Recharge Successful!")
//                                        .show();
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        finish();
//                                    }
//                                }, 3000);

                                finish();
                                Toast.makeText(getApplicationContext(), "Recharge Processing...", Toast.LENGTH_LONG).show();

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
                params.put("customer",dthCustomerName.getText().toString());
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

}