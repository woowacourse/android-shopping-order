package woowacourse.shopping.mapper

import com.example.domain.model.Order
import woowacourse.shopping.model.OrderUiModel

fun Order.toPresentation(): OrderUiModel =
    OrderUiModel(orderId, payAmount, orderAt, productName, productImageUrl, totalProductCount)
