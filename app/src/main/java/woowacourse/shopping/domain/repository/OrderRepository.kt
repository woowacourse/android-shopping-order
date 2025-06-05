package woowacourse.shopping.domain.repository

interface OrderRepository {
    fun addOrder(
        cartItemIds: List<String>,
        onResult: (Result<Unit>) -> Unit,
    )
}
