package woowacourse.shopping.data.remote.dto

import kotlinx.serialization.Serializable
import woowacourse.shopping.data.model.Cart
import woowacourse.shopping.data.model.CartItem

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

fun CartResponse.toDomain(): Cart =
    Cart(
        content.map {
            CartItem(
                id = it.id,
                product = it.product.toDomain(),
                quantity = it.quantity,
            )
        },
    )
