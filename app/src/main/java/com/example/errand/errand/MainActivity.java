/*
主界面，对应activity_main.xml,登陆后即到当前页面
它根据用户点击下方的tab按钮更新显示页面，具体有三个fragment
对应的是tasklist.java（任务列表），rank.java（用户排名），userinfo.java（用户页）
*/
package com.example.errand.errand;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView list;
    private TextView rank;
    private TextView info;
    private FrameLayout frame;

    private MainTaskListFragment frame_list;
    private MainRankListFragment frame_rank;
    private MainUserInfoFragment frame_info;
    private FragmentManager framemanager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_main);

        framemanager = getFragmentManager();
        bindViews();
        list.performClick();
    }

    private void bindViews() {
        list = (TextView) findViewById(R.id.list);
        rank = (TextView) findViewById(R.id.rank);
        info = (TextView) findViewById(R.id.info);

        list.setOnClickListener(this);
        rank.setOnClickListener(this);
        info.setOnClickListener(this);

    }

    private void setSelected() {
        list.setSelected(false);
        rank.setSelected(false);
        info.setSelected(false);
    }

    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if (frame_list != null) fragmentTransaction.hide( frame_list);
        if (frame_rank != null) fragmentTransaction.hide(frame_rank);
        if (frame_info != null) fragmentTransaction.hide(frame_info);
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction fTransaction = framemanager.beginTransaction();
        hideAllFragment(fTransaction);
        switch (v.getId()) {
            case R.id.list:
                setSelected();
                list.setSelected(true);
                if (frame_list == null) {
                    frame_list = new MainTaskListFragment();
                    fTransaction.add(R.id.content, frame_list);
                } else {
                    fTransaction.show(frame_list);
                }
                break;
            case R.id.rank:
                setSelected();
                rank.setSelected(true);
                if (frame_rank == null) {
                    frame_rank = new MainRankListFragment();
                    fTransaction.add(R.id.content, frame_rank);
                } else {
                    fTransaction.show(frame_rank);
                }
                break;
            case R.id.info:
                setSelected();
                info.setSelected(true);
                if (frame_info == null) {
                    frame_info = new MainUserInfoFragment();
                    fTransaction.add(R.id.content, frame_info);
                } else {
                    fTransaction.show(frame_info);
                }
                break;
        }
        fTransaction.commit();
    }

}
