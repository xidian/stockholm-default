package com.stockholm.factory.screen;


import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stockholm.common.utils.WeakHandler;
import com.stockholm.common.view.ReleaseBaseActivity;
import com.stockholm.factory.FactoryApplication;
import com.stockholm.factory.LogUtils;
import com.stockholm.factory.PathUtils;
import com.stockholm.factory.R;
import com.stockholm.factory.di.ApplicationComponent;
import com.stockholm.factory.di.DaggerActivityComponent;


import javax.inject.Inject;

import butterknife.BindView;

public class ScreenActivity extends ReleaseBaseActivity {

    @BindView(R.id.iv_color)
    ImageView ivColor;
    @BindView(R.id.layout_screen_text)
    ViewGroup text;
    @BindView(R.id.tv_screen_tip)
    TextView tips;
    @BindView(R.id.tv_fail)
    TextView tvFail;

    @Inject
    PathUtils pathUtils;
    @Inject
    WeakHandler handler;
    @Inject
    LogUtils logUtils;

    private int[] drawables = new int[]{
            R.drawable.red, R.drawable.blue,
            R.drawable.green, R.drawable.black,
            R.drawable.gray, R.drawable.white,
            R.drawable.gray_8_1, R.drawable.gray_8_2,
            R.drawable.gray_16_1, R.drawable.gray_16_2,
            R.drawable.gray_32_1, R.drawable.gray_32_2,
            R.drawable.gray_64_1, R.drawable.gray_64_2
    };
    private int index;
    private boolean fail = false;

    @Override
    protected void initInject() {
        ApplicationComponent component = ((FactoryApplication) getApplication()).getApplicationComponent();
        DaggerActivityComponent.builder().applicationComponent(component).build().inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_screen;
    }

    @Override
    protected void init() {
    }

    @Override
    protected void pauseSound() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        index = 0;
    }

    @Override
    public void onControlDownClick() {
        logUtils.write("screen | control down click, index=" + index, LogUtils.CLICK);
        if (fail) return;
        if (index == drawables.length) {
            logUtils.write(LogUtils.SCREEN, LogUtils.TESTED);
            pathUtils.goNext(this);
            return;
        } else if (index == 0) {
            logUtils.write(LogUtils.SCREEN, LogUtils.TESTED);
        }
        text.setVisibility(View.INVISIBLE);
        ivColor.setVisibility(View.VISIBLE);
        index = index % drawables.length;
        ivColor.setBackgroundResource(drawables[index]);
        index++;
    }

    @Override
    public void onLineShortDrag() {
        //prevent line drag
        logUtils.write("screen | line drag", LogUtils.CLICK);
        logUtils.write(LogUtils.SCREEN, LogUtils.FAIL);
        if (fail) return;
//        pathUtils.goOver(this);
        fail = true;
        tvFail.setText(R.string.fail);
        tvFail.setVisibility(View.VISIBLE);
    }

}
