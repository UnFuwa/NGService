package com.unfuwa.ngservice.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Requests",
        foreignKeys = @ForeignKey(
                entity = Client.class,
                parentColumns = "Email",
                childColumns = "EmailClient",
                onDelete = ForeignKey.RESTRICT,
                onUpdate = ForeignKey.RESTRICT)
)
public class Request {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "Id")
    private int id;

    @NonNull
    @ColumnInfo(name = "EmailClient")
    private String emailClient;

    @NonNull
    @ColumnInfo(name = "Address")
    private String address;

    @Nullable
    @ColumnInfo(name = "Description")
    private String description;

    @NonNull
    @ColumnInfo(name = "DateArrive")
    private String dateArrive;

    @NonNull
    @ColumnInfo(name = "DateReceive")
    private String dateReceive;

    public Request() { }

    @Ignore
    public Request(@NonNull String emailClient, @NonNull String address, @Nullable String description, @NonNull String dateArrive, @NonNull String dateReceive) {
        this.emailClient = emailClient;
        this.address = address;
        this.description = description;
        this.dateArrive = dateArrive;
        this.dateReceive = dateReceive;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    @NonNull
    public String getEmailClient() {
        return emailClient;
    }

    public void setEmailClient(@NonNull String emailClient) {
        this.emailClient = emailClient;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @NonNull
    public String getDateArrive() {
        return dateArrive;
    }

    public void setDateArrive(@NonNull String dateArrive) {
        this.dateArrive = dateArrive;
    }

    @NonNull
    public String getDateReceive() {
        return dateReceive;
    }

    public void setDateReceive(@NonNull String dateReceive) {
        this.dateReceive = dateReceive;
    }
}
