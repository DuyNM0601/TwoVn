package com.example.twovn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.twovn.adapter.CartAdapter;
import com.example.twovn.model.Account;
import com.example.twovn.model.Product;
import com.example.twovn.repo.AccountRepository;
import com.example.twovn.repo.CartRepository;
import com.example.twovn.utils.VNPayUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderSummaryActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<Product> cartProductList = new ArrayList<>();
    private TextView totalAmountTextView, shippingFeeTextView, grandTotalTextView;
    private TextView userNameTextView, userEmailTextView, userPhoneNumberTextView, userAddressTextView;
    private Button buttonPlaceOrder;
    private RadioGroup paymentMethodRadioGroup;
    private Spinner branchSpinner;
    private MapView branchMapView;
    private GoogleMap googleMap;

    private final LatLng[] branchLocations = {
            new LatLng(10.020326169782301, 105.7686582032475), // Chi nhánh 1
            new LatLng(10.775438432508537, 106.69126500584572), // Chi nhánh 2
            new LatLng(10.831812226183368, 106.67848996138723), // Chi nhánh 3
            new LatLng(10.797866978837535, 106.64805823758375), // Chi nhánh 4
            new LatLng(10.746600734218527, 106.62576577515718)  // Chi nhánh 5
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        recyclerView = findViewById(R.id.recyclerViewOrderSummary);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartProductList, null, false);
        recyclerView.setAdapter(cartAdapter);

        totalAmountTextView = findViewById(R.id.totalAmountTextView);
        shippingFeeTextView = findViewById(R.id.shippingFeeTextView);
        grandTotalTextView = findViewById(R.id.grandTotalTextView);
        buttonPlaceOrder = findViewById(R.id.buttonPlaceOrder);
        paymentMethodRadioGroup = findViewById(R.id.paymentMethodRadioGroup);
        branchSpinner = findViewById(R.id.branchSpinner);
        branchMapView = findViewById(R.id.branchMapView);

        userNameTextView = findViewById(R.id.userNameTextView);
        userEmailTextView = findViewById(R.id.userEmailTextView);
        userPhoneNumberTextView = findViewById(R.id.userPhoneNumberTextView);
        userAddressTextView = findViewById(R.id.userAddressTextView);

        branchMapView.onCreate(savedInstanceState);
        branchMapView.getMapAsync(this);

        String[] branches = {"Chi nhánh 1", "Chi nhánh 2", "Chi nhánh 3", "Chi nhánh 4", "Chi nhánh 5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branches);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branchSpinner.setAdapter(adapter);

        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (googleMap != null) {
                    updateMapView(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            cartProductList = intent.getParcelableArrayListExtra("cartProductList");
            String totalAmountStr = intent.getStringExtra("totalAmount");
            int totalAmount = Integer.parseInt(totalAmountStr.replace(",", "").replace(" đ", ""));
            Log.d("TAG321321", "onCreate: " + totalAmount);
            cartAdapter.setCartProductList(cartProductList);
            updateTotalAmount(totalAmount);
        }

        loadUserInfo();

        buttonPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });
    }

    private void loadUserInfo() {
        // Lấy thông tin từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MySession", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            AccountRepository.getAccountService().getAccountById(userId).enqueue(new Callback<Account>() {
                @Override
                public void onResponse(Call<Account> call, Response<Account> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Account account = response.body();
                        userNameTextView.setText(account.getUserName());
                        userEmailTextView.setText(account.getEmail());
                        userPhoneNumberTextView.setText(account.getPhoneNumber());
                        userAddressTextView.setText(account.getAddress());
                    }
                }

                @Override
                public void onFailure(Call<Account> call, Throwable t) {
                    Log.e("OrderSummaryActivity", "Error loading user info: " + t.getMessage());
                }
            });
        } else {
            Log.e("OrderSummaryActivity", "UserId is null");
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        updateMapView(0);
    }

    private void updateMapView(int branchIndex) {
        LatLng location = branchLocations[branchIndex];
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(location).title("Chi nhánh " + (branchIndex + 1)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
    }

    private void updateTotalAmount(double totalAmount) {
        double shippingFee = 30000;
        double grandTotal = totalAmount + shippingFee;

        totalAmountTextView.setText(String.format("%,.0f đ", totalAmount));
        shippingFeeTextView.setText(String.format("%,.0f đ", shippingFee));
        grandTotalTextView.setText(String.format("%,.0f đ", grandTotal));
    }

    private void placeOrder() {
        int selectedPaymentMethodId = paymentMethodRadioGroup.getCheckedRadioButtonId();
        if (selectedPaymentMethodId == R.id.radioPaymentOnDelivery) {
            Toast.makeText(this, "Đặt hàng thành công với phương thức thanh toán khi nhận hàng!", Toast.LENGTH_SHORT).show();
        } else if (selectedPaymentMethodId == R.id.radioPaymentWithVNPay) {
            String amountText = grandTotalTextView.getText().toString().trim();
            String amountString = amountText.replace(",", "").replace(" đ", ""); // Remove "," and " đ" characters
            if (!amountString.isEmpty()) {
                List<String> productIds = new ArrayList<>();
                for (Product product : cartProductList) {
                    productIds.add(product.get_id());
                }
                processPayment(amountString, productIds);
            } else {
                Toast.makeText(this, "Vui lòng nhập số tiền cần thanh toán", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void processPayment(String amount, List<String> productIds) {
        long amountLong = (long) (Double.parseDouble(amount) * 100);
        SharedPreferences sharedPreferences = getSharedPreferences("MySession", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);
        Random random = new Random();
        int orderCode = random.nextInt(900000) + 100000;

        // Chuyển đổi danh sách productIds thành chuỗi
        String productIdsString = android.text.TextUtils.join(",", productIds);
        String redirectUrl = "twovn://qldn";

        VNPayUtils vnp = new VNPayUtils();
        vnp.addRequestData("vnp_Version", "2.1.0");
        vnp.addRequestData("vnp_TmnCode", "KNV7ASZQ");
        vnp.addRequestData("vnp_Amount", String.valueOf(amountLong));
        vnp.addRequestData("vnp_Command", "pay");
        vnp.addRequestData("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        vnp.addRequestData("vnp_CurrCode", "VND");
        vnp.addRequestData("vnp_IpAddr", "127.0.0.1");
        vnp.addRequestData("vnp_Locale", "vn");
        vnp.addRequestData("vnp_OrderInfo", "Thanh toán đơn hàng" + orderCode);
        vnp.addRequestData("vnp_OrderType", "other");
        vnp.addRequestData("vnp_ReturnUrl", "https://computer-shop-steel.vercel.app/orders?accountId=" + userId + "&totalAmount=" + amountLong + "&redirectUrl=" + redirectUrl);
        vnp.addRequestData("vnp_TxnRef", String.valueOf(System.currentTimeMillis()));

        String paymentUrl = vnp.createRequestUrl("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html", "ZVHGSYOLSXBEJFQXYMADKXQBXHUFPAEC");
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
        startActivity(browserIntent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        branchMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        branchMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        branchMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        branchMapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        branchMapView.onSaveInstanceState(outState);
    }
}
