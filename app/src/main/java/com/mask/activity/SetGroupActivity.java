package com.mask.activity;

import android.view.View;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.mask.R;
import com.mask.adapter.EquipmentAdapter;
import com.mask.base.BaseActivity;
import com.mask.bean.MyDevice;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SetGroupActivity extends BaseActivity {


    @BindView(R.id.listView)
    SwipeMenuListView listView;
    EquipmentAdapter adapter;
    List<MyDevice> mList;
    @Override
    protected int getContentView() {
        return R.layout.activity_set_group;
    }

    @Override
    protected void init() {
        mList = new ArrayList<>();
        mList.add(new MyDevice("123"));
        mList.add(new MyDevice("zsda"));
        mList.add(new MyDevice("asd"));
        mList.add(new MyDevice("fdsf"));
        mList.add(new MyDevice("dfg"));
        adapter = new EquipmentAdapter(mList, this);
        listView.setAdapter(adapter);

    }


    @OnClick({R.id.set_left_tv, R.id.iv_set_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.set_left_tv:
                finish();
                break;
            case R.id.iv_set_right:
                break;
        }
    }
}
