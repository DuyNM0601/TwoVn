package com.example.twovn.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.twovn.R;
import com.example.twovn.api.APIClient;
import com.example.twovn.model.Order;
import com.example.twovn.model.OrderDetail;
import com.example.twovn.model.Product;
import com.example.twovn.service.OrderDetailService;
import com.squareup.picasso.Picasso;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<OrderDetail> orderDetailList;

    public OrderAdapter(Context context, List<OrderDetail> orderDetailList) {
        this.context = context;
        this.orderDetailList = orderDetailList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderDetail orderDetail = orderDetailList.get(position);

        holder.textViewTen.setText(orderDetail.getProductId().getName());
        holder.textViewGia.setText(String.format("%,.2f đ", orderDetail.getPrice()));
        holder.textViewQuantity.setText(String.valueOf(orderDetail.getQuantity()));
        Picasso.get().load(orderDetail.getProductId().getUrlImg()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return orderDetailList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewTen, textViewGia, textViewQuantity;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewTen = itemView.findViewById(R.id.textViewTen);
            textViewGia = itemView.findViewById(R.id.textViewGia);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
        }
    }

    public void setOrderDetailList(List<OrderDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
        notifyDataSetChanged();
    }
}
