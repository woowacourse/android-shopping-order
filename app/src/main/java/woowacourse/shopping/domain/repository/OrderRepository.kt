package woowacourse.shopping.domain.repository

interface OrderRepository {
    fun orderItems(
        checkedItems: List<Long>,
        onResult: (Result<Unit>) -> Unit,
    )
}
