package huce.fit.myapplication.util;

import huce.fit.myapplication.Helper.HMac.HexStringUtil;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Helpers {
    /**
     * Tính toán mã băm HMAC-SHA256 theo chuẩn ZaloPay
     */
    public static String getHmac256(String key, String data) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            // Sử dụng chính HexStringUtil bạn đã cung cấp để chuyển byte[] sang Hex String
            return HexStringUtil.byteArrayToHexString(sha256_HMAC.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return "";
        }
    }
}
