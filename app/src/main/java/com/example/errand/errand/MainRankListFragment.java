/*
用户排名页，点击打开详细用户表，对应rank.xml
 */

package com.example.errand.errand;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by xiasifeng on 2016/5/25.
 */
public class MainRankListFragment extends Fragment {

    private int item_num=20;
    private ListView listview;
    private String rank;
    private String username;
    private String level;
    private View view;
    private ArrayList<HashMap<String, String>> mylist;
    private SimpleAdapter mSchedule;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.rank,container,false);
        listview = (ListView) view.findViewById(R.id.ranklist_listview);
        refresh();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), UserInfoDetailActivity.class);
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
            rank=String.valueOf(i+1)+'.';
            username="personA";
            level="1";
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("MainRankListFragment", rank);
            map.put("username", username);
            map.put("level", level);
            mylist.add(map);
        }
        mSchedule = new SimpleAdapter( this.getActivity(), mylist, R.layout.user_list_item,
                new String[]{"MainRankListFragment", "username", "level"},
                new int[]{R.id.rank,R.id.username, R.id.level});
        listview.setAdapter(mSchedule);

    }
}
