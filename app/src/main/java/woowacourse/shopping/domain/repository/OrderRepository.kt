package woowacourse.shopping.domain.repository

interface OrderRepository {
    fun createOrder(
        cartItemIds: List<Int>,
        callback: (Result<Unit>) -> Unit,
    )
}
