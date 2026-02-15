package com.myapps.keithpottratz;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SupplierDao {

    // READ all suppliers
    @Query("SELECT * FROM suppliers ORDER BY name ASC")
    List<Supplier> getAll();

    // READ single supplier by ID
    @Query("SELECT * FROM suppliers WHERE id = :id LIMIT 1")
    Supplier getById(long id);

    // READ supplier by name
    @Query("SELECT * FROM suppliers WHERE name = :name LIMIT 1")
    Supplier getByName(String name);

    // SEARCH suppliers by name pattern
    @Query("SELECT * FROM suppliers WHERE name LIKE '%' || :search || '%' ORDER BY name ASC")
    List<Supplier> searchByName(String search);

    // COUNT items from a supplier
    @Query("SELECT COUNT(*) FROM inventory WHERE supplier_id = :supplierId")
    int getItemCount(long supplierId);

    // CREATE
    @Insert
    long insert(Supplier supplier);

    // UPDATE
    @Update
    int update(Supplier supplier);

    // DELETE
    @Delete
    int delete(Supplier supplier);

    // DELETE by ID
    @Query("DELETE FROM suppliers WHERE id = :id")
    int deleteById(long id);
}
