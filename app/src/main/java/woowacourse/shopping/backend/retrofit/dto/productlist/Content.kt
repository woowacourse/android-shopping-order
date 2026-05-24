package woowacourse.shopping.backend.retrofit.dto.productlist

import kotlinx.serialization.Serializable

@Serializable
data class Content(
    val id: Long,
    val product: Product,
    val quantity: Int,
)
