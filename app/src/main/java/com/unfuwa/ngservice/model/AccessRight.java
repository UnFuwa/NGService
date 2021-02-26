package com.unfuwa.ngservice.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "AccessRights")
public class AccessRight {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "Name")
    private String name;

    public AccessRight() { }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }
}
