package woowacourse.shopping.data.model

import woowacourse.shopping.presentation.cart.model.Content
import woowacourse.shopping.product.catalog.model.Pageable
import woowacourse.shopping.product.catalog.model.Sort

data class CartItemResponse(
    val content: List<Content>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: Sort,
    val totalElements: Long,
    val totalPages: Int
)