package woowacourse.shopping.storage.room.shoppingcart

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import woowacourse.shopping.storage.room.ShoppingCartItemRow

@Dao
interface ShoppingCartDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(shoppingCartEntity: ShoppingCartEntity): Long

    @Query("DELETE FROM shopping_cart_items WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query(
        """
        SELECT c.id AS id,
               s.product_id AS product_id,
               s.title AS title,
               s.price AS price,
               s.image_url AS image_url,
               s.quantity AS quantity
        FROM shopping_cart_items c
        INNER JOIN shopping_items s ON s.product_id = c.product_id
        ORDER BY c.id
        """,
    )
    suspend fun getShoppingCartItemRows(): List<ShoppingCartItemRow>
}

