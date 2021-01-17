package com.seclob.mywallet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Switch;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DthHistoryActivity extends AppCompatActivity {

    TextView From,ToPic,All;
    private int mYear, mMonth, mDay;
    String FromDate="",ToDate="",DateData="";
    Boolean isFromDate=false,isToDate=false;
    List<RecentDthRechargeModel> mobileDthModels = new ArrayList<>(1000);
    private RecentDthRechargeAdaptor recentDthRechargeAdaptor;
    RecyclerView mobileDthRecyclerView;
    SharedPreferences sharedPreferences;
    Boolean isRefreshOnly=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dth_history);

        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        From = findViewById(R.id.DhFrom);
        ToPic = findViewById(R.id.DhTo);
        All = findViewById(R.id.DhAll);
        getSupportActionBar().setTitle("DTH History");
        mobileDthRecyclerView = findViewById(R.id.DthHistoryRecyclerView);
        recentDthRechargeAdaptor = new RecentDthRechargeAdaptor(getApplication());
        mobileDthRecyclerView.setAdapter(recentDthRechargeAdaptor);
        mobileDthRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        mobileDthRecyclerView.setNestedScrollingEnabled(false);

        GetStatement();

        Switch sw = (Switch) findViewById(R.id.RefreshDth);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isRefreshOnly=true;
                    GetStatement();
                } else {
                    isRefreshOnly=false;
                    GetStatement();
                }
            }
        });
    }

    void getDate(final String Type)
    {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(DthHistoryActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        DateData = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        if(Type.equalsIgnoreCase("From"))
                        {From.setText(DateData);
                            isFromDate=true;
                            FromDate=DateData;}
                        else
                        {ToPic.setText(DateData);
                            isToDate = true;
                            GetStatement();
                            ToDate = DateData;}
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    public String getcurrentDateAndTime(){
        Date c;
        c = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String formattedDate = simpleDateFormat.format(c);
        return formattedDate;
    }


    public void statementAll(View view)
    {
        isFromDate=isToDate=false;
        From.setText("From");
        ToPic.setText("To");
        GetStatement();
    }
    public void statementFrom(View view)
    {
        getDate("From");
    }
    public void statementTo(View view)
    {
        getDate("To");

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
        LinearLayout loader = findViewById(R.id.loader_Dh);
        if(status)
            loader.setVisibility(View.VISIBLE);
        else
            loader.setVisibility(View.GONE);
    }

    public void GetStatement()
    {
        Loader(true);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.recharge_api)+"dthreachargeall";
        StringRequest request = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {


                        Log.i("VOLLEYES", response);

                        try {
                            Loader(false);
                            JSONObject Res=new JSONObject(response);
                            String sts    = Res.getString("sts");
                            String msg    = Res.getString("msg");

                            if(sts.equalsIgnoreCase("01")) {
                                String data = Res.getString("dthreachlist");
                                JSONArray Results = new JSONArray(data);
                                mobileDthModels.clear();
                                for (int i = 0; i < Results.length(); i++) {
                                    String Result = Results.getString(i);
                                    JSONObject rst = new JSONObject(Result);
                                    RecentDthRechargeModel recentDthRechargeModel = new RecentDthRechargeModel();
                                    recentDthRechargeModel.setMobile(rst.getString("dth_customerid"));
                                    recentDthRechargeModel.setProvider(rst.getString("dth_operator"));
                                    recentDthRechargeModel.setDis(rst.getString("dth_paidamount")+" on " + rst.getString("dth_date"));
                                    recentDthRechargeModel.setStatus(rst.getString("dth_status"));
                                    recentDthRechargeModel.setIdRecharge(rst.getString("dth_id"));
                                    mobileDthModels.add(recentDthRechargeModel);
                                }
                                recentDthRechargeAdaptor.renewItems(mobileDthModels);

                            }
                        }catch (Exception e){
                            Log.e("catcherror",e+"d");


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
                params.put("shop_id",sharedPreferences.getString("shop_id",""));
                if(isFromDate)
                {if(isToDate)
                {
                    params.put("fromdate",FromDate);
                    params.put("todate",ToDate);
                }
                }
                if(isRefreshOnly)
                {
                    params.put("status","Refresh");
                }
                params.put("limit","500");
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