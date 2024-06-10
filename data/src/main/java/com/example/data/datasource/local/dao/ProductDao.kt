package com.example.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.datasource.local.entity.product.ProductEntity

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE productId = :id")
    fun findOrNull(id: Int): ProductEntity?

    @Query("SELECT * FROM products LIMIT :pageSize OFFSET :page * :pageSize")
    fun findRange(
        page: Int,
        pageSize: Int,
    ): List<ProductEntity>

    @Query("SELECT COUNT(*) FROM products")
    fun totalCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(productEntities: List<ProductEntity>)
}
