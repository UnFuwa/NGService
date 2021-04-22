package com.unfuwa.ngservice.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(
        tableName = "Specialists",
        foreignKeys = {
                @ForeignKey(
                    entity = Position.class,
                    parentColumns = "Name",
                    childColumns = "NamePosition",
                    onDelete = ForeignKey.RESTRICT,
                    onUpdate = ForeignKey.RESTRICT),
                @ForeignKey(
                    entity = User.class,
                    parentColumns = "Login",
                    childColumns = "Login",
                    onDelete = ForeignKey.RESTRICT,
                    onUpdate = ForeignKey.RESTRICT)
        }
)
public class Specialist implements Serializable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "Login")
    private String login;

    @NonNull
    @ColumnInfo(name = "NamePosition")
    private String namePosition;

    @NonNull
    @ColumnInfo(name = "FName")
    private String fName;

    @NonNull
    @ColumnInfo(name = "IName")
    private String iName;

    @Nullable
    @ColumnInfo(name = "OName")
    private String oName;

    public Specialist() { }

    @NonNull
    public String getLogin() {
        return login;
    }

    public void setLogin(@NonNull String login) {
        this.login = login;
    }

    @NonNull
    public String getNamePosition() {
        return namePosition;
    }

    public void setNamePosition(@NonNull String namePosition) {
        this.namePosition = namePosition;
    }

    @NonNull
    public String getFName() {
        return fName;
    }

    public void setFName(@NonNull String fName) {
        this.fName = fName;
    }

    @NonNull
    public String getIName() {
        return iName;
    }

    public void setIName(@NonNull String iName) {
        this.iName = iName;
    }

    @Nullable
    public String getOName() {
        return oName;
    }

    public void setOName(@Nullable String oName) {
        this.oName = oName;
    }
}
