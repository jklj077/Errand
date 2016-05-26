/*
显示一些用户信息，对应userinfo.xml
点击显示过往任务列表，用户信息等信息
区别于详细用户信息user_info
 */

package com.example.errand.errand;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by xiasifeng on 2016/5/25.
 */
public class userinfo extends Fragment {

    private LinearLayout user;
    private TextView create_view;
    private TextView posted_view;
    private TextView taken_view;
    private TextView past_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.userinfo,container,false);

        user = (LinearLayout) view.findViewById(R.id.user);
        create_view = (TextView) view.findViewById(R.id.create_task);
        posted_view = (TextView) view.findViewById(R.id.posted_task);
        taken_view = (TextView) view.findViewById(R.id.taken_task);
        past_view = (TextView) view.findViewById(R.id.past_task);

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),user_info.class);
                startActivityForResult(intent,0);
            }
        });
        create_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),task_info.class);
                startActivityForResult(intent,0);
            }
        });

        posted_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),nofragment_tasklist.class);
                Bundle bundle = new Bundle();
                bundle.putString("title","已发布任务列表");
                intent.putExtras(bundle);
                startActivityForResult(intent,0);
            }
        });
        taken_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),nofragment_tasklist.class);
                Bundle bundle = new Bundle();
                bundle.putString("title","已领取任务列表");
                intent.putExtras(bundle);
                startActivityForResult(intent,0);
            }
        });
        past_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),nofragment_tasklist.class);
                Bundle bundle = new Bundle();
                bundle.putString("title","过往任务列表");
                intent.putExtras(bundle);
                startActivityForResult(intent,0);
            }
        });

        return view;
    }

}
