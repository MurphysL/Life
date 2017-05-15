package com.murphysl.life.base.mvp;

/**
 * BasePresenter
 *
 * @author: MurphySL
 * @time: 2017/5/15 16:52
 */


public abstract class BasePresenter<V extends BaseView , M extends BaseModel> {
    protected V view;
    protected M model;

    public void attach(V view , M model){
        if(this.view == null)
            this.view = view;
        if(this.model == null)
            this.model = model;
    }

    public void detach(){
        if(view != null)
            view = null;
        if(model != null)
            model = null;
    }
}
