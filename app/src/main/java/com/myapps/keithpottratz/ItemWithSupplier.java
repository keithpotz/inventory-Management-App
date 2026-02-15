package com.myapps.keithpottratz;

import androidx.room.Embedded;
import androidx.room.Relation;

/**
 * POJO class for inventory item with its supplier.
 * Used for JOIN query results.
 */
public class ItemWithSupplier {
    @Embedded
    public InventoryItem item;

    @Relation(
        parentColumn = "supplier_id",
        entityColumn = "id"
    )
    public Supplier supplier;
}
