package com.myapps.keithpottratz;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InventoryHistoryDao {

    // READ all history for an item (newest first)
    @Query("SELECT * FROM inventory_history WHERE item_id = :itemId ORDER BY timestamp DESC")
    List<InventoryHistory> getHistoryForItem(long itemId);

    // READ all history by a user (newest first)
    @Query("SELECT * FROM inventory_history WHERE user_id = :userId ORDER BY timestamp DESC")
    List<InventoryHistory> getHistoryByUser(long userId);

    // READ recent history (all items, newest first, with limit)
    @Query("SELECT * FROM inventory_history ORDER BY timestamp DESC LIMIT :limit")
    List<InventoryHistory> getRecentHistory(int limit);

    // READ history by action type
    @Query("SELECT * FROM inventory_history WHERE action = :action ORDER BY timestamp DESC")
    List<InventoryHistory> getHistoryByAction(String action);

    // READ history within time range
    @Query("SELECT * FROM inventory_history WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    List<InventoryHistory> getHistoryInRange(long startTime, long endTime);

    // COUNT total history entries for an item
    @Query("SELECT COUNT(*) FROM inventory_history WHERE item_id = :itemId")
    int getHistoryCount(long itemId);

    // CREATE history entry
    @Insert
    long insert(InventoryHistory history);

    // DELETE history for an item (used when item is deleted - CASCADE handles this, but manual option)
    @Query("DELETE FROM inventory_history WHERE item_id = :itemId")
    int deleteHistoryForItem(long itemId);

    // DELETE old history (cleanup older than timestamp)
    @Query("DELETE FROM inventory_history WHERE timestamp < :beforeTime")
    int deleteOldHistory(long beforeTime);
}
