package com.unfuwa.ngservice.extendedmodel;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.unfuwa.ngservice.model.City;
import com.unfuwa.ngservice.model.Filial;

import java.util.List;

public class FilialCity {
    @Embedded
    private Filial filial;

    @Relation(parentColumn = "NameCity", entityColumn = "Name")
    private List<City> city;

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public List<City> getCity() {
        return city;
    }

    public void setCity(List<City> city) {
        this.city = city;
    }
}
