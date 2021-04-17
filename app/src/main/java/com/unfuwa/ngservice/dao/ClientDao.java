package com.unfuwa.ngservice.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.unfuwa.ngservice.extendedmodel.ClientUser;
import com.unfuwa.ngservice.model.Client;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface ClientDao {

    @Query("SELECT * FROM Clients WHERE email = :email")
    Single<ClientUser> getClientByEmail(String email);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Client client);

    @Update
    void update(Client client);

    @Delete
    void delete(Client client);
}
