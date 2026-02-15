package com.myapps.keithpottratz;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LocationDao {

    // READ all locations
    @Query("SELECT * FROM locations ORDER BY name ASC")
    List<Location> getAll();

    // READ single location by ID
    @Query("SELECT * FROM locations WHERE id = :id LIMIT 1")
    Location getById(long id);

    // READ location by name
    @Query("SELECT * FROM locations WHERE name = :name LIMIT 1")
    Location getByName(String name);

    // READ locations by building
    @Query("SELECT * FROM locations WHERE building = :building ORDER BY zone, aisle, shelf")
    List<Location> getByBuilding(String building);

    // SEARCH locations by name pattern
    @Query("SELECT * FROM locations WHERE name LIKE '%' || :search || '%' ORDER BY name ASC")
    List<Location> searchByName(String search);

    // COUNT items at a location
    @Query("SELECT COUNT(*) FROM inventory WHERE location_id = :locationId")
    int getItemCount(long locationId);

    // CREATE
    @Insert
    long insert(Location location);

    // UPDATE
    @Update
    int update(Location location);

    // DELETE
    @Delete
    int delete(Location location);

    // DELETE by ID
    @Query("DELETE FROM locations WHERE id = :id")
    int deleteById(long id);
}
