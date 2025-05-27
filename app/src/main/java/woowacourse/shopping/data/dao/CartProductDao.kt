package woowacourse.shopping.data.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface CartProductDao {
    @Query("SELECT quantity FROM cart_product WHERE product_id = :productId")
    fun getQuantityByProductId(productId: Int): Int?

    @Query("UPDATE cart_product SET quantity = :quantity WHERE product_id = :productId")
    fun updateQuantity(
        productId: Int,
        quantity: Int,
    )
}
