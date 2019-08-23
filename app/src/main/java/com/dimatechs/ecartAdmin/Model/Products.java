package com.dimatechs.ecartAdmin.Model;

public class Products
{
    private String name;
    private String description;
    private String price;
    private String date;
    private String pid;
    private String time;
    private String category;
    private String image;


    public Products() {

    }

    public Products(String name, String description, String price, String date, String pid, String time, String category,String image) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.date = date;
        this.pid = pid;
        this.time = time;
        this.category = category;
        this.image=image;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    public String getPid() {
        return pid;
    }

    public String getTime() {
        return time;
    }

    public String getCategory() {
        return category;
    }

    public String getImage() {
        return image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
