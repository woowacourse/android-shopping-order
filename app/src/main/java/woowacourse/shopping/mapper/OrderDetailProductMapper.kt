package woowacourse.shopping.mapper

import com.example.domain.model.OrderDetailProduct
import com.example.domain.model.OrderInfo
import com.example.domain.model.OrderState
import woowacourse.shopping.model.OrderDetailProductUiModel
import woowacourse.shopping.model.OrderInfoUiModel
import woowacourse.shopping.model.OrderStateUiModel

fun OrderDetailProduct.toPresentation() =
    OrderDetailProductUiModel(quantity, product.toPresentation())

fun OrderInfo.toPresentation() =
    OrderInfoUiModel(
        orderId,
        orderAt,
        orderState.toPresentation(),
        payAmount.value,
        usedPoint.value,
        savedPoint.value,
        products.map { it.toPresentation() }
    )

private fun OrderState.toPresentation() = OrderStateUiModel.values().first { it.name == this.name }
