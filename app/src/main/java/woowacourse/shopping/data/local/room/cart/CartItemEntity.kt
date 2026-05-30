package woowacourse.shopping.data.local.room.cart

import androidx.room.Entity
import androidx.room.PrimaryKey
import woowacourse.shopping.domain.model.cart.CartItem

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: Long,
    val quantity: Int,
    val createdAtMillis: Long,
) {
    fun toDomain(): CartItem =
        CartItem(
            productId = (productId),
            quantity = quantity,
        )

    companion object {
        fun fromDomain(
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
