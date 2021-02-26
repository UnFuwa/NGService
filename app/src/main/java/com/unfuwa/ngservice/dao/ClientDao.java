package com.unfuwa.ngservice.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import com.unfuwa.ngservice.model.Client;

@Dao
public interface ClientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Client client);

    @Update
    void update(Client client);

    @Delete
    void delete(Client client);
}
