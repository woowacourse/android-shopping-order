package woowacourse.shopping.local.cart

import androidx.room.Entity
import androidx.room.PrimaryKey
import woowacourse.shopping.model.cart.CartItem

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: Long,
    val quantity: Int,
    val createdAtMillis: Long,
) {
    fun toCartItem(): CartItem =
        CartItem(
            productId = (productId),
            quantity = quantity,
        )

    companion object {
        fun fromCartItem(
            cartItem: CartItem,
            createdAtMillis: Long,
        ): CartItemEntity =
            CartItemEntity(
                productId = cartItem.productId,
                quantity = cartItem.quantity,
                createdAtMillis = createdAtMillis,
            )
    }
}
