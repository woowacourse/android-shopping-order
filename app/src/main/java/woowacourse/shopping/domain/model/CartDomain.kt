package woowacourse.shopping.domain.model

data class CartDomain(
    val cartItems: List<CartItemDomain>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val totalPages: Int,
)
