package com.caper.jungsoos.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This is the product data class
 * This is the Product Table for the SqlLite
 */
@Entity(tableName = "product")
data class Product(
    @PrimaryKey @NonNull var id: String = "",
    var thumbnail: String = "",
    var name: String = "",
    var price: String = ""
)

/**
 * This is the Cart data Class
 */
data class Cart(var product: Product? = null, var quantity: Int? = 0)