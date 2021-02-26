package com.unfuwa.ngservice.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "TasksWork",
        foreignKeys = @ForeignKey(
                entity = Specialist.class,
                parentColumns = "Login",
                childColumns = "LoginSpecialist",
                onDelete = ForeignKey.RESTRICT,
                onUpdate = ForeignKey.RESTRICT)
)
public class TaskWork {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "Id")
    private int id;

    @NonNull
    @ColumnInfo(name = "LoginSpecialist")
    private String loginSpecialist;

    @NonNull
    @ColumnInfo(name = "Title")
    private String title;

    @NonNull
    @ColumnInfo(name = "ShortDescription")
    private String shortDescription;

    @NonNull
    @ColumnInfo(name = "FullDescription")
    private String fullDescription;

    @NonNull
    @ColumnInfo(name = "DateFrom")
    private String dateFrom;

    @NonNull
    @ColumnInfo(name = "DateTo")
    private String dateTo;

    @NonNull
    @ColumnInfo(name = "Difficult")
    private String difficult;

    @NonNull
    @ColumnInfo(name = "FlagComplete")
    private boolean flagComplete;

    public TaskWork() { }

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
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(@NonNull String shortDescription) {
        this.shortDescription = shortDescription;
    }

    @NonNull
    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(@NonNull String fullDescription) {
        this.fullDescription = fullDescription;
    }

    @NonNull
    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(@NonNull String dateFrom) {
        this.dateFrom = dateFrom;
    }

    @NonNull
    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(@NonNull String dateTo) {
        this.dateTo = dateTo;
    }

    @NonNull
    public String getDifficult() {
        return difficult;
    }

    public void setDifficult(@NonNull String difficult) {
        this.difficult = difficult;
    }

    public boolean isFlagComplete() {
        return flagComplete;
    }

    public void setFlagComplete(boolean flagComplete) {
        this.flagComplete = flagComplete;
    }
}
