package cn.sunday.imoochybridandroidnative;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cn.sunday.imoochybridandroidnative.util.WeiXinConstants;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private final String TAG = "WXPayEntryActivity";

    private IWXAPI api;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(cn.sunday.R.layout.pay_result);
        api = WXAPIFactory.createWXAPI(this, WeiXinConstants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }
    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.i(TAG,"errCode = " + resp.errCode);
        //最好依赖于商户后台的查询结果
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            //如果返回-1，很大可能是因为应用签名的问题。用官方的工具生成
            //签名工具下载：https://open.weixin.qq.com/zh_CN/htmledition/res/dev/download/sdk/Gen_Signature_Android.apk
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage(getString(cn.sunday.R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
            builder.show();
        }
    }
}