package woowacourse.shopping.data.localdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import woowacourse.shopping.data.localdb.entity.CartItemQuantityEntity

@Dao
interface CartItemQuantityDao {
    @Query("SELECT * FROM cart_item_quantities")
    fun getAll(): Flow<List<CartItemQuantityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartItemQuantityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CartItemQuantityEntity>)

    @Query("SELECT * FROM cart_item_quantities WHERE productId = :productId")
    suspend fun findByProductId(productId: Long): CartItemQuantityEntity?

    @Query("DELETE FROM cart_item_quantities WHERE productId = :productId")
    suspend fun deleteByProductId(productId: Long)

    @Query("DELETE FROM cart_item_quantities WHERE cartItemId = :cartItemId")
    suspend fun deleteByCartItemId(cartItemId: Long)

    @Query("DELETE FROM cart_item_quantities")
    suspend fun clear()
}
