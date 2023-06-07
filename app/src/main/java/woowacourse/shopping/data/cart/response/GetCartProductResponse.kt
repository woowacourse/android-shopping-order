package woowacourse.shopping.data.cart.response

import kotlinx.serialization.Serializable
import woowacourse.shopping.data.product.response.GetProductResponse

@Serializable
data class GetCartProductResponse(
    val id: Int,
    val quantity: Int = 0,
    val product: GetProductResponse
)