package com.mask.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import butterknife.BindView;
import butterknife.OnClick;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.mask.R;
import com.mask.adapter.GroupAdapter;
import com.mask.base.BaseActivity;
import com.mask.bean.MyGroup;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends BaseActivity implements SwipeMenuListView.OnMenuItemClickListener, AdapterView.OnItemClickListener {
    @BindView(R.id.listView)
    SwipeMenuListView listView;
    GroupAdapter adapter;
    List<MyGroup> mList;

    @Override
    protected int getContentView() {
        return R.layout.activity_group;
    }

    @Override
    protected void init() {
        mList=new ArrayList<>();
        mList.addAll(DataSupport.findAll(MyGroup.class));
        adapter = new GroupAdapter(mList, this);
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
         startActivity(new Intent(this,SetGroupActivity.class));
    }

    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

        DataSupport.delete(MyGroup.class, mList.get(position).getId());
        mList.remove(position);
        adapter.notifyDataSetChanged();
        return false;
    }

    protected int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }


    @OnClick({R.id.equipment_left_tv, R.id.iv_equipment_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.equipment_left_tv:
                finish();
                break;
            case R.id.iv_equipment_right:
                startActivity(new Intent(this, AddActivity.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mList.clear();
        mList.addAll( DataSupport.findAll(MyGroup.class));
        adapter.setmList(mList);
    }
}
