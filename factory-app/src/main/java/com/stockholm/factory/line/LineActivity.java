package com.stockholm.factory.line;


import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.stockholm.common.utils.WeakHandler;
import com.stockholm.common.view.ReleaseBaseActivity;
import com.stockholm.factory.FactoryApplication;
import com.stockholm.factory.LogUtils;
import com.stockholm.factory.PathUtils;
import com.stockholm.factory.R;
import com.stockholm.factory.Tips;
import com.stockholm.factory.di.ApplicationComponent;
import com.stockholm.factory.di.DaggerActivityComponent;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * 测试线控和拉绳
 */
public class LineActivity extends ReleaseBaseActivity implements LineView {

    @BindView(R.id.tv_main_text)
    TextView tvMainText;
    @BindView(R.id.tv_line_skip)
    TextView tvSkip;
    @BindView(R.id.cb_key_up)
    ImageView cbKeyUp;
    @BindView(R.id.cb_key_center)
    ImageView cbKeyCenter;
    @BindView(R.id.cb_key_down)
    ImageView cbKeyDown;
    @BindView(R.id.layout_line_cb)
    ViewGroup layoutCb;
    @BindView(R.id.cb_1)
    ImageView cb1;
    @BindView(R.id.cb_2)
    ImageView cb2;
    @BindView(R.id.cb_3)
    ImageView cb3;
    @BindView(R.id.tv_countdown)
    TextView tvCountdown;

    @Inject
    LinePresenter presenter;
    @Inject
    WeakHandler handler;
    @Inject
    PathUtils pathUtils;
    @Inject
    LogUtils logUtils;

    private Tips tips;
    private boolean fail = false;

    @Override
    protected void initInject() {
        ApplicationComponent component = ((FactoryApplication) getApplication()).getApplicationComponent();
        DaggerActivityComponent.builder().applicationComponent(component).build().inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_line;
    }

    @Override
    protected void init() {
        presenter.attachView(this);
        presenter.start();
    }

    @Override
    protected void pauseSound() {

    }

    @Override
    public void onControlUpClick() {
        if (fail) return;
        presenter.onControlUpClick();
    }

    @Override
    public void onControlDownClick() {
        if (fail) return;
        presenter.onControlDownClick();
    }

    @Override
    public void onControlOKClick() {
        if (fail) return;
        presenter.onControlOKClick();
    }

    @Override
    public void onLineShortDrag() {
        if (fail) return;
        presenter.onLineDrag();
    }

    @Override
    public void onUpdateView(int state) {
        switch (state) {
            case LinePresenter.STATE_UP_BUTTON:
                tvMainText.setText(R.string.line_up_btn);
                break;
            case LinePresenter.STATE_OK_BUTTON:
                cbKeyUp.setBackgroundResource(R.drawable.pass);
                logUtils.write(LogUtils.LINE_CONTROL_UP, LogUtils.PASS);
                tvMainText.setText(R.string.line_center_btn);
                break;
            case LinePresenter.STATE_DOWN_BUTTON:
                cbKeyCenter.setBackgroundResource(R.drawable.pass);
                logUtils.write(LogUtils.LINE_CONTROL_OK, LogUtils.PASS);
                tvMainText.setText(R.string.line_down_btn);
                break;
            case LinePresenter.STATE_LINE_DRAG:
                cbKeyDown.setBackgroundResource(R.drawable.pass);
                logUtils.write(LogUtils.LINE_CONTROL_DOWN, LogUtils.PASS);
                tvMainText.setText(R.string.line_drag);
                layoutCb.setVisibility(View.VISIBLE);
                break;
            case LinePresenter.STATE_OVER:
                tvMainText.setText(R.string.line_over);
                break;
            default:
        }
        YoYo.with(Techniques.Shake).duration(400).playOn(tvMainText);
    }

    @Override
    public void onLineDrag(int times) {
        if (fail) return;
        if (times == 1) {
            cb1.setBackgroundResource(R.drawable.pass);
            logUtils.write(LogUtils.LINE_CONTROL, LogUtils.PASS);
        } else if (times == 2) {
            cb2.setBackgroundResource(R.drawable.pass);
            logUtils.write(LogUtils.LINE_CONTROL, LogUtils.PASS);
        } else if (times == 3) {
            cb3.setBackgroundResource(R.drawable.pass);
            logUtils.write(LogUtils.LINE_CONTROL, LogUtils.PASS);
        }
    }

    @Override
    public void onTestSuccess(boolean over) {
        if (tips == null) {
            tips = new Tips(this);
        }
        tips.showSuccess();
        if (over) {
            handler.postDelayed(() -> pathUtils.goNext(this), 1000);
        }
    }

    @Override
    public void onTestFail(int state) {
        if (state == LinePresenter.STATE_UP_BUTTON) {
            cbKeyUp.setBackgroundResource(R.drawable.fail);
            logUtils.write(LogUtils.LINE_CONTROL_UP, LogUtils.FAIL);
        } else if (state == LinePresenter.STATE_OK_BUTTON) {
            cbKeyCenter.setBackgroundResource(R.drawable.fail);
            logUtils.write(LogUtils.LINE_CONTROL_OK, LogUtils.FAIL);
        } else if (state == LinePresenter.STATE_DOWN_BUTTON) {
            cbKeyDown.setBackgroundResource(R.drawable.fail);
            logUtils.write(LogUtils.LINE_CONTROL_DOWN, LogUtils.FAIL);
        } else if (state == LinePresenter.STATE_LINE_DRAG) {
            logUtils.write(LogUtils.LINE_CONTROL, LogUtils.FAIL);
        }
//        pathUtils.goOver(this);
        fail = true;
        tvCountdown.setTextColor(ContextCompat.getColor(this, R.color.red));
        tvCountdown.setText(R.string.fail);
    }

    @Override
    public void onCountdown(long millisUntilFinished) {
        int display = (int) (millisUntilFinished / 1000);
        tvCountdown.setText(String.valueOf(display));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.release();
    }
}
