package com.myapps.keithpottratz;

/**
 * POJO class for low stock items with supplier contact info.
 * Used for reorder report queries.
 */
public class LowStockItem {
    public long itemId;
    public String itemName;
    public int quantity;
    public int minStockLevel;
    public int deficit;  // How many units below minimum
    public String supplierName;
    public String supplierEmail;
    public String supplierPhone;

    public LowStockItem(long itemId, String itemName, int quantity, int minStockLevel,
                        int deficit, String supplierName, String supplierEmail, String supplierPhone) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.minStockLevel = minStockLevel;
        this.deficit = deficit;
        this.supplierName = supplierName;
        this.supplierEmail = supplierEmail;
        this.supplierPhone = supplierPhone;
    }
}
