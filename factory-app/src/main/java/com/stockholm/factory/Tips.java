package com.stockholm.factory;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class Tips extends Toast {

    @BindView(R.id.tv_ok)
    TextView tvOk;
    @BindView(R.id.tv_fail)
    TextView tvFail;

    public Tips(@NonNull Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.view_tips, null);
        setView(view);
        ButterKnife.bind(this, view);

    }

    public void setSuccessText(int strRes) {
        tvOk.setText(strRes);
    }

    public void setSuccessText(String str) {
        tvOk.setText(str);
    }

    public void setFailText(int strRes) {
        tvFail.setText(strRes);
    }

    public void setFailText(String str) {
        tvFail.setText(str);
    }

    public void showSuccess() {
        setDuration(Toast.LENGTH_SHORT);
        tvFail.setVisibility(GONE);
        tvOk.setVisibility(VISIBLE);
        show();
    }

    public void showFail() {
        setDuration(Toast.LENGTH_SHORT);
        tvOk.setVisibility(GONE);
        tvFail.setVisibility(VISIBLE);
        show();
    }
}
