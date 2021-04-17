package com.unfuwa.ngservice.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.unfuwa.ngservice.extendedmodel.ClientUser;
import com.unfuwa.ngservice.extendedmodel.SpecialistUser;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface SpecialistDao {

    @Query("SELECT * FROM Specialists WHERE login = :login")
    Single<SpecialistUser> getSpecialistByUser(String login);
}
