package com.unfuwa.ngservice.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.unfuwa.ngservice.extendedmodel.EquipmentClient;
import com.unfuwa.ngservice.model.Equipment;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface EquipmentDao {

    @Query("SELECT * FROM Equipments WHERE id = :id")
    Single<Equipment> getEquipmentById(int id);

    @Query("SELECT * FROM Equipments WHERE StatusRepair = 0")
    Flowable<List<EquipmentClient>> getListEquipment();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Equipment equipment);

    @Update
    Completable update(Equipment equipment);
}
