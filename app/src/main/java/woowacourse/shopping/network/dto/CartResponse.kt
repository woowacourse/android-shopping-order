package woowacourse.shopping.network.dto

import kotlinx.serialization.Serializable
import woowacourse.shopping.model.Cart
import woowacourse.shopping.model.CartItem

@Serializable
data class CartResponse(
    val content: List<CartItemResponse>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: PageableResponse,
    val size: Int,
    val sort: SortResponse,
    val totalElements: Long,
    val totalPages: Int
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