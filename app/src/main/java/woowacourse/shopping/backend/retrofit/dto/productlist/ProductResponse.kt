package woowacourse.shopping.backend.retrofit.dto.productlist

import kotlinx.serialization.Serializable
import woowacourse.shopping.backend.retrofit.dto.Pageable
import woowacourse.shopping.backend.retrofit.dto.PageableResponse
import woowacourse.shopping.backend.retrofit.dto.Sort

@Serializable
data class ProductResponse(
    val totalElements: Long, // 카테코리의 총 수량
    val totalPages: Int,
    val size: Int,
    val content: List<Pageable>,
    val number: Int,
    val sort: Sort,
    val pageable: PageableResponse,
    val first: Boolean,
    val last: Boolean,
    val numberOfElements: Int, // 총 몇개 조회 되었는지
    val empty: Boolean,
)
