package com.unfuwa.ngservice.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.unfuwa.ngservice.extendedmodel.RegServiceExtended;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface RegServiceDao {

    @Query("SELECT * FROM RegService WHERE idEquipment = :id")
    Flowable<List<RegServiceExtended>> getRegServiceByEquipment(int id);
}
