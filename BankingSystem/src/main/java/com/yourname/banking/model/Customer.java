package com.yourname.banking.model;

import java.util.UUID;

public class Customer {
    private String id;
    private String name;
    private String address;
    private String email;
    private String phone;
    private String password;

    public Customer(String name, String phone, String address, String email, String password) {
        this.id = UUID.randomUUID().toString(); // Generate a unique ID
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.password = password;
    }

    // Constructor for cases where ID is already known (e.g., updating an existing
    // customer)
    public Customer(String id, String name, String phone, String address, String email, String password) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    // public void setId(String id) {
    //     this.id = id;
    // }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}