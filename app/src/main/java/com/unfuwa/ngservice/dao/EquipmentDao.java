package com.unfuwa.ngservice.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.unfuwa.ngservice.model.Equipment;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface EquipmentDao {

    @Query("SELECT * FROM Equipments WHERE id = :id")
    Single<Equipment> getEquipmentById(int id);
}
