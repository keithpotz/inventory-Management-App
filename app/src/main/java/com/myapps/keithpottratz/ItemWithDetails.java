package com.myapps.keithpottratz;

import androidx.room.Embedded;
import androidx.room.Relation;

/**
 * POJO class for inventory item with ALL related entities.
 * Used for complete item details with category, supplier, and location.
 */
public class ItemWithDetails {
    @Embedded
    public InventoryItem item;

    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    public Category category;

    @Relation(
        parentColumn = "supplier_id",
        entityColumn = "id"
    )
    public Supplier supplier;

    @Relation(
        parentColumn = "location_id",
        entityColumn = "id"
    )
    public Location location;
}
