package huce.fit.myapplication.Api;

import android.util.Log;
import org.json.JSONObject;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpProvider {
    public static JSONObject sendPost(String URL, RequestBody formBody) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(URL)
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                return null;
            }
            String responseData = response.body().string();
            Log.d("ZaloPay_Http", "Response: " + responseData);
            return new JSONObject(responseData);
        } catch (Exception e) {
            Log.e("ZaloPay_Http", "Error: " + e.getMessage());
            return null;
        }
    }
}
