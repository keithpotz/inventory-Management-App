package com.myapps.keithpottratz;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "inventory",
    foreignKeys = {
        @ForeignKey(
            entity = Category.class,
            parentColumns = "id",
            childColumns = "category_id",
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = Supplier.class,
            parentColumns = "id",
            childColumns = "supplier_id",
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = Location.class,
            parentColumns = "id",
            childColumns = "location_id",
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index(value = "category_id"),
        @Index(value = "supplier_id"),
        @Index(value = "location_id"),
        @Index(value = "sku")
    }
)
public class InventoryItem {
    @PrimaryKey(autoGenerate = true)
    private long id;

    // Original fields
    @NonNull
    private String name;

    private String description;

    private int quantity;

    // NEW: Foreign key fields
    @ColumnInfo(name = "category_id")
    private Long categoryId;

    @ColumnInfo(name = "supplier_id")
    private Long supplierId;

    @ColumnInfo(name = "location_id")
    private Long locationId;

    // NEW: Additional fields
    private double price;

    private String sku;

    @ColumnInfo(name = "min_stock_level")
    private int minStockLevel;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    // Primary constructor for Room (all fields)
    public InventoryItem(long id, @NonNull String name, String description, int quantity,
                         Long categoryId, Long supplierId, Long locationId,
                         double price, String sku, int minStockLevel,
                         long createdAt, long updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.locationId = locationId;
        this.price = price;
        this.sku = sku;
        this.minStockLevel = minStockLevel;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Convenience constructor for new items (basic fields only)
    @Ignore
    public InventoryItem(@NonNull String name, String description, int quantity) {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.minStockLevel = 10;  // Default minimum stock level
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Full convenience constructor for new items (all fields except id)
    @Ignore
    public InventoryItem(@NonNull String name, String description, int quantity,
                         Long categoryId, Long supplierId, Long locationId,
                         double price, String sku, int minStockLevel) {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.locationId = locationId;
        this.price = price;
        this.sku = sku;
        this.minStockLevel = minStockLevel;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // ==================== GETTERS ====================

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

    public int getQuantity() {
        return quantity;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public double getPrice() {
        return price;
    }

    public String getSku() {
        return sku;
    }

    public int getMinStockLevel() {
        return minStockLevel;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    // ==================== SETTERS ====================

    public void setId(long id) {
        this.id = id;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setPrice(double price) {
        this.price = price;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setSku(String sku) {
        this.sku = sku;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setMinStockLevel(int minStockLevel) {
        this.minStockLevel = minStockLevel;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Calculate total value of this item (quantity * price)
     */
    public double getTotalValue() {
        return quantity * price;
    }

    /**
     * Check if item is below minimum stock level
     */
    public boolean isLowStock() {
        return quantity < minStockLevel;
    }

    /**
     * Get stock status as string
     */
    public String getStockStatus() {
        if (quantity == 0) {
            return "OUT_OF_STOCK";
        } else if (quantity < minStockLevel) {
            return "LOW_STOCK";
        } else {
            return "IN_STOCK";
        }
    }
}
