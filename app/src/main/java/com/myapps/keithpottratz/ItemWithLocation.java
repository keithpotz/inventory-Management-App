package com.myapps.keithpottratz;

import androidx.room.Embedded;
import androidx.room.Relation;

/**
 * POJO class for inventory item with its location.
 * Used for JOIN query results.
 */
public class ItemWithLocation {
    @Embedded
    public InventoryItem item;

    @Relation(
        parentColumn = "location_id",
        entityColumn = "id"
    )
    public Location location;
}
