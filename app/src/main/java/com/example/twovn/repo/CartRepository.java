package com.example.twovn.repo;

import com.example.twovn.model.Product;

import java.util.ArrayList;
import java.util.List;

public class CartRepository {

    private static CartRepository instance;
    private List<Product> cartProducts = new ArrayList<>();

    private CartRepository() {
    }

    public static synchronized CartRepository getInstance() {
        if (instance == null) {
            instance = new CartRepository();
        }
        return instance;
    }

    public void addProductToCart(Product product) {
        cartProducts.add(product);
    }

    public List<Product> getCartProducts() {
        return new ArrayList<>(cartProducts);
    }

    public void clearCart() {
        cartProducts.clear();
    }
}
