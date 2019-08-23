package com.dimatechs.ecart.Model;

public class AdminOrders
{
    private String name,phone,date,time,state,totalAmount,address,orderNum;

    public AdminOrders() {
    }

    public AdminOrders(String name, String phone, String date, String time, String state, String totalAmount, String address, String orderNum) {
        this.name = name;
        this.phone = phone;
        this.date = date;
        this.time = time;
        this.state = state;
        this.totalAmount = totalAmount;
        this.address = address;
        this.orderNum = orderNum;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
