package woowacourse.shopping.data.entity

import com.squareup.moshi.JsonClass
import woowacourse.shopping.data.entity.ProductEntity.Companion.toDomain
import woowacourse.shopping.data.entity.ProductEntity.Companion.toEntity
import woowacourse.shopping.domain.cart.CartItem

@JsonClass(generateAdapter = true)
data class CartItemEntity(
    val id: Long?,
    val quantity: Int,
    val product: ProductEntity
) {
    companion object {
        fun CartItem.toEntity() = CartItemEntity(
            id, quantity, product.toEntity()
        )

        fun CartItemEntity.toDomain() = CartItem(
            id ?: -1, quantity, product.toDomain()
        )
    }
}
