package com.unfuwa.ngservice.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Users",
        foreignKeys = @ForeignKey(
                entity = AccessRight.class,
                parentColumns = "Name",
                childColumns = "NameAccessRight",
                onDelete = ForeignKey.RESTRICT,
                onUpdate = ForeignKey.RESTRICT)
        /*indices = @Index(
                value = "Token",
                unique = true)*/
)
public class User {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "Login")
    private String login;

    @NonNull
    @ColumnInfo(name = "NameAccessRight")
    private String nameAccessRight;

    @NonNull
    @ColumnInfo(name = "Password")
    private String password;

    @NonNull
    @ColumnInfo(name = "Token", index = true)
    private String token;

    public User() { }

    @Ignore
    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Ignore
    public User(String login, String accessRight, String password, String token) {
        this.login = login;
        this.nameAccessRight = accessRight;
        this.password = password;
        this.token = token;
    }

    @NonNull
    public String getLogin() {
        return login;
    }

    public void setLogin(@NonNull String login) {
        this.login = login;
    }

    @NonNull
    public String getNameAccessRight() {
        return nameAccessRight;
    }

    public void setNameAccessRight(@NonNull String nameAccessRight) {
        this.nameAccessRight = nameAccessRight;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    @NonNull
    public String getToken() {
        return token;
    }

    public void setToken(@NonNull String token) {
        this.token = token;
    }
}
