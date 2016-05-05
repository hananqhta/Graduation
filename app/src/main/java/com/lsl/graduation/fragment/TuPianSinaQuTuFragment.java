package com.lsl.graduation.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lsl.graduation.R;
import com.lsl.graduation.adapter.PictureAdapter;
import com.lsl.graduation.bean.PictureModel;
import com.lsl.graduation.json.PicuterSinaJson;
import com.lsl.graduation.net.context.LoadContext;
import com.lsl.graduation.net.context.StringContext;
import com.lsl.graduation.net.loadlistener.SimpleLoadListener;
import com.lsl.graduation.widget.HeaderView;
import com.lsl.graduation.widget.water.WaterDropListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Forrest on 16/4/19.
 */
public class TuPianSinaQuTuFragment extends BaseFragment implements WaterDropListView.IWaterDropListViewListener{
    /** 列表*/
    private WaterDropListView water_list;
    private HeaderView headerView;
    /** 数据*/
    private List<PictureModel> datas;
    /** 页数*/
    private int index = 0;
    /** 适配器*/
    private PictureAdapter mAdapter;
    /** 刷新标志*/
    private boolean isRefresh = false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment,null);
        water_list= (WaterDropListView) view.findViewById(R.id.water_list);
        datas=new ArrayList<PictureModel>();
        mAdapter=new PictureAdapter(getMyActivity(),datas);
        water_list.setAdapter(mAdapter);
        water_list.setWaterDropListViewListener(this);
        water_list.setPullLoadEnable(true);
        initData(index);
        datas=new ArrayList<>();
        return view;
    }

    private void initData(int page) {
        getMyActivity().showDialog();
        new StringContext().flag(LoadContext.FLAG_HTTP_FIRST).get(getSinaQuTu(page + ""))
                .listener(new SimpleLoadListener<String>() {
                    @Override
                    public void loadComplete(LoadContext<String> context) {
                        super.loadComplete(context);
                        getResult(context.getResult());
                        getMyActivity().dismissDialog();
                    }

                    @Override
                    public void loadFail(LoadContext<String> context) {
                        super.loadFail(context);
                        getMyActivity().dismissDialog();

                    }
                }).load();
    }
    public void getResult(String result) {
        List<PictureModel> list = PicuterSinaJson.instance(getActivity()).readJsonPhotoListModles(
                result);
        if (isRefresh){
            isRefresh=false;
            mAdapter.clear();
            datas.clear();

        }
        mAdapter.appendList(list);
    }
    @Override
    public void onRefresh() {
        isRefresh = true;
        index=0;
        initData(index);
        water_list.stopRefresh();
    }

    @Override
    public void onLoadMore() {
        index = index + 20;
        initData(index);
        water_list.stopLoadMore();
    }
}