package com.murphysl.life.base;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;


/**
 * BaseActivity
 *
 * @author: MurphySL
 * @time: 2017/5/15 17:00
 */


public abstract class BaseActivity extends AppCompatActivity {

    @LayoutRes
    protected abstract int getContentViewId();

    @IdRes
    protected abstract int getFragmentContentId();

    public void addFragment(Fragment fragment){
        if(fragment != null){
            getSupportFragmentManager().beginTransaction()
                    .replace(getFragmentContentId() , fragment , fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    public void removeFragment(){
        if(getSupportFragmentManager().getBackStackEntryCount() > 1){
            getSupportFragmentManager().popBackStack();
        }else{
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK ){
            removeFragment();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
