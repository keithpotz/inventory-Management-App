package com.myapps.keithpottratz;

/**
 * POJO class for category statistics.
 * Used for aggregation query results.
 */
public class CategoryStats {
    public long categoryId;
    public String categoryName;
    public int itemCount;
    public int totalQuantity;
    public double totalValue;

    public CategoryStats(long categoryId, String categoryName, int itemCount,
                         int totalQuantity, double totalValue) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.itemCount = itemCount;
        this.totalQuantity = totalQuantity;
        this.totalValue = totalValue;
    }
}
