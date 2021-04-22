package com.unfuwa.ngservice.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.unfuwa.ngservice.model.Notification;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface NotificationDao {

    @Query("SELECT * FROM Notifications WHERE LoginSpecialist = :login ORDER BY DateCreate DESC LIMIT 3")
    List<Notification> getLastNotifications(String login);
}
