/*
fragment，任务列表，对应tasklist.xml，
动态显示了任务的列表，任务列表项对应的是tasjklist_item.xml
并可以点击任务项进入对应的任务详情页task_info.java

*/


package com.example.errand.errand;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by xiasifeng on 2016/5/25.
 */
public class tasklist extends Fragment {
    private int item_num=20;
    private ListView listview;
    private String taskcontent;
    private String taskuser;
    private String taskpay;
    private View view;
    private ArrayList<HashMap<String, String>> mylist;
    private SimpleAdapter mSchedule;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tasklist,container,false);
        listview = (ListView) view.findViewById(R.id.tasklist_listView);
        refresh();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, String> map = (Map<String, String>)  mSchedule.getItem(i);
                String temp = map.get("task_item_content");
                Intent intent = new Intent(getActivity(),task_info.class);
                startActivityForResult(intent,0);
            }
        });
        return view;
    }

    public void refresh()
    {
        mylist = new ArrayList< HashMap<String, String> >();
        for (int i=0;i<item_num;i++)
        {
            taskcontent="求帮忙拿快递！！";
            taskuser="personA";
            taskpay="      5RMB";
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("task_item_content", taskcontent);
            map.put("task_item_person", taskuser);
            map.put("task_item_pay", taskpay);
            mylist.add(map);
        }
        mSchedule = new SimpleAdapter( this.getActivity(), mylist, R.layout.tasklist_item,
                new String[] {"task_item_content", "task_item_person" , "task_item_pay"},
                new int[]{R.id.task_item_content,R.id.task_item_person, R.id.task_item_pay});
        listview.setAdapter(mSchedule);

    }
}
