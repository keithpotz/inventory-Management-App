package com.myapps.keithpottratz;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "users",
    indices = {
        @Index(value = "username", unique = true)  // Enforce unique usernames
    }
)
public class User {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String username;

    @NonNull
    @ColumnInfo(name = "password_hash")
    public String passwordHash;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    // Primary constructor for Room (all fields)
    public User(long id, @NonNull String username, @NonNull String passwordHash, long createdAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    // Convenience constructor for new users (no ID, auto timestamp)
    @Ignore
    public User(@NonNull String username, @NonNull String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters
    public long getId() {
        return id;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    @NonNull
    public String getPasswordHash() {
        return passwordHash;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    public void setPasswordHash(@NonNull String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
