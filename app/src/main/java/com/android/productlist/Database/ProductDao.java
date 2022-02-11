package com.android.productlist.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM product")
    List<Product> getAll();

    @Query("SELECT * FROM product WHERE id IN (:id)")
    Product loadAllByProductids(int id);

    @Query("SELECT id FROM product")
    List<Integer> getallids();

    @Query("SELECT productname FROM product")
    List<String> getallproducts();

    @Query("SELECT productdescription FROM product")
    List<String> getallprodescription();

    @Query("SELECT productprice FROM product")
    List<Double> getallprodprice();

    @Insert
    void insertAll(Product... products);

    @Insert
    void insertProduct(Product product);

    @Insert
    void insertProducts(List<Product> products);

    @Update
    void updateProduct(Product product);

    @Delete
    void delete(Product product);

    @Query("DELETE FROM product WHERE id IN (:id)")
    void deleteproductbyuid(int id);

}
