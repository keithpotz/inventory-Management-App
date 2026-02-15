package com.myapps.keithpottratz;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "suppliers")
public class Supplier {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String name;

    @ColumnInfo(name = "contact_person")
    private String contactPerson;

    private String email;

    private String phone;

    private String address;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    // Constructor for Room (with ID)
    public Supplier(long id, @NonNull String name, String contactPerson, String email,
                    String phone, String address, long createdAt) {
        this.id = id;
        this.name = name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.createdAt = createdAt;
    }

    // Constructor for new suppliers (no ID)
    @Ignore
    public Supplier(@NonNull String name, String contactPerson, String email,
                    String phone, String address) {
        this.name = name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters
    public long getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
