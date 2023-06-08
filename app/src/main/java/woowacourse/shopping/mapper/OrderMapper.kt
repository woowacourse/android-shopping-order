package woowacourse.shopping.mapper

import com.example.domain.model.Order
import woowacourse.shopping.model.OrderUiModel
import woowacourse.shopping.model.toPresentation

fun Order.toPresentation(): OrderUiModel =
    OrderUiModel(
        orderId,
        payAmount.value,
        orderAt,
        orderStatus.toPresentation(),
        productName,
        productImageUrl,
        totalProductCount
    )
