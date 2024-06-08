package com.example.data.datasource.local.room.entity.cart

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.datasource.local.room.entity.product.ProductEntity
import com.example.domain.model.Quantity

@Entity(tableName = "cart")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @Embedded
    @ColumnInfo(name = "product") val product: ProductEntity,
    @ColumnInfo(name = "quantity") var quantity: Quantity,
)
