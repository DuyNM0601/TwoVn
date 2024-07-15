package com.example.twovn;

import android.content.Context;
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
import com.example.twovn.api.APIClient;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class OrderSummaryActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<Product> cartProductList = new ArrayList<>();
    private TextView totalAmountTextView, shippingFeeTextView, grandTotalTextView;
    private TextView userNameTextView, userEmailTextView, userPhoneNumberTextView, userAddressTextView, txtChangeProfile;
    private Button buttonPlaceOrder;
    private RadioGroup paymentMethodRadioGroup;
    private Spinner branchSpinner;
    private MapView branchMapView;
    private GoogleMap googleMap;

    private final LatLng[] branchLocations = {
            new LatLng(10.77391933567824, 106.68930109126615), // Chi nhánh 1
            new LatLng(10.803783603842847, 106.68931218078278), // Chi nhánh 2
            new LatLng(10.831812226183368, 106.67848996138723), // Chi nhánh 3
            new LatLng(10.797866978837535, 106.64805823758375), // Chi nhánh 4
            new LatLng(10.84435644588202, 106.78139720651212)  // Chi nhánh 5
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
        txtChangeProfile = findViewById(R.id.txtChangeProfile);

        branchMapView.onCreate(savedInstanceState);
        branchMapView.getMapAsync(this);

        String[] branches = {"CN1: 264A-264B-264C Nguyễn Thị Minh Khai, Phường 6, Quận 3, Hồ Chí Minh ", "CN2: 26 Phan Đăng Lưu, Phường 6, Bình Thạnh, Hồ Chí Minh", "CN3: 2A Nguyễn Oanh, Phường 7, Gò Vấp, Hồ Chí Minh", "CN4: 02 Hoàng Hoa Thám, Phường 13, Tân Bình, Hồ Chí Minh", "CN5: 164 Lê Văn Việt, Tăng Nhơn Phú B, Quận 9, Hồ Chí Minh"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branches);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branchSpinner.setAdapter(adapter);

        txtChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderSummaryActivity.this, TtcnActivity.class));
            }
        });

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
                String txtPhone = userPhoneNumberTextView.getText().toString();
                String txtAddress = userAddressTextView.getText().toString();
                if (txtPhone.isEmpty()){
                    Toast.makeText(OrderSummaryActivity.this, "Vui lòng thêm số điện thoại", Toast.LENGTH_SHORT).show();
                } else if (txtAddress.isEmpty()) {
                    Toast.makeText(OrderSummaryActivity.this, "Vui lòng thêm địa chỉ", Toast.LENGTH_SHORT).show();
                }else{
                    placeOrder();
                }

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
        SharedPreferences sharedPreferences = getSharedPreferences("MySession", MODE_PRIVATE);
        String accountId = sharedPreferences.getString("userId", null);

        if (accountId != null && !cartProductList.isEmpty()) {
            int selectedPaymentMethodId = paymentMethodRadioGroup.getCheckedRadioButtonId();
            if (selectedPaymentMethodId == -1) {
                Toast.makeText(OrderSummaryActivity.this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
            } else{
                processOrder(accountId, cartProductList);
            }

        } else {
            Toast.makeText(this, "Thông tin tài khoản hoặc giỏ hàng không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void processOrder(String accountId, List<Product> products) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("accountId", accountId);

        JsonArray productsArray = new JsonArray();
        for (Product product : products) {
            JsonObject productObject = new JsonObject();
            productObject.addProperty("productId", product.get_id());
            productObject.addProperty("quantity", product.getQuantity());
            productObject.addProperty("price", product.getPrice());
            productsArray.add(productObject);
        }
        requestBody.add("products", productsArray);

        // Gọi API process-order
        Retrofit retrofit = APIClient.getClient();
        OrderService orderService = retrofit.create(OrderService.class);
        Call<JsonObject> call = orderService.processOrder(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().has("paymentId")) {
                        String paymentId = response.body().get("paymentId").getAsString();
                        Log.d("payment idddddddddddddddddddđ", paymentId);
                        String redirectUrl = "twovn://qldn";
                        // Tạo URL vnp_ReturnUrl với paymentId
                        String returnUrl = "https://computer-shop-steel.vercel.app/payments/success/" + paymentId + "?returnUrl=" + redirectUrl;
                        int selectedPaymentMethodId = paymentMethodRadioGroup.getCheckedRadioButtonId();
                        if (selectedPaymentMethodId == R.id.radioPaymentOnDelivery) {

                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(returnUrl));
                            startActivity(browserIntent);
                            clearCart();
                            Toast.makeText(OrderSummaryActivity.this, "Đặt hàng thành công với phương thức thanh toán khi nhận hàng!", Toast.LENGTH_SHORT).show();
                        }
                        else if (selectedPaymentMethodId == R.id.radioPaymentWithVNPay) {
                            // Tiếp tục với thanh toán VNPay
                            String amountText = grandTotalTextView.getText().toString().trim();
                            String amountString = amountText.replace(",", "").replace(" đ", ""); // Remove "," and " đ" characters
                            if (!amountString.isEmpty()) {
                                List<String> productIds = new ArrayList<>();
                                for (Product product : cartProductList) {
                                    productIds.add(product.get_id());
                                }
                                clearCart();
                                processPayment(amountString, productIds, returnUrl); // Truyền returnUrl vào hàm processPayment
                            } else {
                                Toast.makeText(OrderSummaryActivity.this, "Vui lòng nhập số tiền cần thanh toán", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } else {
                        Toast.makeText(OrderSummaryActivity.this, "Không tìm thấy paymentId trong phản hồi từ server", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OrderSummaryActivity.this, "Không thể xử lý đơn hàng, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(OrderSummaryActivity.this, "Xảy ra lỗi khi xử lý đơn hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearCart() {
        CartRepository cartRepository = CartRepository.getInstance(getApplicationContext());
        SharedPreferences sharedPreferences = getSharedPreferences("MySession", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);
        if (userId != null) {
            cartRepository.clearCart(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        // Cart cleared successfully
                    } else {
                        // Handle unsuccessful response
                        Toast.makeText(OrderSummaryActivity.this, "Không thể xóa giỏ hàng", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Handle failure
                    Toast.makeText(OrderSummaryActivity.this, "Lỗi khi xóa giỏ hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(OrderSummaryActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }


    private void processPayment(String amount, List<String> productIds, String returnUrl) {
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
        vnp.addRequestData("vnp_ReturnUrl", returnUrl);
        vnp.addRequestData("vnp_TxnRef", String.valueOf(System.currentTimeMillis()));
        String paymentUrl = vnp.createRequestUrl("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html", "ZVHGSYOLSXBEJFQXYMADKXQBXHUFPAEC");
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
        startActivity(browserIntent);
    }



    // Interface cho Retrofit
    public interface OrderService {
        @POST("process-order")
        Call<JsonObject> processOrder(@Body JsonObject requestBody);
    }

    @Override
    protected void onResume() {
        super.onResume();
        branchMapView.onResume();
        loadUserInfo();
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
