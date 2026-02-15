package com.myapps.keithpottratz;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface InventoryDao {


    // BASIC CRUD OPERATIONS (Original)


    // READ all items
    @Query("SELECT * FROM inventory ORDER BY id DESC")
    List<InventoryItem> getAll();

    // READ single item by ID
    @Query("SELECT * FROM inventory WHERE id = :id LIMIT 1")
    InventoryItem getById(Long id);

    // CREATE
    @Insert
    long insert(InventoryItem item);

    // UPDATE
    @Update
    int update(InventoryItem item);

    // DELETE
    @Delete
    int delete(InventoryItem item);


    // RELATIONSHIP QUERIES (JOIN via @Transaction)


    // Get item with category
    @Transaction
    @Query("SELECT * FROM inventory WHERE id = :id")
    ItemWithCategory getItemWithCategory(long id);

    // Get all items with categories
    @Transaction
    @Query("SELECT * FROM inventory ORDER BY name ASC")
    List<ItemWithCategory> getAllWithCategories();

    // Get item with supplier
    @Transaction
    @Query("SELECT * FROM inventory WHERE id = :id")
    ItemWithSupplier getItemWithSupplier(long id);

    // Get all items with suppliers
    @Transaction
    @Query("SELECT * FROM inventory ORDER BY name ASC")
    List<ItemWithSupplier> getAllWithSuppliers();

    // Get item with location
    @Transaction
    @Query("SELECT * FROM inventory WHERE id = :id")
    ItemWithLocation getItemWithLocation(long id);

    // Get all items with locations
    @Transaction
    @Query("SELECT * FROM inventory ORDER BY name ASC")
    List<ItemWithLocation> getAllWithLocations();

    // Get item with ALL details (category, supplier, location)
    @Transaction
    @Query("SELECT * FROM inventory WHERE id = :id")
    ItemWithDetails getItemWithDetails(long id);

    // Get all items with all details
    @Transaction
    @Query("SELECT * FROM inventory ORDER BY name ASC")
    List<ItemWithDetails> getAllWithDetails();


    // FILTERING QUERIES


    // Get items by category
    @Query("SELECT * FROM inventory WHERE category_id = :categoryId ORDER BY name ASC")
    List<InventoryItem> getByCategory(long categoryId);

    // Get items by supplier
    @Query("SELECT * FROM inventory WHERE supplier_id = :supplierId ORDER BY name ASC")
    List<InventoryItem> getBySupplier(long supplierId);

    // Get items by location
    @Query("SELECT * FROM inventory WHERE location_id = :locationId ORDER BY name ASC")
    List<InventoryItem> getByLocation(long locationId);

    // Get LOW STOCK items (quantity below minimum)
    @Query("SELECT * FROM inventory WHERE quantity < min_stock_level ORDER BY (min_stock_level - quantity) DESC")
    List<InventoryItem> getLowStockItems();

    // Get OUT OF STOCK items (quantity = 0)
    @Query("SELECT * FROM inventory WHERE quantity = 0 ORDER BY name ASC")
    List<InventoryItem> getOutOfStockItems();

    // Get items in stock (quantity > 0)
    @Query("SELECT * FROM inventory WHERE quantity > 0 ORDER BY name ASC")
    List<InventoryItem> getInStockItems();

    // Get items by price range
    @Query("SELECT * FROM inventory WHERE price BETWEEN :minPrice AND :maxPrice ORDER BY price ASC")
    List<InventoryItem> getByPriceRange(double minPrice, double maxPrice);

    // Get items by quantity range
    @Query("SELECT * FROM inventory WHERE quantity BETWEEN :minQty AND :maxQty ORDER BY quantity ASC")
    List<InventoryItem> getByQuantityRange(int minQty, int maxQty);


    // SEARCH QUERIES


    // Search by name (partial match)
    @Query("SELECT * FROM inventory WHERE name LIKE '%' || :search || '%' ORDER BY name ASC")
    List<InventoryItem> searchByName(String search);

    // Search by SKU (exact match)
    @Query("SELECT * FROM inventory WHERE sku = :sku LIMIT 1")
    InventoryItem getBySku(String sku);

    // Search by name or description
    @Query("SELECT * FROM inventory WHERE name LIKE '%' || :search || '%' OR description LIKE '%' || :search || '%' ORDER BY name ASC")
    List<InventoryItem> searchByNameOrDescription(String search);


    // AGGREGATION QUERIES


    // Get total inventory value (sum of quantity * price)
    @Query("SELECT COALESCE(SUM(quantity * price), 0) FROM inventory")
    double getTotalInventoryValue();

    // Get total item count
    @Query("SELECT COUNT(*) FROM inventory")
    int getTotalItemCount();

    // Get total quantity of all items
    @Query("SELECT COALESCE(SUM(quantity), 0) FROM inventory")
    int getTotalQuantity();

    // Get average price
    @Query("SELECT COALESCE(AVG(price), 0) FROM inventory")
    double getAveragePrice();

    // Get count of low stock items
    @Query("SELECT COUNT(*) FROM inventory WHERE quantity < min_stock_level")
    int getLowStockCount();

    // Get count of out of stock items
    @Query("SELECT COUNT(*) FROM inventory WHERE quantity = 0")
    int getOutOfStockCount();


    // STATISTICS QUERIES (GROUP BY)


    // Get statistics by category
    @Query("SELECT c.id as categoryId, c.name as categoryName, " +
           "COUNT(i.id) as itemCount, " +
           "COALESCE(SUM(i.quantity), 0) as totalQuantity, " +
           "COALESCE(SUM(i.quantity * i.price), 0) as totalValue " +
           "FROM categories c " +
           "LEFT JOIN inventory i ON c.id = i.category_id " +
           "GROUP BY c.id, c.name " +
           "ORDER BY totalValue DESC")
    List<CategoryStats> getCategoryStatistics();

    // Get statistics by supplier
    @Query("SELECT s.id as supplierId, s.name as supplierName, " +
           "COUNT(i.id) as itemCount, " +
           "COALESCE(SUM(i.quantity), 0) as totalQuantity, " +
           "COALESCE(SUM(i.quantity * i.price), 0) as totalValue " +
           "FROM suppliers s " +
           "LEFT JOIN inventory i ON s.id = i.supplier_id " +
           "GROUP BY s.id, s.name " +
           "ORDER BY totalValue DESC")
    List<SupplierStats> getSupplierStatistics();


    // REORDER REPORT (Low Stock with Supplier Info)


    @Query("SELECT i.id as itemId, i.name as itemName, " +
           "i.quantity as quantity, i.min_stock_level as minStockLevel, " +
           "(i.min_stock_level - i.quantity) as deficit, " +
           "s.name as supplierName, s.email as supplierEmail, s.phone as supplierPhone " +
           "FROM inventory i " +
           "LEFT JOIN suppliers s ON i.supplier_id = s.id " +
           "WHERE i.quantity < i.min_stock_level " +
           "ORDER BY deficit DESC")
    List<LowStockItem> getReorderReport();


    // SORTING OPTIONS


    // Sort by name
    @Query("SELECT * FROM inventory ORDER BY name ASC")
    List<InventoryItem> getAllSortedByName();

    // Sort by quantity (lowest first)
    @Query("SELECT * FROM inventory ORDER BY quantity ASC")
    List<InventoryItem> getAllSortedByQuantityAsc();

    // Sort by quantity (highest first)
    @Query("SELECT * FROM inventory ORDER BY quantity DESC")
    List<InventoryItem> getAllSortedByQuantityDesc();

    // Sort by price (lowest first)
    @Query("SELECT * FROM inventory ORDER BY price ASC")
    List<InventoryItem> getAllSortedByPriceAsc();

    // Sort by price (highest first)
    @Query("SELECT * FROM inventory ORDER BY price DESC")
    List<InventoryItem> getAllSortedByPriceDesc();

    // Sort by value (quantity * price, highest first)
    @Query("SELECT * FROM inventory ORDER BY (quantity * price) DESC")
    List<InventoryItem> getAllSortedByValueDesc();

    // Sort by recently added
    @Query("SELECT * FROM inventory ORDER BY created_at DESC")
    List<InventoryItem> getAllSortedByNewest();

    // Sort by recently updated
    @Query("SELECT * FROM inventory ORDER BY updated_at DESC")
    List<InventoryItem> getAllSortedByRecentlyUpdated();


    // PAGINATION


    // Get page of items
    @Query("SELECT * FROM inventory ORDER BY id DESC LIMIT :limit OFFSET :offset")
    List<InventoryItem> getPage(int limit, int offset);

    // Get page sorted by name
    @Query("SELECT * FROM inventory ORDER BY name ASC LIMIT :limit OFFSET :offset")
    List<InventoryItem> getPageSortedByName(int limit, int offset);
}
