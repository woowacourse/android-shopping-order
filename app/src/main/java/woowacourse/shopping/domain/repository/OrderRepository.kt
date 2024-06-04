package woowacourse.shopping.domain.repository

interface OrderRepository {
    fun insertOrder(cartItemsIds: List<Int>): Result<Unit>

    companion object {
        private var instance: OrderRepository? = null

        fun setInstance(orderRepository: OrderRepository) {
            instance = orderRepository
        }

        fun getInstance(): OrderRepository = requireNotNull(instance)
    }
}
