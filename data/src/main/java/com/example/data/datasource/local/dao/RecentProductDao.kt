package com.example.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.datasource.local.entity.recentproduct.RecentProductEntity

@Dao
interface RecentProductDao {
    @Query("SELECT * FROM recent_products WHERE productId = :productId")
    fun findOrNull(productId: Int): RecentProductEntity?

    @Query("SELECT * FROM recent_products ORDER BY seen_date_time DESC LIMIT :size")
    fun findRange(size: Int): List<RecentProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recentProductEntity: RecentProductEntity)

    @Query("UPDATE recent_products SET seen_date_time = :seenDateTime WHERE productId = :productId")
    fun update(
        productId: Int,
        seenDateTime: Long,
    )

    @Query("SELECT * FROM recent_products WHERE category = :category ORDER BY seen_date_time DESC")
    fun findCategory(category: String): List<RecentProductEntity>
}
