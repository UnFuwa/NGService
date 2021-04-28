package com.unfuwa.ngservice.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Equipments",
        foreignKeys = {
                @ForeignKey(
                        entity = TypeEquipment.class,
                        parentColumns = "Name",
                        childColumns = "NameType",
                        onDelete = ForeignKey.RESTRICT,
                        onUpdate = ForeignKey.RESTRICT),
                @ForeignKey(
                        entity = Client.class,
                        parentColumns = "Email",
                        childColumns = "EmailClient",
                        onDelete = ForeignKey.RESTRICT,
                        onUpdate = ForeignKey.RESTRICT)
        }
)
public class Equipment {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "Id")
    private int id;

    @NonNull
    @ColumnInfo(name = "NameType")
    private String nameType;

    @NonNull
    @ColumnInfo(name = "EmailClient")
    private String emailClient;

    @NonNull
    @ColumnInfo(name = "Name")
    private String name;

    @Nullable
    @ColumnInfo(name = "Characters")
    private String characters;

    @NonNull
    @ColumnInfo(name = "DescriptionProblem")
    private String descriptionProblem;

    @NonNull
    @ColumnInfo(name = "StatusRepair")
    private boolean statusRepair;

    public Equipment() { }

    public Equipment(@NonNull String nameType, @NonNull String emailClient, @NonNull String name, @Nullable String characters, @NonNull String descriptionProblem, boolean statusRepair) {
        this.nameType = nameType;
        this.emailClient = emailClient;
        this.name = name;
        this.characters = characters;
        this.descriptionProblem = descriptionProblem;
        this.statusRepair = statusRepair;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    @NonNull
    public String getNameType() {
        return nameType;
    }

    public void setNameType(@NonNull String nameType) {
        this.nameType = nameType;
    }

    @NonNull
    public String getEmailClient() {
        return emailClient;
    }

    public void setEmailClient(@NonNull String emailClient) {
        this.emailClient = emailClient;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Nullable
    public String getCharacters() {
        return characters;
    }

    public void setCharacters(@Nullable String characters) {
        this.characters = characters;
    }

    @NonNull
    public String getDescriptionProblem() {
        return descriptionProblem;
    }

    public void setDescriptionProblem(@NonNull String descriptionProblem) {
        this.descriptionProblem = descriptionProblem;
    }

    public boolean isStatusRepair() {
        return statusRepair;
    }

    public void setStatusRepair(boolean statusRepair) {
        this.statusRepair = statusRepair;
    }
}
