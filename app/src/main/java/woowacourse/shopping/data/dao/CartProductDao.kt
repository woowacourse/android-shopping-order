package woowacourse.shopping.data.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface CartProductDao {
    @Query("SELECT COUNT(*) FROM cart_product")
    fun getTotalCount(): Int

    @Query("SELECT quantity FROM cart_product WHERE product_id = :productId")
    fun getQuantityByProductId(productId: Long): Int?

    @Query("SELECT COALESCE(SUM(quantity), 0) FROM cart_product")
    fun getTotalQuantity(): Int

    @Query("UPDATE cart_product SET quantity = :quantity WHERE product_id = :productId")
    fun updateQuantity(
        productId: Long,
        quantity: Int,
    )
}
