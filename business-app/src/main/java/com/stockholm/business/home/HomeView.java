package com.stockholm.business.home;

import com.stockholm.api.business.DisplayBean;
import com.stockholm.common.view.MvpView;
import java.util.List;


public interface HomeView extends MvpView {

    void getDataSuccess(List<DisplayBean> data);

    void getDataFail();

}