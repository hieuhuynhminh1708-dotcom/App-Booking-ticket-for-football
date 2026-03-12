package com.example.client.models;

public class User {
    private String uid;
    private String fullName;
    private String email;
    private String nik;
    private String phone;
    private String birthDate;
    private String gender;
    private String address;
    private String photoURL;
    private String role;

    // Constructor trống cho Firebase
    public User() {}

    // Constructor đầy đủ
    public User(String uid, String fullName, String email, String nik, String phone,
                String birthDate, String gender, String address, String photoURL, String role) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.nik = nik;
        this.phone = phone;
        this.birthDate = birthDate;
        this.gender = gender;
        this.address = address;
        this.photoURL = photoURL;
        this.role = role;
    }

    // GETTERS
    public String getUid() {
        return uid;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getNik() {
        return nik;
    }

    public String getPhone() {
        return phone;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getGender() {
        return gender;
    }

    public String getAddress() {
        return address;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public String getRole() {
        return role;
    }

    // SETTERS
    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
