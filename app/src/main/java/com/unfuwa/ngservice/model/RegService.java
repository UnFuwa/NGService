package com.unfuwa.ngservice.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "RegService",
        foreignKeys = {
                @ForeignKey(
                        entity = Equipment.class,
                        parentColumns = "Id",
                        childColumns = "IdEquipment",
                        onDelete = ForeignKey.RESTRICT,
                        onUpdate = ForeignKey.RESTRICT),
                @ForeignKey(
                        entity = Specialist.class,
                        parentColumns = "Login",
                        childColumns = "LoginSpecialist",
                        onDelete = ForeignKey.RESTRICT,
                        onUpdate = ForeignKey.RESTRICT),
                @ForeignKey(
                        entity = Service.class,
                        parentColumns = "Name",
                        childColumns = "NameService",
                        onDelete = ForeignKey.RESTRICT,
                        onUpdate = ForeignKey.RESTRICT)
        }
)
public class RegService {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "Id")
    private int id;

    @NonNull
    @ColumnInfo(name = "IdEquipment")
    private int idEquipment;

    @NonNull
    @ColumnInfo(name = "LoginSpecialist")
    private String loginSpeciliast;

    @NonNull
    @ColumnInfo(name = "NameService")
    private String nameService;

    @Nullable
    @ColumnInfo(name = "Description")
    private String description;

    public RegService() { }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public int getIdEquipment() {
        return idEquipment;
    }

    public void setIdEquipment(int idEquipment) {
        this.idEquipment = idEquipment;
    }

    @NonNull
    public String getLoginSpeciliast() {
        return loginSpeciliast;
    }

    public void setLoginSpeciliast(@NonNull String loginSpeciliast) {
        this.loginSpeciliast = loginSpeciliast;
    }

    @NonNull
    public String getNameService() {
        return nameService;
    }

    public void setNameService(@NonNull String nameService) {
        this.nameService = nameService;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }
}
