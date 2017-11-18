package com.stockholm.factory.bluetooth;


import android.content.Context;
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

public class BluetoothListAdapter extends RecyclerView.Adapter<BluetoothListAdapter.ViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private List<MeowBluetoothDevice> scanResultList;

    public BluetoothListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        scanResultList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_recycle_view, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MeowBluetoothDevice device = scanResultList.get(position);
        holder.tvWifi.setText(device.getDevice().getName() + "\t[信号强度:" + device.getRssi() + "]");
    }

    @Override
    public int getItemCount() {
        return scanResultList == null ? 0 : scanResultList.size();
    }

    public void add(MeowBluetoothDevice device) {
        this.scanResultList.add(device);
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
