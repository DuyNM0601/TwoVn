package com.example.twovn.model;

public class Product {
    private String _id;
    private String name;
    private String description;
    private String urlImg;
    private int price;
    private int quantity;

    public Product(String _id, String name, String description, String urlImg, int price, int quantity) {
        this._id = _id;
        this.name = name;
        this.description = description;
        this.urlImg = urlImg;
        this.price = price;
        this.quantity = quantity;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
