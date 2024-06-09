package com.example.data.datasource.local.room.entity.recentproduct

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.datasource.local.room.entity.product.ProductEntity

@Entity(tableName = "recent_products")
class RecentProductEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @Embedded val product: ProductEntity,
    @ColumnInfo(name = "seen_date_time") val seenDateTime: Long,
)
