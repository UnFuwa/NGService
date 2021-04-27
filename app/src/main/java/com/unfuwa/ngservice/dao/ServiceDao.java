package com.unfuwa.ngservice.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.unfuwa.ngservice.model.Service;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface ServiceDao {

    @Query("SELECT * FROM Services")
    Flowable<List<Service>> getServices();
}
