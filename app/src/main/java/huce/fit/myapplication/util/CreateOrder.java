package huce.fit.myapplication.util;

import android.util.Log;
import huce.fit.myapplication.Api.HttpProvider;
import huce.fit.myapplication.ZaloPayConstant;
import huce.fit.myapplication.Helper.Helpers;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateOrder {
    public JSONObject createOrder(String amount) throws Exception {
        String appId = ZaloPayConstant.APP_ID;
        String appUser = "huce_user";
        
        // Loại bỏ mọi ký tự không phải số nếu có
        String cleanAmount = amount.replaceAll("[^0-9]", "");
        
        long timestamp = new Date().getTime();
        String appTime = String.valueOf(timestamp);
        String appTransId = new SimpleDateFormat("yyMMdd", Locale.getDefault()).format(new Date()) + "_" + timestamp;
        
        String embedData = "{}";
        String item = "[]";
        String description = "Thanh toan don hang #" + appTransId;
        String bankCode = ""; 

        // MAC Input: app_id|app_trans_id|app_user|amount|app_time|embed_data|item
        String dataToHash = appId + "|" + appTransId + "|" + appUser + "|" + cleanAmount + "|" + appTime + "|" + embedData + "|" + item;
        
        String mac = Helpers.getMac(ZaloPayConstant.KEY1, dataToHash);

        RequestBody formBody = new FormBody.Builder()
                .add("app_id", appId)
                .add("app_user", appUser)
                .add("app_trans_id", appTransId)
                .add("app_time", appTime)
                .add("amount", cleanAmount)
                .add("item", item)
                .add("embed_data", embedData)
                .add("description", description)
                .add("bank_code", bankCode)
                .add("mac", mac)
                .build();

        JSONObject result = HttpProvider.sendPost("https://sb-openapi.zalopay.vn/v2/create", formBody);
        if (result != null) {
            Log.d("ZaloPay_Result", result.toString());
        }
        return result;
    }
}
