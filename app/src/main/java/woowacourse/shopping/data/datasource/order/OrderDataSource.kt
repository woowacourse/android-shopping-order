package woowacourse.shopping.data.datasource.order

interface OrderDataSource {
    interface Local

    interface Remote {
        fun addOrder(
            basketProductsId: List<Int>,
            usingPoint: Int,
            orderTotalPrice: Int,
            onReceived: (Result<Int>) -> Unit
        )
    }
}
