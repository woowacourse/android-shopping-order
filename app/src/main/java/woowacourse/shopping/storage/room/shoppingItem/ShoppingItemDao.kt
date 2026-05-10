package woowacourse.shopping.storage.room.shoppingItem

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingItemDao {
    @Query("SELECT * FROM shopping_items ORDER BY product_id")
    fun observeAll(): Flow<List<ShoppingItemEntity>>

    @Query("SELECT * FROM shopping_items WHERE product_id = :productId LIMIT 1")
    suspend fun getByProductId(productId: Long): ShoppingItemEntity?

    @Query("SELECT quantity FROM shopping_items WHERE product_id = :productId LIMIT 1")
    suspend fun getQuantityOrNull(productId: Long): Int?

    @Query("UPDATE shopping_items SET quantity = :quantity WHERE product_id = :productId")
    suspend fun updateQuantity(
        productId: Long,
        quantity: Int,
    ): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(shoppingItems: List<ShoppingItemEntity>)

    @Query("SELECT COUNT(*) FROM shopping_items")
    suspend fun count(): Int
}

