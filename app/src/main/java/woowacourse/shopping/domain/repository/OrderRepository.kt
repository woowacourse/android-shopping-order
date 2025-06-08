package woowacourse.shopping.domain.repository

interface OrderRepository {
    suspend fun orderItems(checkedItems: List<Long>): Result<Unit>
}
