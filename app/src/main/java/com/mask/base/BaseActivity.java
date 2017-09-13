package com.mask.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.mask.app.MyApplication;

import butterknife.ButterKnife;


public abstract class BaseActivity extends FragmentActivity {
    //添加到活动管理集合中
    {
        MyApplication.newInstance().addActyToList(this);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getContentView());
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.newInstance().removeActyFromList(this);
    }

    //注入布局
    protected abstract int getContentView();

    //初始化
    protected abstract void init();
}
