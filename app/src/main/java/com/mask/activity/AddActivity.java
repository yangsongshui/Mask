package com.mask.activity;

import android.view.View;
import android.widget.EditText;

import com.mask.R;
import com.mask.base.BaseActivity;
import com.mask.bean.MyGroup;

import butterknife.BindView;
import butterknife.OnClick;

public class AddActivity extends BaseActivity {


    @BindView(R.id.et)
    EditText et;

    @Override
    protected int getContentView() {
        return R.layout.activity_add;
    }

    @Override
    protected void init() {


    }


    @OnClick({R.id.add_left_tv, R.id.et_re, R.id.add_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_left_tv:
                finish();
                break;
            case R.id.et_re:
                et.setText("");
                break;
            case R.id.add_tv:
                if (!et.getText().toString().equals("")) {
                    MyGroup group = new MyGroup();
                    group.setName(et.getText().toString());
                    if (group.save())
                        finish();
                }
                break;
        }
    }
}
