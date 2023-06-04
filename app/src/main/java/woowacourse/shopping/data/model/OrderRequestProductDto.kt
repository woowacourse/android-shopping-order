package woowacourse.shopping.data.model

import com.example.domain.model.OrderProduct
import kotlinx.serialization.Serializable

@Serializable
data class OrderRequestProductDto(
    val quantity: Int,
    val productId: Int
)

fun OrderProduct.toData() = OrderRequestProductDto(
    quantity, product.id.toInt()
)
