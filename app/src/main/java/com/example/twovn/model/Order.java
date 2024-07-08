package com.example.twovn.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Order implements Parcelable {
    private String _id;
    private String accountId;
    private String shopId;
    private int quantity;
    private double totalAmount;
    private String address;
    private String status;
    private String shipmentDetailId;
    private String dateTime; // Sử dụng String cho dateTime

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    // Constructors
    public Order(String _id, String accountId, String shopId, int quantity, double totalAmount, String address, String status, String shipmentDetailId, String dateTime) {
        this._id = _id;
        this.accountId = accountId;
        this.shopId = shopId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.address = address;
        this.status = status;
        this.shipmentDetailId = shipmentDetailId;
        this.dateTime = dateTime; // Khởi tạo dateTime bằng chuỗi ngày giờ
    }

    protected Order(Parcel in) {
        _id = in.readString();
        accountId = in.readString();
        shopId = in.readString();
        quantity = in.readInt();
        totalAmount = in.readDouble();
        address = in.readString();
        status = in.readString();
        shipmentDetailId = in.readString();
        dateTime = in.readString(); // Đọc dateTime từ Parcel
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(accountId);
        dest.writeString(shopId);
        dest.writeInt(quantity);
        dest.writeDouble(totalAmount);
        dest.writeString(address);
        dest.writeString(status);
        dest.writeString(shipmentDetailId);
        dest.writeString(dateTime); // Ghi dateTime thành chuỗi vào Parcel
    }

    // Getters and Setters
    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getShipmentDetailId() {
        return shipmentDetailId;
    }

    public void setShipmentDetailId(String shipmentDetailId) {
        this.shipmentDetailId = shipmentDetailId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Order{" +
                "_id='" + _id + '\'' +
                ", accountId='" + accountId + '\'' +
                ", shopId='" + shopId + '\'' +
                ", quantity=" + quantity +
                ", totalAmount=" + totalAmount +
                ", address='" + address + '\'' +
                ", status='" + status + '\'' +
                ", shipmentDetailId='" + shipmentDetailId + '\'' +
                ", dateTime='" + dateTime + '\'' + // In ra chuỗi dateTime
                '}';
    }

    // Phương thức parseDate để chuyển đổi chuỗi ngày giờ thành Calendar
    private Calendar parseDate(String dateString) {
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = dateFormat.parse(dateString);
            if (date != null) {
                calendar.setTime(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }
}
