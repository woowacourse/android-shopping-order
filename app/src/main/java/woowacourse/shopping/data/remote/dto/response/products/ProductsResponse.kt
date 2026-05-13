package woowacourse.shopping.data.remote.dto.response.products

import kotlinx.serialization.Serializable
import woowacourse.shopping.data.remote.dto.response.Pageable
import woowacourse.shopping.data.remote.dto.response.Sort

@Serializable
data class ProductsResponse(
    val content: List<ProductDto>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: Sort,
    val totalElements: Long,
    val totalPages: Int,
)
