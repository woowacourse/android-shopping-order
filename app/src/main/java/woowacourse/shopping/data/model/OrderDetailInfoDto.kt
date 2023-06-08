package woowacourse.shopping.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderDetailInfoDto(
    val product: ProductDto,
    val quantity: Int
)
