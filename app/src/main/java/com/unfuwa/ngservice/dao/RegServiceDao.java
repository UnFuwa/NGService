package com.unfuwa.ngservice.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.unfuwa.ngservice.extendedmodel.RegServiceExtended;
import com.unfuwa.ngservice.model.RegService;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface RegServiceDao {

    @Query("SELECT * FROM RegService WHERE idEquipment = :id")
    Flowable<List<RegServiceExtended>> getRegServiceByEquipment(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable addRegServiceByEquipment(RegService regService);

}
