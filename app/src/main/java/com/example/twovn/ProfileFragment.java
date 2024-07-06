package com.example.profile_pj;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.twovn.QldnActivity;
import com.example.twovn.R;
import com.example.twovn.TtcnActivity;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    TextView userNameText;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ImageView imageView4 = view.findViewById(R.id.imageView4);
        userNameText = view.findViewById(R.id.userNameTextView);

        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TtcnActivity.class);
                startActivity(intent);
            }
        });

        ImageView imageView5 = view.findViewById(R.id.imageView5);
        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QldnActivity.class);
                startActivity(intent);
            }
        });
        loadUserInfo();
        return view;
    }
    private void loadUserInfo() {
        // Lấy thông tin từ SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySession", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            AccountRepository.getAccountService().getAccountById(userId).enqueue(new Callback<Account>() {
                @Override
                public void onResponse(Call<Account> call, Response<Account> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Account account = response.body();
                        userNameText.setText(account.getUserName());



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



}
