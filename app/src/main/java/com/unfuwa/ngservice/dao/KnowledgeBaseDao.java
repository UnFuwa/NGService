package com.unfuwa.ngservice.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.unfuwa.ngservice.model.KnowledgeBase;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface KnowledgeBaseDao {

    @Query("SELECT * FROM KnowledgeBase WHERE NameSubcategory = :subcategory")
    Flowable<List<KnowledgeBase>> getListKnowledgeBase(String subcategory);
}
