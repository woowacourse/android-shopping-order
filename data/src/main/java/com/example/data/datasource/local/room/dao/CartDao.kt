package com.example.data.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.datasource.local.room.entity.cart.CartItemEntity

@Dao
interface CartDao {
    @Query("SELECT * FROM cart")
    fun findAll(): List<CartItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cartItemEntity: CartItemEntity)

    @Query("UPDATE cart SET quantity = :quantity WHERE product_id = :productId")
    fun changeQuantity(
        productId: Int,
        quantity: com.example.domain.model.Quantity,
    )

    @Query("DELETE FROM cart WHERE product_id = :productId")
    fun delete(productId: Int)

    fun find(productId: Int): CartItemEntity {
        return findOrNull(productId) ?: throw IllegalArgumentException()
    }

    @Query("SELECT * FROM cart WHERE product_id = :productId")
    fun findOrNull(productId: Int): CartItemEntity?

    @Query("SELECT * FROM cart LIMIT :pageSize OFFSET :page * :pageSize")
    fun findRange(
        page: Int,
        pageSize: Int,
    ): List<CartItemEntity>

    @Query("SELECT COUNT(*) FROM cart")
    fun totalCount(): Int
}
