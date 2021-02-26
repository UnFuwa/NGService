package com.unfuwa.ngservice.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Filials",
        foreignKeys = @ForeignKey(
                entity = City.class,
                parentColumns = "Name",
                childColumns = "NameCity",
                onDelete = ForeignKey.RESTRICT,
                onUpdate = ForeignKey.RESTRICT
        )
)
public class Filial {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "Id")
    private int id;

    @NonNull
    @ColumnInfo(name = "NameCity")
    private String nameCity;

    @NonNull
    @ColumnInfo(name = "NameStreet")
    private String nameStreet;

    @NonNull
    @ColumnInfo(name = "Number")
    private String number;

    public Filial() { }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    @NonNull
    public String getNameCity() {
        return nameCity;
    }

    public void setNameCity(@NonNull String nameCity) {
        this.nameCity = nameCity;
    }

    @NonNull
    public String getNameStreet() {
        return nameStreet;
    }

    public void setNameStreet(@NonNull String nameStreet) {
        this.nameStreet = nameStreet;
    }

    @NonNull
    public String getNumber() {
        return number;
    }

    public void setNumber(@NonNull String number) {
        this.number = number;
    }
}
