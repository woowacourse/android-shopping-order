package woowacourse.shopping.presentation.model

import woowacourse.shopping.domain.model.Order

data class OrderUiModel(
    val totalPrice: Long,
    val discount: Long,
    val deliveryPrice: Int,
    val paymentPrice: Long,
)

fun Order.toUiModel() = OrderUiModel(totalPrice(), discount(), deliveryPrice(), paymentPrice())
