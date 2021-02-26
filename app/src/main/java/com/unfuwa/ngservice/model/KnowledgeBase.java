package com.unfuwa.ngservice.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "KnowledgeBase",
        foreignKeys = @ForeignKey(
                entity = Subcategory.class,
                parentColumns = "Name",
                childColumns = "NameSubcategory",
                onDelete = ForeignKey.RESTRICT,
                onUpdate = ForeignKey.RESTRICT)
)
public class KnowledgeBase {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "Id")
    private int id;

    @NonNull
    @ColumnInfo (name = "NameSubcategory")
    private String nameSubcategory;

    @NonNull
    @ColumnInfo (name = "Theme")
    private String theme;

    @NonNull
    @ColumnInfo (name = "ShortDescription")
    private String shortDescription;

    @NonNull
    @ColumnInfo (name = "FullDescription")
    private String fullDescription;

    @NonNull
    @ColumnInfo (name = "URLFullContent")
    private String URL;

    public KnowledgeBase() { }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    @NonNull
    public String getNameSubcategory() {
        return nameSubcategory;
    }

    public void setNameSubcategory(@NonNull String nameSubcategory) {
        this.nameSubcategory = nameSubcategory;
    }

    @NonNull
    public String getTheme() {
        return theme;
    }

    public void setTheme(@NonNull String theme) {
        this.theme = theme;
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
    public String getURL() {
        return URL;
    }

    public void setURL(@NonNull String URL) {
        this.URL = URL;
    }
}
