package woowacourse.shopping.domain.repository

interface OrderRepository {
    fun postOrder(
        cartItemIds: List<Int>,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit,
    )
}
