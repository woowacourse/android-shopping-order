package woowacourse.shopping.data.datasource.order

import woowacourse.shopping.data.model.DataOrder

interface OrderDataSource {
    interface Local

    interface Remote {
        fun addOrder(
            basketProductsId: List<Int>,
            usingPoint: Int,
            orderTotalPrice: Int,
            onReceived: (Result<Int>) -> Unit
        )

        fun getIndividualOrderInfo(orderId: Int, onReceived: (DataOrder) -> Unit)
    }
}
