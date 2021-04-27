package com.unfuwa.ngservice.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.unfuwa.ngservice.model.TypeEquipment;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface TypeEquipmentDao {

    @Query("SELECT * FROM TypesEquipment")
    Flowable<List<TypeEquipment>> getTypesEquipment();
}
