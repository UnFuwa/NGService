package com.unfuwa.ngservice.extendedmodel;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.unfuwa.ngservice.model.Specialist;
import com.unfuwa.ngservice.model.User;

import java.io.Serializable;

public class SpecialistUser implements Serializable {

    @Embedded
    private Specialist specialist;

    @Relation(parentColumn = "Login", entityColumn = "Login")
    private User user;

    public Specialist getSpecialist() {
        return specialist;
    }

    public void setSpecialist(Specialist specialist) {
        this.specialist = specialist;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
