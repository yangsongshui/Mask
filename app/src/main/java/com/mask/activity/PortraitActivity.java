package com.mask.activity;

import android.view.View;

import com.mask.R;
import com.mask.base.BaseActivity;
import com.mask.utils.SpUtils;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.mask.utils.Constant.HEAD_PORTRAIT;
import static com.mask.utils.Constant.id;

public class PortraitActivity extends BaseActivity {
    @BindView(R.id.me_pic_iv)
    CircleImageView mePicIv;

    @Override
    protected int getContentView() {
        return R.layout.activity_portrait;
    }

    @Override
    protected void init() {
        mePicIv.setImageResource(id[SpUtils.getInt(HEAD_PORTRAIT, 0)]);
    }

    @OnClick({R.id.equipment_left_tv, R.id.me_pic_iv, R.id.pic_tv1, R.id.pic_tv2, R.id.pic_tv3, R.id.pic_tv4, R.id.pic_tv5, R.id.pic_tv6, R.id.pic_tv7, R.id.pic_tv8})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.equipment_left_tv:
                finish();
                break;
            case R.id.pic_tv1:
                SpUtils.putInt(HEAD_PORTRAIT, 0);
                mePicIv.setImageResource(id[0]);
                break;
            case R.id.pic_tv2:
                SpUtils.putInt(HEAD_PORTRAIT, 1);
                mePicIv.setImageResource(id[1]);
                break;
            case R.id.pic_tv3:
                SpUtils.putInt(HEAD_PORTRAIT, 2);
                mePicIv.setImageResource(id[2]);
                break;
            case R.id.pic_tv4:
                SpUtils.putInt(HEAD_PORTRAIT, 3);
                mePicIv.setImageResource(id[3]);
                break;
            case R.id.pic_tv5:
                SpUtils.putInt(HEAD_PORTRAIT, 4);
                mePicIv.setImageResource(id[4]);
                break;
            case R.id.pic_tv6:
                SpUtils.putInt(HEAD_PORTRAIT, 5);
                mePicIv.setImageResource(id[5]);
                break;
            case R.id.pic_tv7:
                SpUtils.putInt(HEAD_PORTRAIT, 6);
                mePicIv.setImageResource(id[6]);
                break;
            case R.id.pic_tv8:
                SpUtils.putInt(HEAD_PORTRAIT, 7);
                mePicIv.setImageResource(id[7]);
                break;
        }
    }
}
