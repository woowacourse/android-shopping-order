package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.Order

interface OrderRepository {
    fun addOrder(
        basketProductsId: List<Int>,
        usingPoint: Int,
        orderTotalPrice: Int,
        onReceived: (Result<Int>) -> Unit
    )

    fun getIndividualOrderInfo(orderId: Int, onReceived: (Order) -> Unit)
}
