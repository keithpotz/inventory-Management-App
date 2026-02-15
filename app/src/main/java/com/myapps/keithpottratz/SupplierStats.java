package com.myapps.keithpottratz;

/**
 * POJO class for supplier statistics.
 * Used for aggregation query results.
 */
public class SupplierStats {
    public long supplierId;
    public String supplierName;
    public int itemCount;
    public int totalQuantity;
    public double totalValue;

    public SupplierStats(long supplierId, String supplierName, int itemCount,
                         int totalQuantity, double totalValue) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.itemCount = itemCount;
        this.totalQuantity = totalQuantity;
        this.totalValue = totalValue;
    }
}
