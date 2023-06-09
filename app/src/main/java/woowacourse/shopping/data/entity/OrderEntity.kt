package woowacourse.shopping.data.entity

import com.squareup.moshi.JsonClass
import woowacourse.shopping.data.entity.CartItemEntity.Companion.toDomain
import woowacourse.shopping.data.entity.CartItemEntity.Companion.toEntity
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.order.Order

@JsonClass(generateAdapter = true)
data class OrderEntity(
    val id: Long,
    val totalPrice: Int,
    val cartItems: List<CartItemEntity>
) {
    companion object {
        fun Order.toEntity() = OrderEntity(id, price, cart.value.map { it.toEntity() })

        fun OrderEntity.toDomain() =
            Order(id, Cart(cartItems.map { it.toDomain() }.toSet()), totalPrice)
    }
}
