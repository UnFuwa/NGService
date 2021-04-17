package com.unfuwa.ngservice.extendedmodel;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.unfuwa.ngservice.model.Equipment;
import com.unfuwa.ngservice.model.RegService;
import com.unfuwa.ngservice.model.Service;

public class RegServiceEquipment {

    @Embedded
    private RegService regService;

    @Relation(parentColumn = "NameService", entityColumn = "Name")
    private Service service;

    public RegService getRegService() {
        return regService;
    }

    public void setRegService(RegService regService) {
        this.regService = regService;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
