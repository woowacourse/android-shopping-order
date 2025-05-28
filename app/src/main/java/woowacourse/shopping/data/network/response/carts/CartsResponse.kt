package woowacourse.shopping.data.network.response.carts

import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.cart.CartsSinglePage

@Serializable
data class CartsResponse(
    val content: List<Content>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: SortX,
    val totalElements: Int,
    val totalPages: Int,
) {
    fun toDomain(): CartsSinglePage {
        val shoppingCart = content.map { it.toDomain() }
        return CartsSinglePage(
            shoppingCart,
            last,
        )
    }
}
