package com.dimatechs.ecartAdmin.Model;

public class Admin
{
    private String fname,lname,phone,password;

    public Admin(String fname, String lname, String phone, String password) {
        this.fname = fname;
        this.lname = lname;
        this.phone = phone;
        this.password = password;
    }

    public Admin() {
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
