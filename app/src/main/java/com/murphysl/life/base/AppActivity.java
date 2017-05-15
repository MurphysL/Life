package com.murphysl.life.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.murphysl.life.R;

/**
 * AppActivity
 *
 * @author: MurphySL
 * @time: 2017/5/15 18:28
 */


public abstract class AppActivity extends BaseActivity {

    protected abstract BaseFragment getFirstFragment();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getFragmentContentId() {
        return R.id.container;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        if(getIntent() != null)
            handleIntent(getIntent());

        if(getSupportFragmentManager().getFragments() == null){
            BaseFragment firstFragment = getFirstFragment();
            if(firstFragment != null)
                addFragment(firstFragment);
        }

    }

    protected void handleIntent(Intent intent){}
}
