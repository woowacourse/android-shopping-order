package woowacourse.shopping.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import woowacourse.shopping.data.entity.CartProductEntity

@Dao
interface CartProductDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertCartProduct(cartProduct: CartProductEntity)

    @Delete
    suspend fun deleteCartProduct(cartProduct: CartProductEntity)

    @Query("SELECT * FROM CartProducts WHERE uid = :id")
    suspend fun getCartProduct(id: Int): CartProductEntity?

    @Query("SELECT * FROM CartProducts LIMIT :endIndex OFFSET :startIndex")
    suspend fun getCartProductsInRange(
        startIndex: Int,
        endIndex: Int,
    ): List<CartProductEntity>

    @Query("SELECT * FROM CartProducts WHERE uid IN (:uids)")
    suspend fun getCartProductsByUids(uids: List<Int>): List<CartProductEntity>

    @Query("UPDATE CartProducts SET quantity = quantity + :diff WHERE uid = :id")
    suspend fun updateProduct(
        id: Int,
        diff: Int,
    )

    @Query("SELECT quantity FROM CartProducts WHERE uid = :uid")
    suspend fun getProductQuantity(uid: Int): Int?

    @Query("SELECT COUNT(*) FROM CartProducts")
    suspend fun getAllProductsSize(): Int

    @Query("SELECT SUM(quantity) FROM CartProducts")
    suspend fun getCartItemSize(): Int
}
