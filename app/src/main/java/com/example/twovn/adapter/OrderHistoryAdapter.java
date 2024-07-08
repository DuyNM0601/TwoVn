package com.example.twovn.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.twovn.R;
import com.example.twovn.model.Order;
import com.example.twovn.model.OrderDetail;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private Context context;
    private List<OrderDetail> orderDetailList;
    private double total;

    // Đặt phương thức để cập nhật total
    public void setTotal(double total) {
        this.total = total;
        notifyDataSetChanged();
    }
    public OrderHistoryAdapter(Context context, List<OrderDetail> orderDetailList) {
        this.context = context;
        this.orderDetailList = orderDetailList;
    }

    @NonNull
    @Override
    public OrderHistoryAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new OrderHistoryAdapter.OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderDetail orderDetail = orderDetailList.get(position);
//        holder.textTotal.setText("Tổng tiền: " + total + " đ");
        String productName = orderDetail.getProductId().getName();

        if (orderDetailList.size() > 2) {
            SpannableString spannableProductName = new SpannableString(productName + " Vv...");
            int start = productName.length();
            int end = spannableProductName.length();

            // Đặt màu và kích thước cho "Vv..."
            spannableProductName.setSpan(new ForegroundColorSpan(Color.GRAY), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableProductName.setSpan(new AbsoluteSizeSpan(15, true), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.textViewTen.setText(spannableProductName);
//            holder.textTotal.setText(String.format("Tổng tiền: %,.2f đ", total));
        } else {
            holder.textViewTen.setText(productName);
        }

//        holder.textViewProductPrice.setText(String.format("%,.2f đ", orderDetail.getPrice()));
//        holder.textViewProductQuantity.setText(String.valueOf(orderDetail.getQuantity()));
        Picasso.get().load(orderDetail.getProductId().getUrlImg()).into(holder.imageView);
    }



    @Override
    public int getItemCount() {
        return orderDetailList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewTen, textViewGia, textViewQuantity,textTotal;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewTen = itemView.findViewById(R.id.textViewTen);
            textViewGia = itemView.findViewById(R.id.textViewGia);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            textTotal = itemView.findViewById(R.id.textTotal);
        }
    }

    public void setOrderDetailList(List<OrderDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
        notifyDataSetChanged();
    }
}
