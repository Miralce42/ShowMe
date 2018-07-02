package com.hanzhuang42.showme.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.hanzhuang42.showme.R;
import com.hanzhuang42.showme.adapters.TestRecyclerViewAdapter;
import com.hanzhuang42.showme.db.DetectObject;
import com.hanzhuang42.showme.util.DbUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewFragment extends Fragment {

    private static final boolean GRID_LAYOUT = false;
    private List<DetectObject> detectObjectList = new ArrayList<>();
    TestRecyclerViewAdapter adapter;
    RecyclerView mRecyclerView;
    private int type;

    public static RecyclerViewFragment newInstance() {
        return new RecyclerViewFragment();
    }

    public static RecyclerViewFragment newInstance(int pos) {
        RecyclerViewFragment viewFragment = new RecyclerViewFragment();
        viewFragment.type = pos;
        return viewFragment;
    }

    private void refreshList(int type){
        detectObjectList.clear();
        detectObjectList.addAll(DbUtility.query(type));
        //去除没有图片的项
        for(int i = 0;i < detectObjectList.size();i++){
            DetectObject detectObject = detectObjectList.get(i);
            String imgPath = detectObject.getImgPath();
            if(imgPath != null) {
                File file = new File(imgPath);
                if (!file.exists()) {
                    detectObjectList.remove(i);
                    detectObject.delete();
                }
            }
        }
    }


    public void refreshData() {
        refreshList(type);
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        mRecyclerView = v.findViewById(R.id.recyclerView);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //setup materialviewpager

        if (GRID_LAYOUT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        mRecyclerView.setHasFixedSize(true);

        //Use this now
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        adapter=new TestRecyclerViewAdapter(detectObjectList);
        mRecyclerView.setAdapter(adapter);
        refreshData();
    }
}
