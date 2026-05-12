package woowacourse.shopping._archive.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import woowacourse.shopping._archive.local.entity.CartEntity

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    suspend fun getAll(): List<CartEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(cartEntity: CartEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE productId = :productId")
    suspend fun updateQuantity(
        productId: Long,
        quantity: Int,
    )

    @Query("DELETE FROM cart_items WHERE productId = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM cart_items WHERE productId = :id")
    suspend fun getCartItemById(id: Long): CartEntity?

    @Query("SELECT * FROM cart_items ORDER BY productId ASC LIMIT :count OFFSET :fromIndex")
    suspend fun getPagedEntities(fromIndex: Int, count: Int): List<CartEntity>

    @Query("SELECT COUNT(*) FROM cart_items")
    suspend fun getSize(): Int
}
