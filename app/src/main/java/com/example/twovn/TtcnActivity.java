package com.example.twovn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.twovn.model.Account;
import com.example.twovn.repo.AccountRepository;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TtcnActivity extends AppCompatActivity {

    Button loginButton, logoutButton;
    EditText nameText, phoneText, addressText, emailText;
    Button updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ttcn);

        nameText = findViewById(R.id.edtName);
        phoneText = findViewById(R.id.edtPhone);
        addressText = findViewById(R.id.edtAddress);
        emailText = findViewById(R.id.edtEmail);
        updateButton = findViewById(R.id.btnsave);
        loginButton = findViewById(R.id.btnLogin);
        logoutButton = findViewById(R.id.btnLogout);

        // Kiểm tra trạng thái đăng nhập
        checkLoginStatus();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình đăng nhập
                Intent intent = new Intent(TtcnActivity.this, LoginSignUpActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        loadUserInfo();
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cập nhật thông tin người dùng
                updateUser();
            }
        });
    }

    private void checkLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySession", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            // Đã đăng nhập
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
        } else {
            // Chưa đăng nhập
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
        }
    }

    private void logoutUser() {
        // Đăng xuất khỏi Firebase
        FirebaseAuth.getInstance().signOut();

        // Xóa session
        SharedPreferences sharedPreferences = getSharedPreferences("MySession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Xóa tất cả dữ liệu trong SharedPreferences
        editor.apply();

        // Cập nhật trạng thái đăng nhập
        checkLoginStatus();

        // Chuyển hướng người dùng về trang đăng nhập
        Intent intent = new Intent(TtcnActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa tất cả các activity trước đó
        startActivity(intent);
        finish();
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
                        nameText.setText(account.getUserName());
                        addressText.setText(account.getAddress());
                        phoneText.setText(account.getPhoneNumber());
                        emailText.setText(account.getEmail());
                    }
                }

                @Override
                public void onFailure(Call<Account> call, Throwable t) {
                    Log.e("Fail to load user info", "Error loading user info: " + t.getMessage());
                }
            });
        } else {
            Log.e("Fail user info", "UserId is null");
        }
    }

    private void updateUser() {
        // Cập nhật thông tin người dùng
        Account updatedAccount = new Account();
        updatedAccount.setUserName(nameText.getText().toString());
        updatedAccount.setAddress(addressText.getText().toString());
        updatedAccount.setPhoneNumber(phoneText.getText().toString());
        updatedAccount.setEmail(emailText.getText().toString());

        SharedPreferences sharedPreferences = getSharedPreferences("MySession", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            AccountRepository.getAccountService().updateAccount(userId, updatedAccount).enqueue(new Callback<Account>() {
                @Override
                public void onResponse(Call<Account> call, Response<Account> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(TtcnActivity.this, "User updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TtcnActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show();
                        Log.d("UpdateUser", "Response Code: " + response.code() + " Response Message: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<Account> call, Throwable t) {
                    Toast.makeText(TtcnActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(TtcnActivity.this, "UserId is null", Toast.LENGTH_SHORT).show();
        }
    }
}
