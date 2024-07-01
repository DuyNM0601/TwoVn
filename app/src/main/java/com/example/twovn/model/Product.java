package com.example.twovn.model;

public class Product {
    private long id;
    private String name;
    private String description;
    private String urlImg;
    private int price;

    public Product(String name, String description, String urlImg, int price) {
        this.name = name;
        this.description = description;
        this.urlImg = urlImg;
        this.price = price;
    }

    public Product(long id, String name, String description, String urlImg, int price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.urlImg = urlImg;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getPrice() { // Thay đổi từ double thành int
        return price;
    }

    public void setPrice(int price) { // Thay đổi từ double thành int
        this.price = price;
    }
}
