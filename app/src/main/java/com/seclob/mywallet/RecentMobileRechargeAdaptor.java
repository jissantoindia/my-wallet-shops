package com.seclob.mywallet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class RecentMobileRechargeAdaptor extends RecyclerView.Adapter<RecentMobileRechargeAdaptor.MyViewHolder> {

    private LayoutInflater inflater;
    Context ctx;
    private List<RecentMobileRechargeModel> mRecentMobileRechargeModels = new ArrayList<>();
    RecentMobileRechargeModel RecentMobileRechargeModel;
    String vidID;
    SharedPreferences sharedPreferences;
    Context context;
    Fragment LocationFrag;
    FragmentTransaction ft;

    public RecentMobileRechargeAdaptor(Context ctx){
        this.ctx = ctx;

    }

    public void renewItems(List<RecentMobileRechargeModel> list) {
        this.mRecentMobileRechargeModels = list;
        notifyDataSetChanged();
    }

    @Override
    public RecentMobileRechargeAdaptor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_mobiles_layout, parent,false);
        return new RecentMobileRechargeAdaptor.MyViewHolder(inflate);


    }

    @Override
    public void onBindViewHolder(RecentMobileRechargeAdaptor.MyViewHolder holder, final int position) {
        RecentMobileRechargeModel = mRecentMobileRechargeModels.get(position);
        holder.Mobile.setText(RecentMobileRechargeModel.getMobile());
        holder.Details.setText(RecentMobileRechargeModel.getDis());
        holder.Provider.setText(RecentMobileRechargeModel.getProvider());
        if(RecentMobileRechargeModel.getStatus().toLowerCase().equalsIgnoreCase("pending"))
        {holder.Status.setBackground(ctx.getDrawable(R.drawable.pending));
            holder.Status.setText(RecentMobileRechargeModel.getStatus());}
        else if(RecentMobileRechargeModel.getStatus().toLowerCase().equalsIgnoreCase("success"))
        { holder.Status.setBackground(ctx.getDrawable(R.drawable.success));
            holder.Status.setText(RecentMobileRechargeModel.getStatus());}
        else
        {holder.Status.setBackground(ctx.getDrawable(R.drawable.danger));
            holder.Status.setText(RecentMobileRechargeModel.getStatus());}


        holder.cardvidparent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(v.getContext(),MobileRechargeDetailsActivity.class);
                i.putExtra("RechargeID",mRecentMobileRechargeModels.get(position).getIdRecharge());
                //Toast.makeText(ctx, mRecentMobileRechargeModels.get(position).getIdRecharge(), Toast.LENGTH_SHORT).show();
                v.getContext().startActivity(i);


            }

        });
    }

    @Override
    public int getItemCount() {
        return mRecentMobileRechargeModels.size();
    }

    public void updateList(List<RecentMobileRechargeModel> list){
        mRecentMobileRechargeModels = list;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Mobile,Provider,Details,Status;

        LinearLayout cardvidparent;
        public MyViewHolder(View itemView) {
            super(itemView);

            Mobile = (TextView) itemView.findViewById(R.id.recent_mobile);
            Provider = (TextView) itemView.findViewById(R.id.recent_provider);
            Details = (TextView) itemView.findViewById(R.id.recent_details);
            Status = (TextView) itemView.findViewById(R.id.recent_status);

            cardvidparent = itemView.findViewById(R.id.recent_layout);
        }

    }


}