package com.stockholm.factory.uuid;

import com.stockholm.common.view.MvpView;

interface UuidView extends MvpView {
    void onSnGot(String sn);
}
