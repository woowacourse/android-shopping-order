package woowacourse.shopping.data.cart.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import woowacourse.shopping.data.cart.local.entity.CartItemEntity
import woowacourse.shopping.domain.model.Quantity

@Dao
interface CartDao {
    @Query("SELECT * FROM cart")
    fun findAll(): List<CartItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cartItemEntity: CartItemEntity)

    @Query("UPDATE cart SET quantity = :quantity WHERE product_id = :productId")
    fun changeQuantity(
        productId: Int,
        quantity: Quantity,
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
