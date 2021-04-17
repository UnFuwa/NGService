package com.unfuwa.ngservice.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.unfuwa.ngservice.extendedmodel.FilialCity;

import java.util.List;
import java.util.Observable;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface FilialCityDao {

    @Query("SELECT * FROM Filials")
    Flowable<List<FilialCity>> getListFilials();
}
