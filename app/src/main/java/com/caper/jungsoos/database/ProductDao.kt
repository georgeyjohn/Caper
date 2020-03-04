package com.caper.jungsoos.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.caper.jungsoos.entities.Product

/**
 * This is the SqlLite Queries to Fetch details from the SqlLite
 */
@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(product: ArrayList<Product>)

    @Query("SELECT * FROM product")
    fun getProduct(): List<Product>

    @Query("SELECT * FROM product where id=:id")
    fun getProductWithID(id: String): Product
}