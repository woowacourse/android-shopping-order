package woowacourse.shopping.mapper

import com.example.domain.model.OrderHistoryProduct
import com.example.domain.model.OrderState
import woowacourse.shopping.model.OrderHistoryProductUiModel
import woowacourse.shopping.model.OrderStateUiModel

fun OrderHistoryProduct.toPresentation() = OrderHistoryProductUiModel(
    orderId,
    payAmount.value,
    orderAt,
    orderStatus.toPresentation(),
    productName,
    productImageUrl,
    totalProductCount
)

private fun OrderState.toPresentation() = OrderStateUiModel.values().first { it.name == this.name }
