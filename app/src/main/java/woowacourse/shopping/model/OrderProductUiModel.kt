package woowacourse.shopping.model

import com.example.domain.model.OrderProduct
import woowacourse.shopping.mapper.toPresentation

data class OrderProductUiModel(
    val quantity: Int,
    val product: ProductUiModel
)

fun OrderProduct.toPresentation() = OrderProductUiModel(
    quantity, product.toPresentation(quantity)
)
