package cn.sunday.imoochybridandroidnative;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.sunday.R;
import cn.sunday.imoochybridandroidnative.util.WeiXinConstants;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static cn.sunday.imoochybridandroidnative.util.WeiXinConstants.APP_ID;
import static cn.sunday.imoochybridandroidnative.util.WeiXinConstants.PARTNER_ID;

/**
 * App发起支付
 * https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_12&index=2
 */
public class WXPayActivity extends AppCompatActivity {
    OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
    private static final String TAG = "PayActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxpay);
        final IWXAPI wxapi = WXAPIFactory.createWXAPI(this, APP_ID,false);
        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setEnabled(false);
                String url = "http://best-pay.springboot.cn/pay?amount=0.1&payType=WXPAY_APP&orderId=" + System.currentTimeMillis();
                Request request = new Request.Builder().url(url).build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        button.setEnabled(true);
                        Toast.makeText(WXPayActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                Log.i(TAG,jsonObject.toString());
                                String p = jsonObject.getString("package");
                                if (p.contains("prepay_id=")) {
                                    String prepayId = p.replace("prepay_id=", "");
                                    String nonceStr = jsonObject.getString("nonceStr");
                                    String timeStamp = jsonObject.getString("timeStamp");
                                    String sign = jsonObject.getString("paySign");
                                    PayReq req = new PayReq();
                                    req.appId = WeiXinConstants.APP_ID;
                                    req.partnerId = WeiXinConstants.PARTNER_ID;
                                    req.prepayId = prepayId;
                                    req.packageValue = WeiXinConstants.PACKAGE_VALUE;
                                    req.nonceStr = nonceStr;
                                    req.timeStamp = timeStamp;
                                    req.sign = sign;
                                    boolean result = wxapi.sendReq(req);
                                    Toast.makeText(WXPayActivity.this, "调起支付结果:" +result, Toast.LENGTH_LONG).show();
//
                                } else {
                                    Toast.makeText(WXPayActivity.this, "数据出错", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        button.setEnabled(true);
                    }
                });
            }
        });
    }
}
