package woowacourse.shopping.domain.repository

interface OrderRepository {
    fun postOrderProducts(
        cartIds: List<Long>,
        callback: (Result<Unit>) -> Unit,
    )
}
