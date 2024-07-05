package com.example.twovn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.twovn.model.Auth;
import com.example.twovn.service.AuthService;
import com.example.twovn.repo.AuthRepository;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginSignUpActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView signUpTextView;

    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);

        authService = AuthRepository.getAuthService();

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        signUpTextView = findViewById(R.id.signupText);

        loginButton.setOnClickListener(v -> loginUser());
        signUpTextView.setOnClickListener(v -> navigateToSignUp());
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        Auth auth = new Auth(email, "", password); // Assuming your Auth model has the required constructor

        Call<Auth> call = authService.loginUser(auth);
        call.enqueue(new Callback<Auth>() {
            @Override
            public void onResponse(Call<Auth> call, Response<Auth> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(LoginSignUpActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    // Extract the userId from response body
                    Auth auth = response.body();
                    String userId = auth.getSub(); // Assuming getSub() method exists in Auth model to get userId
                    Log.d("UserId1", "Adding product to cart: " + userId);
                    // Save session with userId
                    saveSession(userId);

                    startActivity(new Intent(LoginSignUpActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginSignUpActivity.this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<Auth> call, Throwable t) {
                Toast.makeText(LoginSignUpActivity.this, "Đăng nhập thất bại! " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSession(String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("MySession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true); // Set isLoggedIn to true
        editor.putString("userId", userId); // Save userId
        editor.apply();
    }

    private void navigateToSignUp() {
        startActivity(new Intent(LoginSignUpActivity.this, SignUpActivity.class));
    }
}
