package com.murphysl.life.base.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.murphysl.life.base.AppActivity;
import com.murphysl.life.util.Tutil;

/**
 * BaseMVPActivity
 *
 * @author: MurphySL
 * @time: 2017/5/15 17:47
 */


public abstract class BaseMVPActivity<M extends BaseModel , P extends BasePresenter> extends AppActivity implements BaseView{

    protected M model;
    protected P presenter;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = Tutil.getT(this, 0);
        presenter = Tutil.getT(this, 1);
        if(model != null && presenter != null)
            presenter.attach(this, model);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(presenter != null)
            presenter.detach();
    }


}
