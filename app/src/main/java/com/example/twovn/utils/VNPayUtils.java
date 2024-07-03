package com.example.twovn.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VNPayUtils {
    private SortedMap<String, String> requestData = new TreeMap<>();
    private SortedMap<String, String> responseData = new TreeMap<>();

    public void addRequestData(String key, String value) {
        if (value != null && !value.isEmpty()) {
            requestData.put(key, value);
        }
    }

    public void addResponseData(String key, String value) {
        if (value != null && !value.isEmpty()) {
            responseData.put(key, value);
        }
    }

    public String getResponseData(String key) {
        return responseData.getOrDefault(key, "");
    }

    public String createRequestUrl(String baseUrl, String vnpHashSecret) {
        StringBuilder data = new StringBuilder();

        for (Map.Entry<String, String> entry : requestData.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                try {
                    data.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                            .append("&");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        String querystring = data.toString();
        if (querystring.length() > 0) {
            querystring = querystring.substring(0, querystring.length() - 1);
        }

        String signData = querystring;
        String vnpSecureHash = hmacSHA512(vnpHashSecret, signData);
        return baseUrl + "?" + querystring + "&vnp_SecureHash=" + vnpSecureHash;
    }

    public boolean validateSignature(String inputHash, String secretKey) {
        String rspRaw = getResponseDataString();
        String myChecksum = hmacSHA512(secretKey, rspRaw);
        return myChecksum.equalsIgnoreCase(inputHash);
    }

    private String getResponseDataString() {
        StringBuilder data = new StringBuilder();
        if (responseData.containsKey("vnp_SecureHashType")) {
            responseData.remove("vnp_SecureHashType");
        }

        if (responseData.containsKey("vnp_SecureHash")) {
            responseData.remove("vnp_SecureHash");
        }

        for (Map.Entry<String, String> entry : responseData.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                try {
                    data.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                            .append("&");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        if (data.length() > 0) {
            data.deleteCharAt(data.length() - 1);
        }

        return data.toString();
    }

    public static String hmacSHA512(String key, String inputData) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(secretKeySpec);

            byte[] hashBytes = hmac.doFinal(inputData.getBytes(StandardCharsets.UTF_8));
            StringBuilder hashString = new StringBuilder();

            for (byte b : hashBytes) {
                hashString.append(String.format("%02x", b));
            }

            return hashString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC SHA-512", e);
        }
    }
}
