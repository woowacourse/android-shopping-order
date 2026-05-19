package woowacourse.shopping.repository.http.dto.product

import kotlinx.serialization.Serializable

@Serializable
data class ProductPageResponseDto(
    val content: List<ProductResponseDto>? = null,
    val totalElements: Long? = null,
    val last: Boolean? = null,
)
