package com.unfuwa.ngservice.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Clients",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "Login",
                childColumns = "Email",
                onDelete = ForeignKey.RESTRICT,
                onUpdate = ForeignKey.RESTRICT)
)
public class Client {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "Email")
    private String email;

    @NonNull
    @ColumnInfo(name = "FName")
    private String fName;

    @NonNull
    @ColumnInfo(name = "IName")
    private String iName;

    @Nullable
    @ColumnInfo(name = "OName")
    private String oName;

    @Nullable
    @ColumnInfo(name = "Telephone")
    private String telephone;

    public Client() { }

    @Ignore
    public Client(@NonNull String email, @NonNull String fName, @NonNull String iName, @Nullable String oName, @Nullable String telephone) {
        this.email = email;
        this.fName = fName;
        this.iName = iName;
        this.oName = oName;
        this.telephone = telephone;
    }

    public Client(User user, Client client) {
        this.email = user.getLogin();
        this.fName = client.getFName();
        this.iName = client.getIName();
        this.oName = client.getOName();
        this.telephone = client.getTelephone();
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
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

    @Nullable
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(@Nullable String telephone) {
        this.telephone = telephone;
    }
}
