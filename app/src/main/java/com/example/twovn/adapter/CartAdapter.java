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
    private boolean showButtons; // Biến để quyết định xem có hiển thị ImageButton cộng và trừ hay không

    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }

    public CartAdapter(Context context, List<Product> cartProductList, OnQuantityChangeListener quantityChangeListener, boolean showButtons) {
        this.context = context;
        this.cartProductList = cartProductList;
        this.quantityChangeListener = quantityChangeListener;
        this.showButtons = showButtons; // Khởi tạo biến showButtons
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
        holder.textViewGia.setText(String.format("%,d đ", product.getPrice()));
        holder.textViewQuantity.setText(String.valueOf(product.getQuantity()));
        Picasso.get().load(product.getUrlImg()).into(holder.imageView);

        // Hiển thị hoặc ẩn TextView số lượng label
        if (!showButtons) {
            holder.textViewQuantityLabel.setVisibility(View.VISIBLE);
        } else {
            holder.textViewQuantityLabel.setVisibility(View.GONE);
        }

        // Kiểm tra và điều chỉnh việc hiển thị ImageButton cộng và trừ
        if (showButtons) {
            holder.decreaseQuantityButton.setVisibility(View.VISIBLE);
            holder.increaseQuantityButton.setVisibility(View.VISIBLE);

            holder.decreaseQuantityButton.setOnClickListener(v -> {
                if (product.getQuantity() > 1) {
                    product.setQuantity(product.getQuantity() - 1);
                    holder.textViewQuantity.setText(String.valueOf(product.getQuantity()));
                    quantityChangeListener.onQuantityChanged();
                } else {
                    cartProductList.remove(position); // Xóa sản phẩm khỏi danh sách nếu số lượng là 0
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, cartProductList.size());
                    quantityChangeListener.onQuantityChanged();
                }
            });

            holder.increaseQuantityButton.setOnClickListener(v -> {
                product.setQuantity(product.getQuantity() + 1);
                holder.textViewQuantity.setText(String.valueOf(product.getQuantity()));
                quantityChangeListener.onQuantityChanged();
            });
        } else {
            holder.decreaseQuantityButton.setVisibility(View.GONE);
            holder.increaseQuantityButton.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return cartProductList.size();
    }

    public void setCartProductList(List<Product> cartProductList) {
        this.cartProductList = cartProductList;
        notifyDataSetChanged();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textViewTen, textViewGia, textViewQuantity, textViewQuantityLabel;
        ImageButton decreaseQuantityButton, increaseQuantityButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewTen = itemView.findViewById(R.id.textViewTen);
            textViewGia = itemView.findViewById(R.id.textViewGia);
            textViewQuantityLabel = itemView.findViewById(R.id.textViewQuantityLabel);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            decreaseQuantityButton = itemView.findViewById(R.id.decreaseQuantityButton);
            increaseQuantityButton = itemView.findViewById(R.id.increaseQuantityButton);
        }
    }
}
