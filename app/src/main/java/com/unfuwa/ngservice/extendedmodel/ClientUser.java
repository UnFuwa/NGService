package com.unfuwa.ngservice.extendedmodel;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.unfuwa.ngservice.model.Client;
import com.unfuwa.ngservice.model.User;

import java.io.Serializable;

public class ClientUser implements Serializable {

    @Embedded
    private Client client;

    @Relation(parentColumn = "Email", entityColumn = "Login")
    private User user;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
