package com.caper.jungsoos.entities

import androidx.room.ColumnInfo

/**
 * Not using
 * This can be use to fetch only these values from the SqlLite
 */
class ProductList {
    @ColumnInfo(name = "id")
    var id: String? = null

    @ColumnInfo(name = "name")
    var name: String? = null

    @ColumnInfo(name = "thumbnail")
    var image: String? = null

}