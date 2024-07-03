package com.example.twovn;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.twovn.adapter.CartAdapter;
import com.example.twovn.model.Product;
import com.example.twovn.repo.CartRepository;
import com.example.twovn.utils.VNPayUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CartFragment extends Fragment implements CartAdapter.OnQuantityChangeListener {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<Product> cartProductList = new ArrayList<>();
    private TextView totalAmountTextView;
    private Button buttonCheckout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(getContext(), cartProductList, this);
        recyclerView.setAdapter(cartAdapter);

        totalAmountTextView = view.findViewById(R.id.totalAmountTextView);
        buttonCheckout = view.findViewById(R.id.buttonCheckout);
        buttonCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountText = totalAmountTextView.getText().toString().trim();
                String amountString = amountText.replace(",", "").replace(" đ", ""); // Loại bỏ ký tự "," và " đ"
                if (!amountString.isEmpty()) {
                    processPayment(amountString);
                } else {
                    Toast.makeText(getContext(), "Vui lòng nhập số tiền cần thanh toán", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadCartProducts();

        return view;
    }

    private void loadCartProducts() {
        cartProductList.clear();
        List<Product> allProducts = CartRepository.getInstance().getCartProducts();
        for (Product product : allProducts) {
            product.setQuantity(1);
            cartProductList.add(product);
        }
        cartAdapter.notifyDataSetChanged();
        updateTotalAmount();
    }

    @Override
    public void onQuantityChanged() {
        updateTotalAmount();
    }

    private void updateTotalAmount() {
        double totalAmount = 0;
        for (Product product : cartProductList) {
            totalAmount += product.getPrice() * product.getQuantity();
        }
        totalAmountTextView.setText(String.format("%,.0f đ", totalAmount));
    }

    private void processPayment(String amount) {
        long amountLong = (long) (Double.parseDouble(amount) * 100); // Amount in VND

        VNPayUtils vnp = new VNPayUtils();
        vnp.addRequestData("vnp_Version", "2.1.0");
        vnp.addRequestData("vnp_TmnCode", "KNV7ASZQ");
        vnp.addRequestData("vnp_Amount", String.valueOf(amountLong));
        vnp.addRequestData("vnp_Command", "pay");
        vnp.addRequestData("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        vnp.addRequestData("vnp_CurrCode", "VND");
        vnp.addRequestData("vnp_IpAddr", "127.0.0.1");
        vnp.addRequestData("vnp_Locale", "vn");
        vnp.addRequestData("vnp_OrderInfo", "Thanh toán đơn hàng");
        vnp.addRequestData("vnp_OrderType", "other");
        vnp.addRequestData("vnp_ReturnUrl", "twovn://fragment_cart");
        vnp.addRequestData("vnp_TxnRef", String.valueOf(System.currentTimeMillis()));

        String paymentUrl = vnp.createRequestUrl("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html", "ZVHGSYOLSXBEJFQXYMADKXQBXHUFPAEC");
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
        startActivity(browserIntent);
    }
}
