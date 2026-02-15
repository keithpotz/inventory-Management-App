package com.myapps.keithpottratz;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "inventory_history",
    foreignKeys = {
        @ForeignKey(
            entity = InventoryItem.class,
            parentColumns = "id",
            childColumns = "item_id",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = User.class,
            parentColumns = "id",
            childColumns = "user_id",
            onDelete = ForeignKey.SET_NULL
        )
    },
    indices = {
        @Index(value = "item_id"),
        @Index(value = "user_id")
    }
)
public class InventoryHistory {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "item_id")
    private long itemId;

    @ColumnInfo(name = "user_id")
    private Long userId;

    @NonNull
    private String action;  // "CREATED", "UPDATED", "DELETED"

    @ColumnInfo(name = "field_changed")
    private String fieldChanged;

    @ColumnInfo(name = "old_value")
    private String oldValue;

    @ColumnInfo(name = "new_value")
    private String newValue;

    private long timestamp;

    // Constructor for Room (with ID)
    public InventoryHistory(long id, long itemId, Long userId, @NonNull String action,
                            String fieldChanged, String oldValue, String newValue, long timestamp) {
        this.id = id;
        this.itemId = itemId;
        this.userId = userId;
        this.action = action;
        this.fieldChanged = fieldChanged;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.timestamp = timestamp;
    }

    // Constructor for new history entries (no ID)
    @Ignore
    public InventoryHistory(long itemId, Long userId, @NonNull String action,
                            String fieldChanged, String oldValue, String newValue) {
        this.itemId = itemId;
        this.userId = userId;
        this.action = action;
        this.fieldChanged = fieldChanged;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public long getId() {
        return id;
    }

    public long getItemId() {
        return itemId;
    }

    public Long getUserId() {
        return userId;
    }

    @NonNull
    public String getAction() {
        return action;
    }

    public String getFieldChanged() {
        return fieldChanged;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setAction(@NonNull String action) {
        this.action = action;
    }

    public void setFieldChanged(String fieldChanged) {
        this.fieldChanged = fieldChanged;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
