package com.example.twovn.model;

public class Cart {
    private String accountId;
    private String productId; // Assuming productId is sufficient for cart item creation
    private String name;
    private int price;
    private String imageUrl;
    private int quantity;

    public Cart(String productId) {
        this.productId = productId;
    }

    public Cart(String accountId, String productId) {
        this.accountId = accountId;
        this.productId = productId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
