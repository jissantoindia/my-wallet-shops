package com.seclob.mywallet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommisionActivity extends AppCompatActivity {
    TextInputEditText inputPasscode;
    LinearLayout CodeView;
    SharedPreferences sharedPreferences;

    List<RecentDthRechargeModel> mobileDthModels = new ArrayList<>(1000);
    private RecentDthRechargeAdaptor recentDthRechargeAdaptor;
    RecyclerView mobileDthRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commision);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Commission Details");
        inputPasscode = findViewById(R.id.inputPasscode);
        CodeView = findViewById(R.id.CodeView);
        inputPasscode.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length() == 6)
                {
                    if(s.toString().equalsIgnoreCase("pt1234"))
                        CodeView.setVisibility(View.GONE);
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
        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);
        mobileDthRecyclerView = findViewById(R.id.ComRecyclerView);
        recentDthRechargeAdaptor = new RecentDthRechargeAdaptor(getApplication());
        mobileDthRecyclerView.setAdapter(recentDthRechargeAdaptor);
        mobileDthRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        mobileDthRecyclerView.setNestedScrollingEnabled(false);
        LoginApi();
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

    public void LoginApi()
    {
        Loader(true);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"shop/commission?user_id="+sharedPreferences.getString("id","");
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.i("VOLLEYES", response);
                        try {
                            Loader(false);
                            JSONObject Res=new JSONObject(response);
                            String sts    = Res.getString("sts");
                            String msg    = Res.getString("msg");

                            if(sts.equalsIgnoreCase("01"))
                            {

                                String data = Res.getString("plan");
                                JSONArray Results = new JSONArray(data);
                                mobileDthModels.clear();
                                for (int i = 0; i < Results.length(); i++) {
                                    String Result = Results.getString(i);
                                    JSONObject rst = new JSONObject(Result);
                                    RecentDthRechargeModel recentDthRechargeModel = new RecentDthRechargeModel();
                                    getSupportActionBar().setTitle(rst.getString("planname").toUpperCase());
                                    recentDthRechargeModel.setProvider(rst.getString("operatorname"));
                                    recentDthRechargeModel.setDis(rst.getString("opetatortypename"));
                                    recentDthRechargeModel.setStatus(rst.getString("amount")+"%");
                                    mobileDthModels.add(recentDthRechargeModel);
                                }
                                recentDthRechargeAdaptor.renewItems(mobileDthModels);

                            }
                            Toast.makeText(CommisionActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e)
                        {
                            Log.e("catcherror",e+"d");
                            Toast.makeText(CommisionActivity.this, "Catch Error :"+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if(response != null && response.data != null)
                        {
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            Loader(false);
                            Toast.makeText(CommisionActivity.this, "Network Error :"+errorString, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username","");
                Log.i("loginp ", params.toString());
                return params;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }

    void Loader(Boolean status)
    {
        LinearLayout loader = findViewById(R.id.loader_Comm);
        if(status)
            loader.setVisibility(View.VISIBLE);
        else
            loader.setVisibility(View.GONE);
    }

}