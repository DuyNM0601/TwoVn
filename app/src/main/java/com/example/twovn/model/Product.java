package com.example.twovn.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private String _id;
    private String name;
    private String description;
    private String imageUrl;
    private int price;
    private int quantity;

    public Product(String _id, String name, String description, String imageUrl, int price, int quantity) {
        this._id = _id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
    }

    public Product(String _id, String name, String imageUrl, int price) {
        this._id = _id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = 1;
    }

    protected Product(Parcel in) {
        _id = in.readString();
        name = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        price = in.readInt();
        quantity = in.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeInt(price);
        dest.writeInt(quantity);
    }

    @Override
    public int describeContents() {
        return 0;
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
        return imageUrl;
    }

    public void setUrlImg(String imageUrl) {
        this.imageUrl = imageUrl;
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
