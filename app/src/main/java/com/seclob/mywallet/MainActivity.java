package com.seclob.mywallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    CardView providerPrepaid,providerPostpaid,providerdth;
    SharedPreferences sharedPreferences;
    TextView mainBalance,dthTotal,Totalsale,ksebTotal,clientName;

    List<RecentMobileRechargeModel> mobileRechargeModels = new ArrayList<>(1000);
    private RecentMobileRechargeAdaptor recentMobileRechargeAdaptor;
    RecyclerView mobileRechargeRecyclerView;

    List<RecentDthRechargeModel> mobileDthModels = new ArrayList<>(1000);
    private RecentDthRechargeAdaptor recentDthRechargeAdaptor;
    RecyclerView mobileDthRecyclerView;
    LinearLayout sideNavOverLay;
    ScrollView sideNav;
    Animation pop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        providerPrepaid = findViewById(R.id.providerPrepaid);
        providerPostpaid = findViewById(R.id.providerPostpaid);
        providerdth = findViewById(R.id.providerdth);
        mainBalance = findViewById(R.id.mainBalance);
        clientName = findViewById(R.id.clientName);
        Totalsale = findViewById(R.id.TodaysSales);
        sideNavOverLay = findViewById(R.id.sideNavOverLay);
        sideNav = findViewById(R.id.sideNav);
        pop = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoomin);

        mobileRechargeRecyclerView = findViewById(R.id.mobileRechargeRecyclerView);
        recentMobileRechargeAdaptor = new RecentMobileRechargeAdaptor(getApplication());
        mobileRechargeRecyclerView.setAdapter(recentMobileRechargeAdaptor);
        mobileRechargeRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        mobileRechargeRecyclerView.setNestedScrollingEnabled(false);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);

        HomeAPI(sharedPreferences.getString("username",""),sharedPreferences.getString("password",""));
        RechargesStatus();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {
            case R.id.home:

                break;

            case R.id.commission:
                Intent intents = new Intent(MainActivity.this, CommisionActivity.class);
                startActivity(intents);
                break;

            case R.id.report:
                Intent intent = new Intent(MainActivity.this, RechargeHostoryActivity.class);
                startActivity(intent);
                break;

            case R.id.profile:
                Intent inten = new Intent(MainActivity.this, WalletActivity.class);
                startActivity(inten);
                break;
        }

        return true;
    }

    public void Logout(View view)
    {
        SideNavOpen(false);
        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", "");
        editor.putString("password", "");
        editor.putString("shop_id", "");
        editor.putString("login_id", "");
        editor.apply();

        Intent i = new Intent(getApplicationContext(),LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    public void OpenRE(View view)
    {SideNavOpen(false);
        Intent i = new Intent(this,RmoneyActivity.class);
        startActivity(i);
    }

    public void OpenSideBar(View view)
    {
        SideNavOpen(true);
    }

    public void CloseSideBar(View view)
    {
        SideNavOpen(false);
    }

    void SideNavOpen(boolean isOpen)
    {
        if(isOpen)
        {   sideNavOverLay.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.ltor);
            Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadein);
            sideNavOverLay.startAnimation(animation2);
            sideNav.startAnimation(animation);
        }else{
            Animation animation3 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotl);
            Animation animation4 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadeout);
            sideNavOverLay.startAnimation(animation4);
            sideNav.startAnimation(animation3);
            sideNavOverLay.setVisibility(View.GONE);
        }
    }

    public void TS(View view)
    {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:9995140919"));
        startActivity(i);
    }
    public void CS(View view)
    {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:8075565027"));
        startActivity(i);
    }
    public void WAS(View view)
    {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=918943601858&text=Welcome%20to%20PayTaken"));
        startActivity(i);
    }


    public void OpenStatement(View view)
    {
        SideNavOpen(false);
        Intent intents = new Intent(MainActivity.this, WalletActivity.class);
        startActivity(intents);
    }

    public void OpenWallet(View view)
    {
        Intent intents = new Intent(MainActivity.this, WalletActivity.class);
        startActivity(intents);
    }

    public void OpenRechargeHistory(View view)
    {
        SideNavOpen(false);
        Intent intents = new Intent(MainActivity.this, RechargeHostoryActivity.class);
        startActivity(intents);
    }

    public void OpenCommissions(View view)
    {
        SideNavOpen(false);
        Intent intents = new Intent(MainActivity.this, CommisionActivity.class);
        startActivity(intents);
    }

    public void OpenProfile(View view)
    {
        SideNavOpen(false);
        Intent intents = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intents);
    }

    @Override
    protected void onResume() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("mramt", "");
        editor.apply();
        super.onResume();
        HomeAPI(sharedPreferences.getString("username",""),sharedPreferences.getString("password",""));
        //RechargesStatus();
    }

    public void PrepaidBtn(View view)
    {
        providerPrepaid.setVisibility(View.VISIBLE);
        providerPostpaid.setVisibility(View.GONE);
        providerdth.setVisibility(View.GONE);
    }

    public void PostpaidBtn(View view)
    {
        providerPrepaid.setVisibility(View.GONE);
        providerPostpaid.setVisibility(View.VISIBLE);
        providerdth.setVisibility(View.GONE);
    }
    public void dthBtn(View view)
    {
  //      Toast.makeText(this, "Coming Soon...", Toast.LENGTH_SHORT).show();
        providerPrepaid.setVisibility(View.GONE);
        providerPostpaid.setVisibility(View.GONE);
        providerdth.setVisibility(View.VISIBLE);
    }

    public void ksebBtn(View view)
    {
        Toast.makeText(this, "Coming Soon...", Toast.LENGTH_SHORT).show();
//        Intent intents = new Intent(MainActivity.this, KsebRechargeActivity.class);
//        startActivity(intents);
    }

    //PREPAID

    public void previ(View view)
    {
        Intent intents = new Intent(MainActivity.this, MobileRechargeActivity.class);
        intents.putExtra("type","Prepaid");
        intents.putExtra("provider","0");
        startActivity(intents);
    }
    public void prevf(View view)
    {
        Intent intents = new Intent(MainActivity.this, MobileRechargeActivity.class);
        intents.putExtra("type","Prepaid");
        intents.putExtra("provider","5");
        startActivity(intents);
    }
    public void preairtel(View view)
    {
        Intent intents = new Intent(MainActivity.this, MobileRechargeActivity.class);
        intents.putExtra("type","Prepaid");
        intents.putExtra("provider","1");
        startActivity(intents);
    }
    public void prejio(View view)
    {
        Intent intents = new Intent(MainActivity.this, MobileRechargeActivity.class);
        intents.putExtra("type","Prepaid");
        intents.putExtra("provider","2");
        startActivity(intents);
    }
    public void prebsnl(View view)
    {
        Intent intents = new Intent(MainActivity.this, MobileRechargeActivity.class);
        intents.putExtra("type","Prepaid");
        intents.putExtra("provider","3");
        startActivity(intents);
    }

    public void prebsnlsp(View view)
    {
        Intent intents = new Intent(MainActivity.this, MobileRechargeActivity.class);
        intents.putExtra("type","Prepaid");
        intents.putExtra("provider","4");
        startActivity(intents);
    }

    //POSTPAID

    public void postvi(View view)
    {
        Intent intents = new Intent(MainActivity.this, MobileRechargeActivity.class);
        intents.putExtra("type","Postpaid");
        intents.putExtra("provider","0");
        startActivity(intents);
    }
    public void postairtel(View view)
    {
        Intent intents = new Intent(MainActivity.this, MobileRechargeActivity.class);
        intents.putExtra("type","Postpaid");
        intents.putExtra("provider","1");
        startActivity(intents);
    }
    public void postjio(View view)
    {
        Intent intents = new Intent(MainActivity.this, MobileRechargeActivity.class);
        intents.putExtra("type","Postpaid");
        intents.putExtra("provider","2");
        startActivity(intents);
    }
    public void postbsnl(View view)
    {
        Intent intents = new Intent(MainActivity.this, MobileRechargeActivity.class);
        intents.putExtra("type","Postpaid");
        intents.putExtra("provider","3");
        startActivity(intents);
    }
    public void postvf(View view)
    {
        Intent intents = new Intent(MainActivity.this, MobileRechargeActivity.class);
        intents.putExtra("type","Postpaid");
        intents.putExtra("provider","4");
        startActivity(intents);
    }

//DTH

    public void dthsky(View view)
    {
        Intent intents = new Intent(MainActivity.this, DthRechargeActivity.class);
        intents.putExtra("type","Postpaid");
        intents.putExtra("provider","0");
        intents.putExtra("pcode","TSD");
        startActivity(intents);
    }
    public void dthsun(View view)
    {
        Intent intents = new Intent(MainActivity.this, DthRechargeActivity.class);
        intents.putExtra("type","Postpaid");
        intents.putExtra("provider","1");
        intents.putExtra("pcode","SD");
        startActivity(intents);
    }
    public void dthdishtv(View view)
    {
        Intent intents = new Intent(MainActivity.this, DthRechargeActivity.class);
        intents.putExtra("type","Postpaid");
        intents.putExtra("provider","2");
        intents.putExtra("pcode","DTD");
        startActivity(intents);
    }
    public void dthvcon(View view)
    {
        Intent intents = new Intent(MainActivity.this, DthRechargeActivity.class);
        intents.putExtra("type","Postpaid");
        intents.putExtra("provider","3");
        intents.putExtra("pcode","VDD");
        startActivity(intents);
    }

    public void dthairtel(View view)
    {
        Intent intents = new Intent(MainActivity.this, DthRechargeActivity.class);
        intents.putExtra("provider","4");
        intents.putExtra("pcode","ADT");
        startActivity(intents);
    }

    void Loader(Boolean status)
    {
        LinearLayout loader = findViewById(R.id.loader_Main);
        if(status)
            loader.setVisibility(View.VISIBLE);
        else
            loader.setVisibility(View.GONE);
    }

    public void HomeAPI(final String username, final String password)
    {
        Loader(false);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"shop/home?user_id="+sharedPreferences.getString("id","");
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {


                        Log.i("VOLLEYES", response);

                        try {
                            Loader(false);
                            JSONObject Res = new JSONObject(response);
                            String sts     = Res.getString("sts");
                            String msg     = Res.getString("msg");

                            if(sts.equalsIgnoreCase("01"))
                            {
                                mainBalance.setText("₹"+Res.getString("wallet"));
                                Totalsale.setText("₹"+Res.getString("debit"));
                                clientName.setText("Hello "+Res.getString("name")+", Welcome to PayTaken.");

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("shopname", Res.getString("name"));
                                editor.apply();

                                String data = Res.getString("recharge");
                                JSONArray Results = new JSONArray(data);

                                mobileRechargeModels.clear();
                                for (int i = 0; i < Results.length(); i++)
                                {
                                    String Result = Results.getString(i);
                                    JSONObject rst = new JSONObject(Result);
                                    RecentMobileRechargeModel mobileRechargeModel = new RecentMobileRechargeModel();
                                    mobileRechargeModel.setMobile(rst.getString("number"));
                                    mobileRechargeModel.setProvider(rst.getString("operatorname")+" ("+rst.getString("type")+")");
                                    mobileRechargeModel.setDis("₹"+rst.getString("amount")+"("+rst.getString("circlename")+") on " + rst.getString("created_at")+"\n#"+rst.getString("apitransid")+" ("+rst.getString("id")+")");
                                    mobileRechargeModel.setStatus(rst.getString("status").toUpperCase());
                                    mobileRechargeModel.setIdRecharge(rst.getString("id"));
                                    mobileRechargeModels.add(mobileRechargeModel);
                                }
                                recentMobileRechargeAdaptor.renewItems(mobileRechargeModels);
                            }

                        }catch (Exception e){
                            Log.e("catcherror",e+"d");

                           // Toast.makeText(getApplicationContext(), "Catch Error :"+e, Toast.LENGTH_SHORT).show();

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
                       //    Toast.makeText(getApplicationContext(), "Network Error :"+errorString, Toast.LENGTH_SHORT).show();
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

    public void RechargesStatus()
    {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"recharge/corn";
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        if(response.length()>5)
                        {
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

                            findViewById(R.id.progressBar).setVisibility(View.GONE);

                        }
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("shop_id",sharedPreferences.getString("shop_id",""));
                params.put("limit","50");
                Log.i("loginp ", params.toString());

                return params;
            }

        };

        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);

    }


}