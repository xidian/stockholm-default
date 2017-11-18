package com.stockholm.business.home;


import com.stockholm.api.business.BusinessService;
import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.common.view.BasePresenter;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomePresenter extends BasePresenter<HomeView> {
    private static final String TAG = HomePresenter.class.getSimpleName();

    private BusinessService businessService;

    @Inject
    public HomePresenter(BusinessService businessService) {
        this.businessService = businessService;
    }

    void getDisplayList() {
        businessService.queryDisplayList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    if (resp.isSuccessful() && resp.body().isSuccess()) {
                        getMvpView().getDataSuccess(resp.body().getData());
                    } else {
                        getMvpView().getDataFail();
                    }
                }, e -> {
                    StockholmLogger.e(TAG, "getDisplayList: " + e.toString());
                    getMvpView().getDataFail();
                });
    }

}