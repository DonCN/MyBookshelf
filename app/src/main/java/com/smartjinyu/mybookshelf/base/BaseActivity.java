package com.smartjinyu.mybookshelf.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.smartjinyu.mybookshelf.app.BookShelfApp;
import com.smartjinyu.mybookshelf.di.component.ActivityComponent;
import com.smartjinyu.mybookshelf.di.component.DaggerActivityComponent;
import com.smartjinyu.mybookshelf.di.module.ActivityModule;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 作者：Neil on 2017/4/12 13:08.
 * 邮箱：cn.neillee@gmail.com
 */

public abstract class BaseActivity<T extends BasePresenter>
        extends AppCompatActivity implements BaseView {
    @Inject
    protected T mPresenter;

    protected Activity mContext;
    private Unbinder mUnBinder;

    protected String TAG;

    private View.OnClickListener mUpClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getTag();
        doSavedInstanceState(savedInstanceState);
        setContentView(getLayoutId());
        mUnBinder = ButterKnife.bind(this);
        mContext = this;
        initInject();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        BookShelfApp.getInstance().addActivity(this);
        initEventAndData();
    }

    protected void setupToolbar(Toolbar toolbar, int titleId) {
        this.setupToolbar(toolbar, mContext.getString(titleId));
    }

    protected void setupToolbar(Toolbar toolbar, String title) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDefaultDisplayHomeAsUpEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(mUpClickListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.detachView();
        mUnBinder.unbind();
        BookShelfApp.getInstance().removeActivity(this);
    }

    protected ActivityComponent getActivityComponent() {
        return DaggerActivityComponent.builder()
                .appComponent(BookShelfApp.getAppComponent())
                .activityModule(getActivityModule())
                .build();
    }

    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

    @Override
    public void onBackPressed() {
    }

    protected abstract String getTag();

    protected void doSavedInstanceState(Bundle savedInstanceState) {
    }

    protected abstract int getLayoutId();

    protected abstract void initInject();

    protected abstract void initEventAndData();
}
