package com.mask.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.mask.R;
import com.mask.adapter.EquipmentAdapter;
import com.mask.base.BaseActivity;
import com.mask.bean.MyDevice;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class GroupDeviceActivity extends BaseActivity implements SwipeMenuListView.OnMenuItemClickListener{


    @BindView(R.id.device_title_tv)
    TextView deviceTitleTv;
    @BindView(R.id.listView)
    SwipeMenuListView listView;
    EquipmentAdapter adapter;
    List<MyDevice> mList;

    @Override
    protected int getContentView() {
        return R.layout.activity_group_device;
    }

    @Override
    protected void init() {
        mList = new ArrayList<>();
        mList.add(new MyDevice("123"));
        mList.add(new MyDevice("zsda"));
        mList.add(new MyDevice("asd"));
        mList.add(new MyDevice("fdsf"));
        mList.add(new MyDevice("dfg"));
        adapter = new EquipmentAdapter( this);
        listView.setAdapter(adapter);
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(this);
    }


    @OnClick({R.id.device_left_tv, R.id.iv_device_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.device_left_tv:
                finish();
                break;
            case R.id.iv_device_right:
                startActivity(new Intent(this, SetGroupActivity.class));
                break;
            default:
                break;
        }
    }

    /*添加侧滑菜单*/
    SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {

            // 删除菜单
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.red)));
            // set item width
            deleteItem.setWidth(dp2px(70));
            // 设置内容
            deleteItem.setTitle("删除");
            // 设置字体大小
            deleteItem.setTitleSize(14);
            // 字体颜色
            deleteItem.setTitleColor(getResources().getColor(R.color.white));
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };



    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
        mList.remove(position);
        adapter.notifyDataSetChanged();
        return false;
    }

    protected int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
