package com.unfuwa.ngservice.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(
        tableName = "Notifications",
        foreignKeys = {
                @ForeignKey(
                        entity = TypeNotification.class,
                        parentColumns = "Name",
                        childColumns = "NameType",
                        onDelete = ForeignKey.RESTRICT,
                        onUpdate = ForeignKey.RESTRICT),
                @ForeignKey(
                        entity = Specialist.class,
                        parentColumns = "Login",
                        childColumns = "LoginSpecialist",
                        onDelete = ForeignKey.RESTRICT,
                        onUpdate = ForeignKey.RESTRICT)
        }
)
public class Notification {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "Id")
    private int id;

    @NonNull
    @ColumnInfo(name = "LoginSpecialist")
    private String loginSpecialist;

    @NonNull
    @ColumnInfo(name = "NameType")
    private String nameType;

    @NonNull
    @ColumnInfo(name = "Title")
    private String title;

    @NonNull
    @ColumnInfo(name = "Content")
    private String content;

    @NonNull
    @ColumnInfo(name = "DateCreate")
    private Date dateCreate;

    public Notification() { }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
    public String getNameType() {
        return nameType;
    }

    public void setNameType(@NonNull String nameType) {
        this.nameType = nameType;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    @NonNull
    public Date getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(@NonNull Date dateCreate) {
        this.dateCreate = dateCreate;
    }
}
