package woowacourse.shopping.data.model.order

import kotlinx.serialization.Serializable
import woowacourse.shopping.data.model.product.ProductDto

@Serializable
data class OrderDetailInfoDto(
    val product: ProductDto,
    val quantity: Int
)
