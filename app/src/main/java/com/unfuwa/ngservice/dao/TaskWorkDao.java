package com.unfuwa.ngservice.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Update;

import com.unfuwa.ngservice.model.TaskWork;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface TaskWorkDao {

    @Query("SELECT * FROM TasksWork " +
            "WHERE FlagComplete = 0 " +
            "AND date(substr(:dateNow, 7) || '-' || substr(:dateNow, 4, 2) || '-' || substr(:dateNow, 1, 2)) " +
            "BETWEEN date(substr(DateFrom, 7) || '-' || substr(DateFrom, 4, 2) || '-' || substr(DateFrom, 1, 2)) " +
            "AND date(substr(DateTo, 7) || '-' || substr(DateTo, 4, 2) || '-' || substr(DateTo, 1, 2))")
    Flowable<List<TaskWork>> getListTasksWork(String dateNow);

    @Update
    Completable updateStatus(TaskWork taskWork);

    @Update
    Completable updateStatusList(ArrayList<TaskWork> listTaskWork);
}
