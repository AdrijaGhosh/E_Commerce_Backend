package com.example.ECommerceBackend.services;

import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {
    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;
    public String getKeyId()
    {
        return keyId;
    }

    public com.razorpay.Order createRazorpayOrder(double amount) throws Exception
    {
        RazorpayClient client=new RazorpayClient(keyId,keySecret);
        JSONObject options=new JSONObject();
        options.put("amount", (int) (amount * 100));
        options.put("currency", "INR");
        options.put("receipt", "receipt_" + System.currentTimeMillis());
        return client.orders.create(options);
    }

    public boolean verifySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) throws Exception {
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", razorpayOrderId);
        options.put("razorpay_payment_id", razorpayPaymentId);
        options.put("razorpay_signature", razorpaySignature);
        return com.razorpay.Utils.verifyPaymentSignature(options, keySecret);
    }
}
