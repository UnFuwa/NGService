package com.unfuwa.ngservice.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.unfuwa.ngservice.model.Client;
import com.unfuwa.ngservice.model.User;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public abstract class UserClientDao {

    @Query("SELECT Email, FName, IName, OName, Telephone FROM Clients, Users WHERE Users.token = :token")
    public abstract Single<Client> getEmailClientByToken(String token);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Completable insertUser(User user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Completable insertClient(Client client);

    @Transaction
    public void addClient(User user, Client client) {
        insertUser(user);
        insertClient(client);
    }
}
