package com.stockholm.factory.wifi;


import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stockholm.factory.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.ViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<ScanResult> scanResultList;

    public WifiListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        scanResultList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_recycle_view, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScanResult result = scanResultList.get(position);
        int rssi = WifiManager.calculateSignalLevel(result.level, 100);
        holder.tvWifi.setText(result.SSID + "\t[信号强度:" + rssi + "]");
    }

    @Override
    public int getItemCount() {
        return scanResultList == null ? 0 : scanResultList.size();
    }

    public void setData(List<ScanResult> list) {
        this.scanResultList.clear();
        this.scanResultList.addAll(list);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        TextView tvWifi;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
