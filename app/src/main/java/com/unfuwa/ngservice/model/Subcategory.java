package com.unfuwa.ngservice.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Subcategories",
        foreignKeys = @ForeignKey(
                entity = Category.class,
                parentColumns = "Name",
                childColumns = "NameCategory",
                onDelete = ForeignKey.RESTRICT,
                onUpdate = ForeignKey.RESTRICT)
)
public class Subcategory {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "Name")
    private String name;

    @NonNull
    @ColumnInfo(name = "NameCategory")
    private String nameCategory;

    public Subcategory() { }

    @Ignore
    public Subcategory(@NonNull String name, String nameCategory) {
        this.name = name;
        this.nameCategory = nameCategory;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(@NonNull String nameCategory) {
        this.nameCategory = nameCategory;
    }
}
