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

public class CreateOrder {
    public JSONObject createOrder(String amount) throws Exception {
        String appId = ZaloPayConstant.APP_ID;
        String appUser = "huce_user";
        
        // Quan trọng: Phải dùng chung 1 giá trị thời gian cho cả app_time và app_trans_id
        long timestamp = new Date().getTime();
        String appTime = String.valueOf(timestamp);
        
        // Tạo app_trans_id theo chuẩn: yyMMdd_timestamp
        String appTransId = new SimpleDateFormat("yyMMdd").format(new Date()) + "_" + timestamp;
        
        String embedData = "{}";
        String item = "[]";
        String description = "Thanh toan don hang #" + appTransId;
        String bankCode = ""; 

        // CHUỖI TẠO MAC V2 BẮT BUỘC: app_id|app_trans_id|app_user|amount|app_time|embed_data|item
        String dataToHash = appId + "|" + appTransId + "|" + appUser + "|" + amount + "|" + appTime + "|" + embedData + "|" + item;
        
        Log.d("ZaloPay_Debug", "MAC Input String: " + dataToHash);
        
        // Dùng KEY1 của AppID 2554 để băm
        String mac = Helpers.getMac(ZaloPayConstant.KEY1, dataToHash);

        RequestBody formBody = new FormBody.Builder()
                .add("app_id", appId)
                .add("app_user", appUser)
                .add("app_trans_id", appTransId)
                .add("app_time", appTime)
                .add("amount", amount)
                .add("item", item)
                .add("embed_data", embedData)
                .add("description", description)
                .add("bank_code", bankCode)
                .add("mac", mac)
                .build();

        return HttpProvider.sendPost("https://sb-openapi.zalopay.vn/v2/create", formBody);
    }
}
