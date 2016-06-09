/*
主界面，对应activity_main.xml,登陆后即到当前页面
它根据用户点击下方的tab按钮更新显示页面，具体有三个fragment
对应的是tasklist.java（任务列表），rank.java（用户排名），userinfo.java（用户页）
*/
package com.example.errand.errand;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView list;
    private TextView rank;
    private TextView info;

    private FragmentManager framemanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Override
    public void onClick(View v) {
        FragmentTransaction fTransaction = framemanager.beginTransaction();
        switch (v.getId()) {
            case R.id.list:
                setSelected();
                list.setSelected(true);
                fTransaction.replace(R.id.content, new TaskListFragment());
                break;
            case R.id.rank:
                setSelected();
                rank.setSelected(true);
                fTransaction.replace(R.id.content, new RankListFragment());
                break;
            case R.id.info:
                setSelected();
                info.setSelected(true);
                fTransaction.replace(R.id.content, new UserInfoFragment());
                break;
        }
        fTransaction.commit();
    }

}
