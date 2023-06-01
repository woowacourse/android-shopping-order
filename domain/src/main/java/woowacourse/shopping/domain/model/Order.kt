package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.discount.Discountable

data class Order(
    val id: Int = NO_ID,
    val orderProducts: List<OrderProduct>,
    val payment: Payment,
) {
    val finalPrice: Price = payment.finalPrice

    fun discount(point: Discountable): Order = copy(
        payment = payment.discount(point)
    )

    companion object {
        private const val NO_ID = -1
    }
}
