package com.myapps.keithpottratz;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String name;

    private String description;

    @ColumnInfo(name = "color_code")
    private String colorCode;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    // Constructor for Room (with ID)
    public Category(long id, @NonNull String name, String description, String colorCode, long createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.colorCode = colorCode;
        this.createdAt = createdAt;
    }

    // Constructor for new categories (no ID)
    @Ignore
    public Category(@NonNull String name, String description, String colorCode) {
        this.name = name;
        this.description = description;
        this.colorCode = colorCode;
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

    public String getDescription() {
        return description;
    }

    public String getColorCode() {
        return colorCode;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
