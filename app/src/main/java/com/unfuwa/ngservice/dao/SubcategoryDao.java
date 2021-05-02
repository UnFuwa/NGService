package com.unfuwa.ngservice.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.unfuwa.ngservice.extendedmodel.SubcategoryExtended;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface SubcategoryDao {

    @Query("SELECT * FROM Subcategories WHERE NameCategory = :category")
    Flowable<List<SubcategoryExtended>> getSubcategoriesByCategory(String category);
}
