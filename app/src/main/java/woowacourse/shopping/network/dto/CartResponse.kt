package woowacourse.shopping.network.dto

import kotlinx.serialization.Serializable
import woowacourse.shopping.model.Cart
import woowacourse.shopping.model.CartItem

@Serializable
data class CartResponse(
    val content: List<CartItemResponse>,
    val pageable: PageableResponse,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean,
    val size: Int,
    val number: Int,
    val sort: SortResponse,
    val numberOfElements: Int,
    val first: Boolean,
    val empty: Boolean,
)

fun CartResponse.toDomain(): Cart {
    return Cart(
        content.map {
            CartItem(
                id = it.id,
                product = it.product.toDomain(),
                quantity = it.quantity,
            )
        }
    )
}