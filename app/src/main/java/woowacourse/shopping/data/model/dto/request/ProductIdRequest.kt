package woowacourse.shopping.data.model.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ProductIdRequest(
    val productId: Long
)
