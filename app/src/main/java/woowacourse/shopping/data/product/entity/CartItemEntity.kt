package woowacourse.shopping.data.product.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import woowacourse.shopping.domain.cart.CartItem

@Entity(tableName = "shoppingCart")
class CartItemEntity(
    @PrimaryKey
    val id: Long,
    val productId: Long,
    val name: String,
    val price: Int,
    val imageUrl: String?,
    val quantity: Int,
    val category: String,
) {
    fun toDomain(): CartItem =
        CartItem(
            id,
            productId,
            name,
            price,
            category,
            imageUrl,
            quantity,
        )
}
