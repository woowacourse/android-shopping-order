package woowacourse.shopping.data.datasource.remote.model.response.cart

import woowacourse.shopping.data.datasource.remote.model.response.Sort
import woowacourse.shopping.data.datasource.remote.model.response.product.Pageable

data class CartResponse(
    val cartContent: List<CartContent>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: Sort,
    val totalElements: Int,
    val totalPages: Int,
)
