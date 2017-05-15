package com.murphysl.life.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * BaseFragment
 *
 * @author: MurphySL
 * @time: 2017/5/15 17:50
 */


public abstract class BaseFragment extends Fragment{
    private BaseActivity activity;

    protected BaseActivity getHoldingActivity(){
        return activity;
    }

    protected abstract void initView(final View view, Bundle savedInstanceState);

    @LayoutRes
    protected abstract int getLayoutId();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (BaseActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container , false);
        initView(view, savedInstanceState);
        return view;
    }
}
