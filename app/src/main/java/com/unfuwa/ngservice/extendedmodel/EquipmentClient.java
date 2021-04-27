package com.unfuwa.ngservice.extendedmodel;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.unfuwa.ngservice.model.Client;
import com.unfuwa.ngservice.model.Equipment;
import com.unfuwa.ngservice.model.User;

import java.io.Serializable;

public class EquipmentClient {

    @Embedded
    private Equipment equipment;

    @Relation(parentColumn = "EmailClient", entityColumn = "Email")
    private Client client;

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
