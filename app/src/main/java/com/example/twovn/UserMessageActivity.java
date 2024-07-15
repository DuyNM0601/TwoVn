package com.example.twovn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.twovn.adapter.UserAdapter;
import com.example.twovn.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserMessageActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    String yourName;
    DatabaseReference databaseReference;
    UserAdapter userAdapter;
    Button button_Logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_message);

        Toolbar toolbar = findViewById(R.id.toolbarMessage);
        setSupportActionBar(toolbar);

        String userName = getIntent().getStringExtra("user");
        getSupportActionBar().setTitle(userName);

        userAdapter = new UserAdapter(this);
        recyclerView = findViewById(R.id.recycleview);

        recyclerView.setAdapter(userAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        button_Logout = findViewById(R.id.button_Logout);
        button_Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userAdapter.clear(); // Xóa danh sách người dùng hiện tại để cập nhật lại
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String uId = dataSnapshot.getKey();
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    if (userModel != null && userModel.getUserID() != null && !userModel.getUserID().equals(FirebaseAuth.getInstance().getUid())) {
                        userAdapter.add(userModel);
                    }
                }
                List<UserModel> userModelList = userAdapter.getUserModelList();
                userAdapter.notifyDataSetChanged();

                // Log để kiểm tra dữ liệu đã được lấy về hay không
                Log.d("UserMessageActivity", "Number of users retrieved: " + userModelList.size());
                for (UserModel userModel : userModelList) {
                    Log.d("UserMessageActivity", "User: " + userModel.getUserName() + " - " + userModel.getUserEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra trong khi đọc dữ liệu từ Firebase
                Log.e("UserMessageActivity", "Error fetching users: " + error.getMessage());
            }
        });

    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        // Xóa session
        SharedPreferences sharedPreferences = getSharedPreferences("MySession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Xóa tất cả dữ liệu trong SharedPreferences
        editor.apply();

        // Chuyển hướng người dùng về trang đăng nhập
        Intent intent = new Intent(UserMessageActivity.this, LoginSignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa tất cả các activity trước đó
        startActivity(intent);
        finish();
    }
}
