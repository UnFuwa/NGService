package com.unfuwa.ngservice.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.unfuwa.ngservice.extendedmodel.EquipmentClient;
import com.unfuwa.ngservice.model.Equipment;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface EquipmentDao {

    @Query("SELECT * FROM Equipments WHERE id = :id")
    Single<Equipment> getEquipmentById(int id);

    @Query("SELECT * FROM Equipments WHERE StatusRepair = 0")
    Flowable<List<EquipmentClient>> getListEquipment();
}
