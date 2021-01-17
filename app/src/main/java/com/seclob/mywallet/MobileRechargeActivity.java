package com.seclob.mywallet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

public class MobileRechargeActivity extends AppCompatActivity {

    Boolean providerlist=false,providercircle=false;
    TextView viewPlans;
    String type,provider,offertype="Normal",District,Dvalue,ProviderCode="",CircleCode;
    SharedPreferences sharedPreferences;
    int proid;
    Button RechargeBtn;


    TextInputEditText rechargeAmount,mobileNumber;
    AutoCompleteTextView providerList,providerCircle;
    ArrayAdapter<String> providerAdapter,providerCircleAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_recharge);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Mobile Recharge");

        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);
        rechargeAmount = findViewById(R.id.rechargeAmount);
        mobileNumber = findViewById(R.id.mobileNumber);
        providerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, ProviderList);
        providerCircleAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, ProviderCircle);
        providerList = findViewById(R.id.providerList);
        viewPlans = findViewById(R.id.viewPlans);

        RechargeBtn = findViewById(R.id.RechangeNowBtn);


        try {
            Intent intent = getIntent();
            type=intent.getStringExtra("type");
            provider=intent.getStringExtra("provider");
            proid = Integer.parseInt(provider);
            providerList.setText(ProviderList[proid]);
            providerlist=true;
            getSupportActionBar().setTitle("Mobile Recharge "+type);

        }catch (Exception e)
        {
            getSupportActionBar().setTitle("Mobile Recharge");
        }

        providerCircle = findViewById(R.id.providerCircle);
        providerList.setAdapter(providerAdapter);
        providerCircle.setAdapter(providerCircleAdapter);
        providerCircle.setText("Kerala");
        //providerCircle.setText(ProviderCircle[1]);
        providerCircle.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                if(type.equalsIgnoreCase("Prepaid"))
                {providercircle=true;}
                if(providercircle && providerlist)
                {
                    viewPlans.setVisibility(View.VISIBLE);
                    providerCircle.setVisibility(View.VISIBLE);
                }else
                {
                    viewPlans.setVisibility(View.GONE);
                    providerCircle.setVisibility(View.GONE);

                }

            }
        });

        providerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                providerlist=true;
                if(providercircle && providerlist)
                {
                    viewPlans.setVisibility(View.VISIBLE);
                }else
                {
                    viewPlans.setVisibility(View.GONE);
                }

            }
        });





    }

    void Loader(Boolean status)
    {
       LinearLayout loader = findViewById(R.id.loader_mobileReachrge);
       if(status)
           loader.setVisibility(View.VISIBLE);
       else
           loader.setVisibility(View.GONE);
    }

    public void RechangeNowBtn(View view)
    {
        if(providerList.length()>0 && providerCircle.length()>0 && mobileNumber.length()>0 && rechargeAmount.length()>0)
        {
            RechargeBtn.setEnabled(false);
            MobileRechargeApi();
        }else
        {
            Toast.makeText(this, "Some required Fields are empty!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        if(sharedPreferences.getString("mramt","").length()>0)
        {rechargeAmount.setText(sharedPreferences.getString("mramt",""));}
        super.onResume();
    }

    public void ViewPlansBtn(View view)
    {
        Intent intents = new Intent(MobileRechargeActivity.this, PlansActivity.class);
        intents.putExtra("prid",providerList.getText().toString().toLowerCase());
        intents.putExtra("circle",providerCircle.getText().toString().toLowerCase());
        startActivity(intents);
    }

    private static final String[] ProviderList = new String[] {
            "Idea","Airtel", "Jio", "BSNL", "BSNL Special" ,"Vodafone"
    };


    private static final String[] ProviderCircle = new String[] {
            "Kerala",
            "Tamil Nadu",
            "Andhra Pradesh",
            "Chennai",
            "Assam",
            "Bihar & Jharkhand",
            "Delhi",
            "Uttar Pradesh (East)",
            "Gujarat",
            "Haryana",
            "Himachal Pradesh",
            "Jammu and Kashmir",
            "Karnataka",
            "Kolkata",
            "Mumbai",
            "North East",
            "Odisha",
            "Punjab",
            "Rajasthan",
            "West Bengal",
            "GOA"
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return true;
    }

    public void MobileRechargeApi()
    {
        if(providerCircle.getText().toString().equalsIgnoreCase("Kerala"))
            CircleCode = "11";
        else if(providerCircle.getText().toString().equalsIgnoreCase("Tamil Nadu"))
            CircleCode = "20";
        else if(providerCircle.getText().toString().equalsIgnoreCase("Andhra Pradesh"))
            CircleCode = "1";
        else if(providerCircle.getText().toString().equalsIgnoreCase("Assam"))
            CircleCode = "2";
        else if(providerCircle.getText().toString().equalsIgnoreCase("Bihar & Jharkhand"))
            CircleCode = "3";
        else if(providerCircle.getText().toString().equalsIgnoreCase("Delhi"))
            CircleCode = "5";
        else if(providerCircle.getText().toString().equalsIgnoreCase("Uttar Pradesh"))
            CircleCode = "34";
        else if(providerCircle.getText().toString().equalsIgnoreCase("Gujarat"))
            CircleCode = "6";
        else if(providerCircle.getText().toString().equalsIgnoreCase("Haryana"))
            CircleCode = "7";
        else if(providerCircle.getText().toString().equalsIgnoreCase("Himachal Pradesh"))
            CircleCode = "8";
        else if(providerCircle.getText().toString().equalsIgnoreCase("Jammu and Kashmir"))
            CircleCode = "9";
        else if(providerCircle.getText().toString().equalsIgnoreCase("Karnataka"))
            CircleCode = "10";
        else if(providerCircle.getText().toString().equalsIgnoreCase("Kolkata"))
            CircleCode = "12";
        else if(providerCircle.getText().toString().equalsIgnoreCase("Mumbai"))
            CircleCode = "15";
        else if(providerCircle.getText().toString().equalsIgnoreCase("Rajasthan"))
            CircleCode = "19";
        else if(providerCircle.getText().toString().equalsIgnoreCase("Punjab"))
            CircleCode = "18";
        else if(providerCircle.getText().toString().equalsIgnoreCase("Chennai"))
            CircleCode = "4";
        else if(providerCircle.getText().toString().equalsIgnoreCase("GOA"))
            CircleCode = "28";

        if(ProviderList[proid].equalsIgnoreCase("Idea"))
        {
            ProviderCode = (type.equalsIgnoreCase("Prepaid")?"ID":"IDP");
        } if(ProviderList[proid].equalsIgnoreCase("Vodafone"))
        {
            ProviderCode = (type.equalsIgnoreCase("Prepaid")?"VF":"VFP");
        }
        else if(ProviderList[proid].equalsIgnoreCase("Airtel"))
        {
            ProviderCode = (type.equalsIgnoreCase("Prepaid")?"AT":"ATP");
        }
        else if(ProviderList[proid].equalsIgnoreCase("Jio"))
        {
            ProviderCode = (type.equalsIgnoreCase("Prepaid")?"JIOX":"RJC");
        }
        else if(ProviderList[proid].equalsIgnoreCase("BSNL"))
        {
            ProviderCode = (type.equalsIgnoreCase("Prepaid")?"BSNL":"BSP");
        }
        else if(ProviderList[proid].equalsIgnoreCase("BSNL Special"))
        {
            ProviderCode = (type.equalsIgnoreCase("Prepaid")?"BSS":"BSS");
        }
        else if(ProviderList[proid].equalsIgnoreCase("BSNL - Individual"))
        {
            ProviderCode = (type.equalsIgnoreCase("Prepaid")?"BSL":"BSL");
        }

        String Circle ="&circle=";
        if(type.equalsIgnoreCase("Prepaid"))
        {
            Circle="&circle="+CircleCode;
        }
        Loader(true);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"shop/recharge/mobile?type="+type+"&user_id="+sharedPreferences.getString("id","")+"&mobile="+mobileNumber.getText().toString()+"&operator="+ProviderCode+Circle+"&amount="+rechargeAmount.getText().toString();
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.i("VOLLEYES", response);

                        try
                        {
                            JSONObject Res=new JSONObject(response);
                            String sts    = Res.getString("sts");
                            String msg    = Res.getString("msg");
                            if(sts.equalsIgnoreCase("01"))
                            {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        Loader(false);
                                        finish();
                                        Toast.makeText(MobileRechargeActivity.this, "Recharge Processing...", Toast.LENGTH_LONG).show();
                                    }
                                }, 6000);

                            }
                        }
                        catch (Exception e)
                        {
                            Loader(false);
                            Log.e("catcherror",e+"");
                            Toast.makeText(MobileRechargeActivity.this, "Catch Error :"+e, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MobileRechargeActivity.this, "Network Error :"+errorString, Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobilenumber",mobileNumber.getText().toString());
                params.put("operator",ProviderList[proid]);
                params.put("type",type);
                params.put("circle",providerCircle.getText().toString());
                params.put("amount",rechargeAmount.getText().toString());
                Log.i("loginp ", params.toString());
                return params;
            }

        };

        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1.0f));
        queue.add(request);

    }

}