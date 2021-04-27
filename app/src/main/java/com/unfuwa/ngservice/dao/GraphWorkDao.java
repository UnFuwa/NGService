package com.unfuwa.ngservice.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.unfuwa.ngservice.extendedmodel.GraphTaskWork;

import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface GraphWorkDao {

    @Query("SELECT " +
            "g.Id, " +
            "g.LoginSpecialist, " +
            "g.IdTaskWork, " +
            "g.Date, " +
            "g.FlagDay, " +
            "t.Id, " +
            "t.Title, " +
            "t.ShortDescription, " +
            "t.FullDescription, " +
            "t.DateFrom, " +
            "t.DateTo, " +
            "t.Difficult, " +
            "t.FlagComplete " +
            "FROM GraphsWork g " +
            "LEFT JOIN TasksWork t ON g.IdTaskWork = t.Id " +
            "WHERE g.LoginSpecialist = :login " +
            "AND g.Date = :date " +
            "AND g.FlagDay = 1 " +
            "AND t.FlagComplete = 0 " +
            "AND g.Date BETWEEN t.DateFrom AND t.DateTo")
    Flowable<List<GraphTaskWork>> getTasksWorkTodayBySpecialist(String login, String date);
}
