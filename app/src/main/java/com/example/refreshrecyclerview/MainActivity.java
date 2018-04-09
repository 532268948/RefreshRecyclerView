package com.example.refreshrecyclerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.refreshrecyclerview.custom.RefreshListView;
import com.example.refreshrecyclerview.listener.OnLoadListener;
import com.example.refreshrecyclerview.listener.OnRefreshListener;

public class MainActivity extends AppCompatActivity {

    private RefreshListView mRefreshListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRefreshListView=new RefreshListView(this);
        String []data ={"hi","nihao","yes","no","nihao","yes","no","nihao","yes","no","nihao","yes","no","nihao","yes","no","nihao","yes","no"};
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,data);
        mRefreshListView.setAdapter(arrayAdapter);
        mRefreshListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getApplicationContext(),"refreshing",Toast.LENGTH_SHORT).show();
                mRefreshListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshListView.refreshComplete();
                    }
                },1500);
            }
        });

        mRefreshListView.setOnLoadListener(new OnLoadListener() {
            @Override
            public void onLoadMore() {
                Toast.makeText(getApplicationContext(),"loading",Toast.LENGTH_SHORT).show();
                mRefreshListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshListView.loadCompelte();
                    }
                },1500);
            }
        });
        setContentView(mRefreshListView);
    }
}
