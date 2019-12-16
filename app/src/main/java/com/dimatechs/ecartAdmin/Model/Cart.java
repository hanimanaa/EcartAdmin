package com.dimatechs.ecartAdmin.Model;

import com.dimatechs.ecartAdmin.utils.PDFCreationUtils;

import java.util.ArrayList;
import java.util.List;

public class Cart
{
    private String pid,date,time,name,price,quantity;

    public Cart() {
    }

    public Cart(String pid, String date, String time, String name, String price, String quantity) {
        this.pid = pid;
        this.date = date;
        this.time = time;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }


    public static List<Cart> createDummyPdfModel() {
        PDFCreationUtils.filePath.clear();
        PDFCreationUtils.progressCount = 1;

        boolean isFirstReceivedItem = false;
        List<Cart> pdfModels = new ArrayList<>();

        return pdfModels;
    }
}
