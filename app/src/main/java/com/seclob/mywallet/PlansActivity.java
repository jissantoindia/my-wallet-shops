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
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlansActivity extends AppCompatActivity {

    String prid,circle,Cat="Top up";
    List<FruitModel1> list1 = new ArrayList<>(1000);
    private FruitAdapter1 PlacesAdapter;
    RecyclerView placesView;
    EditText placeSearchInput;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Choose Plan");
        getSupportActionBar().setElevation(0);
        placeSearchInput = findViewById(R.id.placeSearchInput);
        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);
        try {
            Intent intent = getIntent();
            prid=intent.getStringExtra("prid");
            circle=intent.getStringExtra("circle");
            ViewPlans(prid,circle);
        }catch (Exception e)
        {
        }

        placesView = (RecyclerView) findViewById(R.id.placesViewFrag);
        PlacesAdapter = new FruitAdapter1(getApplication());
        placesView.setAdapter(PlacesAdapter);
        placesView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        placeSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length() >0)
                {


                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                //SearchPlaces(s.toString());
                // filter your list from your input
                filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
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

    void filter(String text){
        List<FruitModel1> temp = new ArrayList();
        for(FruitModel1 fruitModel1: list1){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(fruitModel1.getAmt().toLowerCase().contains(text.toLowerCase())){
                temp.add(fruitModel1);
            }
        }
        //update recyclerview
        PlacesAdapter.updateList(temp);
    }
    void clearBox()
    {
        findViewById(R.id.Topup).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.FullTalktime).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.SMS).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.G2Data).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.G3Data).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.G3Data).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.G4Data).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.Local).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.STD).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.ISD).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.Roaming).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.Other).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.Validity).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.Plan).setBackground(getDrawable(R.drawable.ripple));
    }

    public void Topup(View view)
    {
        Cat="Top up";
        ViewPlans(prid,circle);
        clearBox();
        findViewById(R.id.Topup).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void FullTalktime(View view)
    {
        Cat="Full Talktime";
        ViewPlans(prid,circle);
        clearBox();
        findViewById(R.id.FullTalktime).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void SMS(View view)
    {
        Cat="SMS";
        ViewPlans(prid,circle);
        clearBox();
        findViewById(R.id.SMS).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void G2Data(View view)
    {
        Cat="2G Data";
        ViewPlans(prid,circle);
        clearBox();
        findViewById(R.id.G2Data).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void G3Data(View view)
    {
        Cat="3G Data";
        ViewPlans(prid,circle);
        clearBox();
        findViewById(R.id.G3Data).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void G4Data(View view)
    {
        Cat="4G Data";
        ViewPlans(prid,circle);
        clearBox();
        findViewById(R.id.G4Data).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void Local(View view)
    {
        Cat="Local";
        ViewPlans(prid,circle);
        clearBox();
        findViewById(R.id.Local).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void STD(View view)
    {
        Cat="STD";
        ViewPlans(prid,circle);
        clearBox();
        findViewById(R.id.STD).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void ISD(View view)
    {
        Cat="ISD";
        ViewPlans(prid,circle);
        clearBox();
        findViewById(R.id.ISD).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void Roaming(View view)
    {
        Cat="Roaming";
        ViewPlans(prid,circle);
        clearBox();
        findViewById(R.id.Roaming).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void Other(View view)
    {
        Cat="Other";
        ViewPlans(prid,circle);
        clearBox();
        findViewById(R.id.Other).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void Validity(View view)
    {
        Cat="Validity";
        ViewPlans(prid,circle);
        clearBox();
        findViewById(R.id.Validity).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void Plan(View view)
    {
        Cat="Plan";
        ViewPlans(prid,circle);
        clearBox();
        findViewById(R.id.Plan).setBackground(getDrawable(R.drawable.ripple_box));
    }

    public void ViewPlans(String opid,String circle)
    {

        Loader(true);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.tdapi_urlmrplans)+"&operator_id="+opid+"&circle_id="+circle+"&recharge_type="+Cat;
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {


                        Log.i("VOLLEYES", response);
                        list1.clear();
                        try {
                            Loader(false);

                            JSONObject Res=new JSONObject(response);
                            String data    = Res.getString("data");
                            JSONArray Results = new JSONArray(data);


                            for(int i=0; i<Results.length(); i++) {
                                String Result = Results.getString(i);
                                JSONObject rst = new JSONObject(Result);
                                FruitModel1 fruitModel1 = new FruitModel1();
                                fruitModel1.setAmt(rst.getString("recharge_amount"));
                                fruitModel1.setDis(rst.getString("recharge_long_desc"));
                                fruitModel1.setVal("Validity: "+rst.getString("recharge_validity"));
                                list1.add(fruitModel1);}
                            PlacesAdapter.renewItems(list1);


                        }catch (Exception e){
                            Log.e("catcherror",e+"d");
                            Toast.makeText(PlansActivity.this, "No Plans available on this category!", Toast.LENGTH_SHORT).show();
                            PlacesAdapter.renewItems(list1);
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
                            Toast.makeText(PlansActivity.this, "Network Error: "+errorString, Toast.LENGTH_SHORT).show();

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

    void Loader(Boolean status)
    {
        LinearLayout loader = findViewById(R.id.loader_ViewPlans);
        if(status)
            loader.setVisibility(View.VISIBLE);
        else
            loader.setVisibility(View.GONE);
    }


}