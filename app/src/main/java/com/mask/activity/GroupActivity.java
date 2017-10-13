package com.mask.activity;

import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;

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

public class GroupActivity extends BaseActivity implements SwipeMenuListView.OnMenuItemClickListener, AdapterView.OnItemClickListener {
    @BindView(R.id.listView)
    SwipeMenuListView listView;
    EquipmentAdapter adapter;
    List<MyDevice> mList;

    @Override
    protected int getContentView() {
        return R.layout.activity_group;
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
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(this);
        listView.setOnItemClickListener(this);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
        return false;
    }
    protected int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
