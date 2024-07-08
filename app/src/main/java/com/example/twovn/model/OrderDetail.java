package com.example.twovn.model;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderDetail implements Parcelable {
    private String _id;
    private String orderId;
    private Product productId; // Sử dụng Product thay vì String
    private int quantity;
    private double price;
    private String status;
    private int __v;

    // Constructors
    public OrderDetail(String _id, String orderId, Product productId, int quantity, double price, String status, int __v) {
        this._id = _id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
        this.__v = __v;
    }

    protected OrderDetail(Parcel in) {
        _id = in.readString();
        orderId = in.readString();
        productId = in.readParcelable(Product.class.getClassLoader());
        quantity = in.readInt();
        price = in.readDouble();
        status = in.readString();
        __v = in.readInt();
    }

    public static final Creator<OrderDetail> CREATOR = new Creator<OrderDetail>() {
        @Override
        public OrderDetail createFromParcel(Parcel in) {
            return new OrderDetail(in);
        }

        @Override
        public OrderDetail[] newArray(int size) {
            return new OrderDetail[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(orderId);
        dest.writeParcelable(productId, flags);
        dest.writeInt(quantity);
        dest.writeDouble(price);
        dest.writeString(status);
        dest.writeInt(__v);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and Setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Product getProductId() {
        return productId;
    }

    public void setProductId(Product productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "_id='" + _id + '\'' +
                ", orderId='" + orderId + '\'' +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", status='" + status + '\'' +
                ", __v=" + __v +
                '}';
    }
}