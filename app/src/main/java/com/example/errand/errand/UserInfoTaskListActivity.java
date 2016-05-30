/*
对应activity_nofragment_tasklist.xml
主要是用于userinfo（用户信息）fragment的一些历史任务列表的显示
 */

package com.example.errand.errand;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserInfoTaskListActivity extends Activity {

    private int item_num=20;
    private ListView listview;
    private String taskcontent;
    private String taskuser;
    private String taskpay;
    private TextView titletext;
    private TextView back;
    private ArrayList<HashMap<String, String>> mylist;
    private SimpleAdapter mSchedule;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nofragment_tasklist);

        title = this.getIntent().getExtras().getString("title");
        titletext = (TextView) findViewById(R.id.title) ;
        titletext.setText(title);
        listview = (ListView) findViewById(R.id.tasklist_listView);

        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = getIntent();
//                setResult(RESULT_OK,intent);
                finish();
            }
        });


        refresh();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, String> map = (Map<String, String>)  mSchedule.getItem(i);
                String temp = map.get("task_item_content");
                Intent intent = new Intent(UserInfoTaskListActivity.this, TaskInfoDetailActivity.class);
                startActivityForResult(intent,0);
            }
        });

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
        mSchedule = new SimpleAdapter( this, mylist, R.layout.tasklist_item,
                new String[] {"task_item_content", "task_item_person" , "task_item_pay"},
                new int[]{R.id.task_item_content,R.id.task_item_person, R.id.task_item_pay});
        listview.setAdapter(mSchedule);

    }
}
