package com.example.twovn;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.profile_pj.ProfileFragment;
import com.example.twovn.model.Cart;
import com.example.twovn.repo.CartRepository;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "cart_notification_channel";
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("MySession", Context.MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "user");

        if ("admin".equals(role)) {
            startActivity(new Intent(MainActivity.this, UserMessageActivity.class));
            finish();
            return;
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        createNotificationChannel();

        // Kiểm tra và yêu cầu quyền nếu cần thiết trước khi kiểm tra giỏ hàng và thông báo
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
        } else {
            checkCartAndNotify();
        }

        // Load the default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomePageFragment());
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                selectedFragment = new HomePageFragment();
            } else if (itemId == R.id.navigation_profile) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.navigation_giohang) {
                selectedFragment = new CartFragment();
            }else if (itemId == R.id.chat) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("userId", "URSrrWSw8OfiBR29HwIbPlmNZ4m2");
                intent.putExtra("name", "Cửa hàng TwoVn");
                startActivity(intent);
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }


    private void checkCartAndNotify() {
        CartRepository cartRepository = CartRepository.getInstance(this);
        cartRepository.getCartProducts(new Callback<List<Cart>>() {
            @Override
            public void onResponse(Call<List<Cart>> call, Response<List<Cart>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Kiểm tra quyền trước khi hiển thị thông báo
                    if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        // Yêu cầu quyền nếu chưa được cấp
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
                    } else {
                        // Quyền đã được cấp, hiển thị thông báo và badge
                        showCartNotification(response.body().size());
                        showCartBadge(response.body().size());
                    }
                } else {
                    // Xóa badge nếu giỏ hàng rỗng
                    removeCartBadge();
                }
            }

            @Override
            public void onFailure(Call<List<Cart>> call, Throwable t) {
                Log.e("MainActivity", "Error fetching cart products: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Failed to load cart products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCartNotification(int itemCount) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_cart)
                .setContentTitle("Cart Notification")
                .setContentText("You have " + itemCount + " items in your cart.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private void showCartBadge(int itemCount) {
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.navigation_giohang);
        badge.setNumber(itemCount);
        badge.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        badge.setVisible(true);
    }

    private void removeCartBadge() {
        bottomNavigationView.removeBadge(R.id.navigation_giohang);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Cart Notification Channel";
            String description = "Channel for Cart Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp, kiểm tra lại giỏ hàng và hiển thị thông báo nếu cần
                checkCartAndNotify();
            } else {
                // Quyền bị từ chối, thông báo cho người dùng
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Phương thức để cập nhật lại badge sau khi xóa một món hàng
    public void updateCartBadge() {
        CartRepository cartRepository = CartRepository.getInstance(this);
        cartRepository.getCartProducts(new Callback<List<Cart>>() {
            @Override
            public void onResponse(Call<List<Cart>> call, Response<List<Cart>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int itemCount = response.body().size();
                    if (itemCount > 0) {
                        showCartBadge(itemCount);
                    } else {
                        removeCartBadge();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Cart>> call, Throwable t) {
                Log.e("MainActivity", "Error updating cart badge: " + t.getMessage());
            }
        });
    }
}
