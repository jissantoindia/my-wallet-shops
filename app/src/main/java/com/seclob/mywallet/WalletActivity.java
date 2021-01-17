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
import android.widget.DatePicker;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalletActivity extends AppCompatActivity {

    TextView From,ToPic,All;
    private int mYear, mMonth, mDay;
    String FromDate="",ToDate="",DateData="",fromdate="";
    Boolean isFromDate=false,isToDate=false;
    List<StatementModel> statementModels = new ArrayList<>(1000);
    private StatementAdaptor statementAdaptor;
    RecyclerView statementRecyclerView;
    SharedPreferences sharedPreferences;
    TextView walletBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        From = findViewById(R.id.walletFrom);
        ToPic = findViewById(R.id.walletTo);
        All = findViewById(R.id.walletAll);
        walletBalance = findViewById(R.id.walletBalance);
        getSupportActionBar().setTitle("Wallet");
        statementRecyclerView = findViewById(R.id.walletRecyclerView);
        statementAdaptor = new StatementAdaptor(getApplication());
        statementRecyclerView.setAdapter(statementAdaptor);
        statementRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        statementRecyclerView.setNestedScrollingEnabled(false);
        walletBalance.setText(sharedPreferences.getString("balance",""));
        GetStatement();

    }

    void getDate(final String Type)
    {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(WalletActivity.this,
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
        LinearLayout loader = findViewById(R.id.loader_wallet);
        if(status)
            loader.setVisibility(View.VISIBLE);
        else
            loader.setVisibility(View.GONE);
    }

    public void GetStatement()
    {
        Loader(true);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        if(isFromDate)
        {if(isToDate)
        {
            fromdate = "&fdate="+FromDate+"&tdate="+ToDate;
        }else
        {
            fromdate = "&fdate=&tdate=";

        }
        }else
        {
            fromdate = "&fdate=&tdate=";
        }
        String URL = getString(R.string.api_url)+"shop/statment?type=&user_id="+sharedPreferences.getString("id","")+"&limit=1000&offset=0"+fromdate;
        StringRequest request = new StringRequest(Request.Method.GET, URL,
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
                            walletBalance.setText(Res.getString("wallet"));
                            if(sts.equalsIgnoreCase("01")) {
                                String data = Res.getString("amounts");
                                JSONArray Results = new JSONArray(data);
                                statementModels.clear();
                                int len = (Results.length()>5)?5:Results.length();
                                for (int i = 0; i < len; i++) {
                                    String Result = Results.getString(i);
                                    JSONObject rst = new JSONObject(Result);
                                    if(rst.getString("amount_type").equalsIgnoreCase("Credit")) {
                                        StatementModel statementModel = new StatementModel();
                                        statementModel.setAmount(rst.getString("amount"));
                                        statementModel.setDate(rst.getString("created_at"));
                                        statementModel.setType(rst.getString("amount_type"));
                                        statementModels.add(statementModel);
                                    }
                                }
                                statementAdaptor.renewItems(statementModels);

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
                params.put("shopid",sharedPreferences.getString("shop_id",""));
                if(isFromDate)
                {if(isToDate)
                {
                    params.put("fromdate",FromDate);
                    params.put("todate",ToDate);
                }
                }
                params.put("limit","100");
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