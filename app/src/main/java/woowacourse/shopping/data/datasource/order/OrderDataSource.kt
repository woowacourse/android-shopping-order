package woowacourse.shopping.data.datasource.order

import woowacourse.shopping.data.remote.response.addorder.AddOrderResponse

interface OrderDataSource {
    interface Local

    interface Remote {
        fun addOrder(
            basketProductsId: List<Int>,
            usingPoint: Int,
            orderTotalPrice: Int,
            onReceived: (AddOrderResponse) -> Unit
        )
    }
}
