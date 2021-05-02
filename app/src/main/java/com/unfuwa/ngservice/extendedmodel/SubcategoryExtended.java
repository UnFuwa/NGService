package com.unfuwa.ngservice.extendedmodel;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.unfuwa.ngservice.model.KnowledgeBase;
import com.unfuwa.ngservice.model.Subcategory;

import java.util.List;

public class SubcategoryExtended {

    @Embedded
    private Subcategory subcategory;

    @Relation(parentColumn = "Name", entityColumn = "NameSubcategory")
    private List<KnowledgeBase> listKnowledgeBase;

    public Subcategory getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(Subcategory subcategory) {
        this.subcategory = subcategory;
    }

    public List<KnowledgeBase> getListKnowledgeBase() {
        return listKnowledgeBase;
    }

    public void setListKnowledgeBase(List<KnowledgeBase> listKnowledgeBase) {
        this.listKnowledgeBase = listKnowledgeBase;
    }
}
