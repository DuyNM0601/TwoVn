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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.twovn.model.Auth;
import com.example.twovn.service.AuthService;
import com.example.twovn.repo.AuthRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginSignUpActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView signUpTextView;

    private AuthService authService;
    private String email, password;

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
        email = emailEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Please enter your email");
            emailEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Please enter your password");
            passwordEditText.requestFocus();
            return;
        }

        Auth auth = new Auth(email, "", password);

        Call<Auth> call = authService.loginUser(auth);
        call.enqueue(new Callback<Auth>() {
            @Override
            public void onResponse(Call<Auth> call, Response<Auth> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (email.equals("admin@gmail.com") && password.equals("admin123")) {
                        saveSession(response.body().getSub(), "admin");
                    } else {
                        saveSession(response.body().getSub(), "user");
                    }
                    Siginin();
                    Toast.makeText(LoginSignUpActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    Auth auth = response.body();
                    String userId = auth.getSub();
                    Log.d("UserId1", "Adding product to cart: " + userId);
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

    private void saveSession(String userId, String role) {
        SharedPreferences sharedPreferences = getSharedPreferences("MySession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("userId", userId);
        editor.putString("role", role);
        editor.apply();
    }


    private void navigateToSignUp() {
        startActivity(new Intent(LoginSignUpActivity.this, SignUpActivity.class));
    }

    private void Siginin(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.trim(), password.trim())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        SharedPreferences sharedPreferences = getSharedPreferences("MySession", Context.MODE_PRIVATE);
                        String role = sharedPreferences.getString("role", "");

                        if (role.equals("admin")) {
                            startActivity(new Intent(LoginSignUpActivity.this, UserMessageActivity.class));
                        } else {
                            startActivity(new Intent(LoginSignUpActivity.this, MainActivity.class));
                        }
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof FirebaseAuthInvalidUserException){
                            Toast.makeText(LoginSignUpActivity.this, "Người dùng không tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
