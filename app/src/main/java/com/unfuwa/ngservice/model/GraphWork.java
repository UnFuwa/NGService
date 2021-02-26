package com.unfuwa.ngservice.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "GraphsWork",
        indices = {
                @Index(value = "LoginSpecialist"),
                @Index(value = "IdTaskWork")
        },
        foreignKeys = {
                @ForeignKey(
                        entity = Specialist.class,
                        parentColumns = "Login",
                        childColumns = "LoginSpecialist",
                        onDelete = ForeignKey.RESTRICT,
                        onUpdate = ForeignKey.RESTRICT),
                @ForeignKey(
                        entity = TaskWork.class,
                        parentColumns = "Id",
                        childColumns = "IdTaskWork",
                        onDelete = ForeignKey.RESTRICT,
                        onUpdate = ForeignKey.RESTRICT)
        }
)
public class GraphWork {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "Id")
    private int id;

    @NonNull
    @ColumnInfo(name = "LoginSpecialist")
    private String loginSpecialist;

    @NonNull
    @ColumnInfo(name = "IdTaskWork")
    private int idTaskWork;

    @NonNull
    @ColumnInfo(name = "Date")
    private String date;

    @NonNull
    @ColumnInfo(name = "FlagDay")
    private boolean flagDay;

    public GraphWork() { }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    @NonNull
    public String getLoginSpecialist() {
        return loginSpecialist;
    }

    public void setLoginSpecialist(@NonNull String loginSpecialist) {
        this.loginSpecialist = loginSpecialist;
    }

    @NonNull
    public int getIdTaskWork() {
        return idTaskWork;
    }

    public void setIdTaskWork(@NonNull int idTaskWork) {
        this.idTaskWork = idTaskWork;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    public boolean isFlagDay() {
        return flagDay;
    }

    public void setFlagDay(boolean flagDay) {
        this.flagDay = flagDay;
    }
}
