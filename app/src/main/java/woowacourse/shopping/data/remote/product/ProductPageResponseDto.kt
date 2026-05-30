package woowacourse.shopping.data.remote.product

import kotlinx.serialization.Serializable

@Serializable
data class ProductPageResponseDto(
    val content: List<ProductResponseDto>? = null,
    val totalElements: Long? = null,
    val last: Boolean? = null,
)
