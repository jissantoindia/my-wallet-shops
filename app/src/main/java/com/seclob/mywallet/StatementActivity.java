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
import android.widget.CompoundButton;
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

import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatementActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    List<StatementModel> statementModels = new ArrayList<>(1000);
    private StatementAdaptor statementAdaptor;
    RecyclerView statementRecyclerView;
    TextView From,ToPic,All;
    private int mYear, mMonth, mDay;
    String FromDate="",ToDate="",DateData="";
    Boolean isFromDate=false,isToDate=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);

        From = findViewById(R.id.statementFrom);
        ToPic = findViewById(R.id.statementTo);
        All = findViewById(R.id.statementAll);


        getSupportActionBar().setTitle("Statement");
        statementRecyclerView = findViewById(R.id.statementRecyclerView);
        statementAdaptor = new StatementAdaptor(getApplication());
        statementRecyclerView.setAdapter(statementAdaptor);
        statementRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        statementRecyclerView.setNestedScrollingEnabled(false);
        GetStatement();

    }

    void getDate(final String Type)
    {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(StatementActivity.this,
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
        LinearLayout loader = findViewById(R.id.loader_Satement);
        if(status)
            loader.setVisibility(View.VISIBLE);
        else
            loader.setVisibility(View.GONE);
    }

    public void GetStatement()
    {
        Loader(true);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.shop_api)+"shopamountlist";
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
                                String data = Res.getString("shopamount");
                                JSONArray Results = new JSONArray(data);
                                statementModels.clear();
                                for (int i = 0; i < Results.length(); i++) {
                                    String Result = Results.getString(i);
                                    JSONObject rst = new JSONObject(Result);
                                    StatementModel statementModel = new StatementModel();
                                    statementModel.setAmount(rst.getString("shopamount"));
                                    statementModel.setDate(rst.getString("amountdate"));
                                    statementModel.setType(rst.getString("amount_type"));
                                    statementModels.add(statementModel);
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