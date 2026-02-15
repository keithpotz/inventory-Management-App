package com.myapps.keithpottratz;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoryDao {

    // READ all categories
    @Query("SELECT * FROM categories ORDER BY name ASC")
    List<Category> getAll();

    // READ single category by ID
    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    Category getById(long id);

    // READ category by name
    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    Category getByName(String name);

    // COUNT items in a category
    @Query("SELECT COUNT(*) FROM inventory WHERE category_id = :categoryId")
    int getItemCount(long categoryId);

    // CREATE
    @Insert
    long insert(Category category);

    // UPDATE
    @Update
    int update(Category category);

    // DELETE
    @Delete
    int delete(Category category);

    // DELETE by ID
    @Query("DELETE FROM categories WHERE id = :id")
    int deleteById(long id);
}
