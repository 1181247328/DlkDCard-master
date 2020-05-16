package com.cdqf.cart.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cdqf.cart_utils.HttpRequestWrap;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import de.greenrobot.event.EventBus;


/**
 * 微信支付
 * Created by XinAiXiaoWen on 2017/4/22.
 */

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private String TAG = WXPayEntryActivity.class.getSimpleName();

    private static final String APP_ID = "wx14bb90ec0c084dcb";

    private EventBus eventBus = EventBus.getDefault();

    private HttpRequestWrap httpRequestWrap = null;

    private IWXAPI iwxapi = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "---微信支付---");
        iwxapi = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID);
        iwxapi.handleIntent(getIntent(), this);
        httpRequestWrap = new HttpRequestWrap(this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        String msg = "";
        String s = baseResp.errStr;
        Log.e(TAG, "---errCode---" + baseResp.errCode);
        Log.e(TAG, "---errStr---" + baseResp.errStr);
        Log.e(TAG, "---transaction---" + baseResp.transaction);
        Log.e(TAG, "---openId---" + baseResp.openId);
        switch (baseResp.errCode) {
            case 0:
                msg = "支付成功";
                Toast.makeText(WXPayEntryActivity.this, msg, Toast.LENGTH_SHORT).show();
                eventBus.post(new WXReturnFind());
                finish();
//                httpRequestWrap.setMethod(HttpRequestWrap.POST);
//                httpRequestWrap.setCallBack(new RequestHandler(WXPayEntrtyActivity.this, 1, "请稍候...", new OnResponseHandler() {
//                    @Override
//                    public void onResponse(String result, RequestStatus status) {
//
//                    }
//                }));
//                Map<String, Object> paramss = new HashMap<String, Object>();
//                httpRequestWrap.send(DrunkAddress.HSOP, paramss);
//                finish();
                break;
            case -1:
                msg = "支付错误";
                eventBus.post(new WXReturnFind());
                Toast.makeText(WXPayEntryActivity.this, msg, Toast.LENGTH_SHORT).show();
                finish();
                break;
            case -2:
                Log.e(TAG, "---支付取消---");
                msg = "支付取消";
                eventBus.post(new WXReturnFind());
                Toast.makeText(WXPayEntryActivity.this, msg, Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        iwxapi.handleIntent(intent, this);
    }
}
