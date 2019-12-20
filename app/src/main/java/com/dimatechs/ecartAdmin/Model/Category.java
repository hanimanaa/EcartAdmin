package com.dimatechs.ecartAdmin.Model;

public class Category {

    private String catID,catName;

    public Category() {
    }

    public Category(String catid, String name) {
        this.catID = catid;
        this.catName = name;
    }

    public String getCatID() {
        return catID;
    }

    public void setCatID(String catID) {
        this.catID = catID;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }
}
