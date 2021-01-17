package com.seclob.mywallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DthRefreshActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String RechargeID;
    TextView RechID,CusNum,CusID,Operator,Amt,Date,Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dth_refresh);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("DTH Refresh");
        RechID = findViewById(R.id.DthRechargeID_details);
        CusNum = findViewById(R.id.CusName_details);
        Operator = findViewById(R.id.DthRechOp_details);
        CusID = findViewById(R.id.CusNum_details);
        Amt = findViewById(R.id.DthRechAmount_details);
        Date = findViewById(R.id.DthRechDT_details);
        Status = findViewById(R.id.DthRechSts_details);
        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);
        try {
            Intent intent = getIntent();
            RechargeID=intent.getStringExtra("RechargeID");
            KsebRechargeApi();
            //Toast.makeText(this, RechargeID, Toast.LENGTH_SHORT).show();
        }catch (Exception e)
        {
        }
    }
    void Loader(Boolean status)
    {
        LinearLayout loader = findViewById(R.id.loader_mrecharge_details);
        if(status)
            loader.setVisibility(View.VISIBLE);
        else
            loader.setVisibility(View.GONE);
    }

    public void ShareApp(View view)
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "MY WALLET\n Your DTH Recharge of Rs."+Amt.getText().toString()+" towards the Customer ID "+CusID.getText().toString()+" was successful.");
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
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

    public void KsebRechargeApi()
    {
        Loader(true);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.recharge_api)+"dthreachargedetails";
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

                                String data = Res.getString("dthreachdetails");
                                JSONArray Results = new JSONArray(data);
                                String Result = Results.getString(0);
                                JSONObject rst = new JSONObject(Result);
                                RechID.setText("MYWT"+rst.getString("dth_id"));
                                CusID.setText(rst.getString("dth_customerid"));
                                CusNum.setText(rst.getString("dth_customer"));
                                Operator.setText(rst.getString("dth_operator"));
                                Amt.setText(rst.getString("dth_amount"));
                                Date.setText(rst.getString("dth_date"));
                                Status.setText(rst.getString("dth_status"));
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
                params.put("dth_id",RechargeID);
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

    public void complaintBtn(View view)
    {ChangeStatus("Refresh");}

    public void ChangeStatus(final String ssts)
    {
        Loader(true);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.recharge_api)+"dthreachargestatus";
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

                                KsebRechargeApi();
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
                params.put("dth_id",RechargeID);
                params.put("status",ssts);

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