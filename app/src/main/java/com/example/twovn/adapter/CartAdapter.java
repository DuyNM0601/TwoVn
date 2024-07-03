package com.example.twovn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.twovn.R;
import com.example.twovn.model.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<Product> cartProductList;
    private OnQuantityChangeListener quantityChangeListener;

    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }

    public CartAdapter(Context context, List<Product> cartProductList, OnQuantityChangeListener quantityChangeListener) {
        this.context = context;
        this.cartProductList = cartProductList;
        this.quantityChangeListener = quantityChangeListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = cartProductList.get(position);

        holder.textViewTen.setText(product.getName());
        holder.textViewGia.setText(String.format("%,dđ", (int) product.getPrice()));
        holder.textViewQuantity.setText(String.valueOf(product.getQuantity()));
        Picasso.get().load(product.getUrlImg()).into(holder.imageView);

        holder.decreaseQuantityButton.setOnClickListener(v -> {
            if (product.getQuantity() > 1) {
                product.setQuantity(product.getQuantity() - 1);
                holder.textViewQuantity.setText(String.valueOf(product.getQuantity()));
                quantityChangeListener.onQuantityChanged();
            }
        });

        holder.increaseQuantityButton.setOnClickListener(v -> {
            product.setQuantity(product.getQuantity() + 1);
            holder.textViewQuantity.setText(String.valueOf(product.getQuantity()));
            quantityChangeListener.onQuantityChanged();
        });
    }

    @Override
    public int getItemCount() {
        return cartProductList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textViewTen, textViewGia, textViewQuantity;
        ImageButton decreaseQuantityButton, increaseQuantityButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewTen = itemView.findViewById(R.id.textViewTen);
            textViewGia = itemView.findViewById(R.id.textViewGia);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            decreaseQuantityButton = itemView.findViewById(R.id.decreaseQuantityButton);
            increaseQuantityButton = itemView.findViewById(R.id.increaseQuantityButton);
        }
    }
}
