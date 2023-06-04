package woowacourse.shopping.data.model

import com.example.domain.model.OrderProduct

data class OrderRequestProductDto(
    val quantity: Int,
    val productId: Int
)

fun OrderProduct.toData() = OrderRequestProductDto(
    quantity, product.id.toInt()
)
