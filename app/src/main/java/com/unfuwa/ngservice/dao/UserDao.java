package com.unfuwa.ngservice.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.unfuwa.ngservice.model.User;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface UserDao {

    @Query("SELECT * FROM Users WHERE login = :login")
    Single<User> getUserByLogin(String login);

    @Query("SELECT * FROM Users WHERE token = :token")
    Single<User> getUserByToken(String token);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);
}
