package woowacourse.shopping.domain.repository

interface OrderRepository {
    fun completeOrder(
        productIds: List<Long>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    )
}
