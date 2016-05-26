/*
任务详情页，对应的是activity_task_info.xml，
点击用户列表可以跳转到对应的用户详情页，user_info.java
可以设置点击事件更改项，通过跳转到changeinfo.java更改对应内容并返回输入值
返回图标返回，确认图标在编辑的时候可以显示并且点击后确认，
如果简单浏览的话可以隐藏
*/

package com.example.errand.errand;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//改变消息的类型，用于changeinfo页更改消息完毕后返回时确定更改的是哪个消息
enum infotype{short_info,positoin1}
public class task_info extends Activity {

    private SimpleAdapter mSchedule;
    private infotype send_type;
    private TextView shortinfo;
    private TextView back;
    private ListView user_list;
    private ArrayList<HashMap<String, String>> mylist;
    private int usernum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);

        user_list = (ListView) findViewById(R.id.user_list);
        shortinfo = (TextView) findViewById(R.id.info_short);
        user_refresh();

        user_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                Intent intent = new Intent(task_info.this,user_info.class);
                startActivityForResult(intent,0);
            }
        });

        shortinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_type = infotype.short_info;
                Intent intent = new Intent(task_info.this,changeinfo.class);
                Bundle bundle = new Bundle();
                bundle.putString("content",shortinfo.getText().toString());
                intent.putExtras(bundle);
                startActivityForResult(intent,0);
            }
        });

        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    public void user_refresh()
    {
        usernum=4;
        mylist = new ArrayList< HashMap<String, String> >();
        for (int i=0;i<usernum;i++)
        {
            String username="personA";
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("user", username);
            mylist.add(map);
        }
       mSchedule = new SimpleAdapter( this, mylist, R.layout.user,
                new String[] {"user"},
                new int[]{R.id.user});
        user_list.setAdapter(mSchedule);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            if(bundle != null){
                String content = bundle.getString("content");
                if (send_type == infotype.short_info)
                {
                    shortinfo.setText(content);
                }
            }
        }
    }
}
