package com.example.chapter_2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chapter_2.recycler.MyAdapter;
import com.example.chapter_2.recycler.TestData;
import com.example.chapter_2.recycler.TestDataSet;

public class RecyclerViewActivity extends AppCompatActivity implements MyAdapter.IOnItemClickListener {

    private static final String TAG = "TAG";
    private MyAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        Log.i(TAG, "RecyclerViewActivity onCreate");
        initView();
    }

    public void myOnClick (View view) {
        ConstraintLayout myDefaultLayout = findViewById(R.id.defaultConstraintLayout);
        int num = CountViews(myDefaultLayout);
        Toast.makeText(RecyclerViewActivity.this,"当前Activity共有"+num+"个view",Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new MyAdapter(TestDataSet.getData());
        mAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState: done");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState: done");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(this,"Recycler状态Stopped!",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this,"Recycler状态Destroyed!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemCLick(int position, TestData data) {
        Log.i(TAG, "onItemCLick: 创建新的activity");
        Intent intent = new Intent(this,InformationViewActivity.class);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        int num = CountViews(recyclerView);
        Toast.makeText(this,"recycler共有"+num+"个view",Toast.LENGTH_SHORT).show();
        intent.putExtra("position",position);
        startActivity(intent);
    }

    @Override
    public void onItemLongCLick(int position, TestData data) {
        mAdapter.removeData(position);
    }

    //此函数用于统计传入的布局的view总数
    public int CountViews (View root) {
        if (null == root) return 0;
        int num = 1;
        if (root instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) root).getChildCount(); i++) {
                num += CountViews(((ViewGroup) root).getChildAt(i));
            }
        }
        return num;
    }
}
