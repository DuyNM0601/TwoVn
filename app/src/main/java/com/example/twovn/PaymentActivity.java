package com.example.twovn;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.twovn.utils.VNPayUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentActivity extends AppCompatActivity {

    TextView totalAmountTextView;
    Button btnContinue;

    private static final String VNPAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String VNPAY_TMN_CODE = "KNV7ASZQ";
    private static final String VNPAY_HASH_SECRET = "ZVHGSYOLSXBEJFQXYMADKXQBXHUFPAEC";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_cart);

        totalAmountTextView = findViewById(R.id.totalAmountTextView);
        btnContinue = findViewById(R.id.buttonCheckout);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = totalAmountTextView.getText().toString().trim();
                if (!amount.isEmpty()) {
                    processPayment(amount);
                    Log.d("PaymentActivity", "Button clicked with amount: " + amount);
                } else {
                    Toast.makeText(PaymentActivity.this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void processPayment(String amount) {
        long amountLong = Long.parseLong(amount) * 100; // Amount in VND

        VNPayUtils vnp = new VNPayUtils();
        vnp.addRequestData("vnp_Version", "2.1.0");
        vnp.addRequestData("vnp_TmnCode", VNPAY_TMN_CODE);
        vnp.addRequestData("vnp_Amount", String.valueOf(amountLong));
        vnp.addRequestData("vnp_Command", "pay");
        vnp.addRequestData("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        vnp.addRequestData("vnp_CurrCode", "VND");
        vnp.addRequestData("vnp_IpAddr", "127.0.0.1");
        vnp.addRequestData("vnp_Locale", "vn");
        vnp.addRequestData("vnp_OrderInfo", "Payment for order");
        vnp.addRequestData("vnp_OrderType", "other");
        vnp.addRequestData("vnp_ReturnUrl", "https://yourcallbackurl.com");
        vnp.addRequestData("vnp_TxnRef", String.valueOf(System.currentTimeMillis()));

        String paymentUrl = vnp.createRequestUrl(VNPAY_URL, VNPAY_HASH_SECRET);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
        startActivity(browserIntent);
    }
}
