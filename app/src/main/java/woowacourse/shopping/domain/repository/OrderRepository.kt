package woowacourse.shopping.domain.repository

interface OrderRepository {
    fun completeOrder(
        cartItemIds: List<Long>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    )
}
