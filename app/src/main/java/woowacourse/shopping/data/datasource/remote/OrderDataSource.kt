package woowacourse.shopping.data.datasource.remote

interface OrderDataSource {
    suspend fun postOrder(cartItemsIds: List<Int>): Result<Unit>

    companion object {
        private var instance: OrderDataSource? = null

        fun setInstance(orderDataSource: OrderDataSource) {
            instance = orderDataSource
        }

        fun getInstance(): OrderDataSource = requireNotNull(instance)
    }
}
