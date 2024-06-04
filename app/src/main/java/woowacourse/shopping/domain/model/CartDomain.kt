package woowacourse.shopping.domain.model

import woowacourse.shopping.data.model.Pageable
import woowacourse.shopping.data.model.Sort

data class CartDomain(
    val cartItems: List<CartItemDomain>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val totalPages: Int,
)


