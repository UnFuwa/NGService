package com.unfuwa.ngservice.util.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.unfuwa.ngservice.dao.ClientDao;
import com.unfuwa.ngservice.dao.EquipmentDao;
import com.unfuwa.ngservice.dao.FilialCityDao;
import com.unfuwa.ngservice.dao.GraphWorkDao;
import com.unfuwa.ngservice.dao.KnowledgeBaseDao;
import com.unfuwa.ngservice.dao.NotificationDao;
import com.unfuwa.ngservice.dao.RegServiceDao;
import com.unfuwa.ngservice.dao.RequestDao;
import com.unfuwa.ngservice.dao.ServiceDao;
import com.unfuwa.ngservice.dao.SpecialistDao;
import com.unfuwa.ngservice.dao.SubcategoryDao;
import com.unfuwa.ngservice.dao.TaskWorkDao;
import com.unfuwa.ngservice.dao.TypeEquipmentDao;
import com.unfuwa.ngservice.dao.UserClientDao;
import com.unfuwa.ngservice.dao.UserDao;
import com.unfuwa.ngservice.model.AccessRight;
import com.unfuwa.ngservice.model.Category;
import com.unfuwa.ngservice.model.City;
import com.unfuwa.ngservice.model.Client;
import com.unfuwa.ngservice.model.Equipment;
import com.unfuwa.ngservice.model.Filial;
import com.unfuwa.ngservice.model.GraphWork;
import com.unfuwa.ngservice.model.KnowledgeBase;
import com.unfuwa.ngservice.model.Notification;
import com.unfuwa.ngservice.model.Position;
import com.unfuwa.ngservice.model.RegService;
import com.unfuwa.ngservice.model.Region;
import com.unfuwa.ngservice.model.Request;
import com.unfuwa.ngservice.model.Service;
import com.unfuwa.ngservice.model.Specialist;
import com.unfuwa.ngservice.model.Subcategory;
import com.unfuwa.ngservice.model.TaskWork;
import com.unfuwa.ngservice.model.TypeEquipment;
import com.unfuwa.ngservice.model.TypeNotification;
import com.unfuwa.ngservice.model.User;

@Database(entities = {
        AccessRight.class,
        User.class,
        Client.class,
        Request.class,
        Category.class,
        City.class,
        Equipment.class,
        Filial.class,
        GraphWork.class,
        KnowledgeBase.class,
        Notification.class,
        Position.class,
        Region.class,
        RegService.class,
        Service.class,
        Specialist.class,
        Subcategory.class,
        TaskWork.class,
        TypeEquipment.class,
        TypeNotification.class,

    },
        version = 8,
        exportSchema = false
)
@TypeConverters({DateConverter.class})
public abstract class DatabaseApi extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract RequestDao requestDao();
    public abstract UserClientDao userClientDao();
    public abstract FilialCityDao filialCityDao();
    public abstract EquipmentDao equipmentDao();
    public abstract RegServiceDao regServiceDao();
    public abstract ClientDao clientDao();
    public abstract SpecialistDao specialistDao();
    public abstract NotificationDao notificationDao();
    public abstract GraphWorkDao graphWorkDao();
    public abstract TaskWorkDao taskWorkDao();
    public abstract ServiceDao serviceDao();
    public abstract TypeEquipmentDao typeEquipmentDao();
    public abstract SubcategoryDao subcategoryDao();
    public abstract KnowledgeBaseDao knowledgeBaseDao();

    private static DatabaseApi INSTANCE;

    public static DatabaseApi getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (DatabaseApi.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DatabaseApi.class, "ngservice.db")
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    public void destroy() {
        synchronized (DatabaseApi.class) {
            INSTANCE = null;
        }
    }
}
