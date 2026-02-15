package com.myapps.keithpottratz;

import androidx.room.Embedded;
import androidx.room.Relation;

/**
 * POJO class for inventory item with its category.
 * Used for JOIN query results.
 */
public class ItemWithCategory {
    @Embedded
    public InventoryItem item;

    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    public Category category;
}
