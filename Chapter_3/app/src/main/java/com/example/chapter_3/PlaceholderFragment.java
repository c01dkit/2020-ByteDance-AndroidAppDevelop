package com.example.chapter_3;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PlaceholderFragment extends Fragment {

    private static final String TAG = "PlaceholderFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO ex3-3: 修改 fragment_placeholder，添加 loading 控件和列表视图控件
        Log.i(TAG, "onCreateView: oncreatview");
        return inflater.inflate(R.layout.fragment_placeholder, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        getView().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // 这里会在 5s 后执行
//                // TODO ex3-4：实现动画，将 lottie 控件淡出，列表数据淡入
//            }
//        }, 5000);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated: yes");
        view.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        },5000);
    }
}
