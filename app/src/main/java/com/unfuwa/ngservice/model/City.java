package com.unfuwa.ngservice.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Cities",
        foreignKeys = @ForeignKey(
                entity = Region.class,
                parentColumns = "Name",
                childColumns = "NameRegion",
                onDelete = ForeignKey.RESTRICT,
                onUpdate = ForeignKey.RESTRICT)
)
public class City {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "Name")
    private String name;

    @NonNull
    @ColumnInfo(name = "NameRegion")
    private String nameRegion;

    public City() { }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getNameRegion() {
        return nameRegion;
    }

    public void setNameRegion(@NonNull String nameRegion) {
        this.nameRegion = nameRegion;
    }
}
