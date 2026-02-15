package com.myapps.keithpottratz;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "locations")
public class Location {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String name;

    private String building;

    private String zone;

    private String aisle;

    private String shelf;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    // Constructor for Room (with ID)
    public Location(long id, @NonNull String name, String building, String zone,
                    String aisle, String shelf, long createdAt) {
        this.id = id;
        this.name = name;
        this.building = building;
        this.zone = zone;
        this.aisle = aisle;
        this.shelf = shelf;
        this.createdAt = createdAt;
    }

    // Constructor for new locations (no ID)
    @Ignore
    public Location(@NonNull String name, String building, String zone,
                    String aisle, String shelf) {
        this.name = name;
        this.building = building;
        this.zone = zone;
        this.aisle = aisle;
        this.shelf = shelf;
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

    public String getBuilding() {
        return building;
    }

    public String getZone() {
        return zone;
    }

    public String getAisle() {
        return aisle;
    }

    public String getShelf() {
        return shelf;
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

    public void setBuilding(String building) {
        this.building = building;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public void setAisle(String aisle) {
        this.aisle = aisle;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
