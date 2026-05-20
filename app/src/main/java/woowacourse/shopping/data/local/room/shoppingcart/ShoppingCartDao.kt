package woowacourse.shopping.data.local.room.shoppingcart

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingCartDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(shoppingCartEntity: ShoppingCartEntity): Long

    @Query("DELETE FROM shopping_cart_items WHERE product_id = :productId")
    suspend fun deleteByProductId(productId: Long): Int

    @Query(
        """
        SELECT c.id AS id,
               s.product_id AS product_id,
               s.title AS title,
               s.price AS price,
               s.image_url AS image_url,
               s.category AS category,
               s.quantity AS quantity
        FROM shopping_cart_items c
        INNER JOIN shopping_items s ON s.product_id = c.product_id
        WHERE s.quantity > 0
        ORDER BY c.id
        """,
    )
    fun observeShoppingCartItemRows(): Flow<List<ShoppingCartItemRow>>
}
