package com.dimatechs.ecartAdmin.Model;

public class Category {

    private String catid,name;

    public Category() {
    }

    public Category(String catid, String name) {
        this.catid = catid;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatid() {
        return catid;
    }

    public void setCatid(String catid) {
        this.catid = catid;
    }
}
