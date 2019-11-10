package com.dimatechs.ecartAdmin.Model;

public class Users
{
    private String bisiness,city,fname,lname,phone,password;
    private boolean active;

    public Users() {
    }

    public Users(String bisiness, String city, String fname, String lname, String phone, String password) {
        this.bisiness = bisiness;
        this.city = city;
        this.fname = fname;
        this.lname = lname;
        this.phone = phone;
        this.password = password;
        this.active=true;
    }

    public String getBisiness() {
        return bisiness;
    }

    public void setBisiness(String bisiness) {
        this.bisiness = bisiness;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
