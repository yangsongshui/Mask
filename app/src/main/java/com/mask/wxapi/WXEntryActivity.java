package com.mask.wxapi;


import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.mask.app.MyApplication;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册API
        api = MyApplication.newInstance().api;
        api.handleIntent(getIntent(), this);

    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    //  发送到微信请求的响应结果
    @Override
    public void onResp(BaseResp resp) {
        if (resp instanceof SendAuth.Resp) {
            SendAuth.Resp newResp = (SendAuth.Resp) resp;
            //获取微信传回的code
            String code = newResp.code;


        }

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                Toast.makeText(this,"发送成功",Toast.LENGTH_SHORT).show();
                //发送成功
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Toast.makeText(this,"发送取消",Toast.LENGTH_SHORT).show();
                //发送取消
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Toast.makeText(this,"发送失败",Toast.LENGTH_SHORT).show();
                //发送被拒绝
                break;
            default:
                Toast.makeText(this,"发送失败",Toast.LENGTH_SHORT).show();
                //发送返回
                break;
        }
        finish();

    }
}
