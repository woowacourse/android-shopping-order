package woowacourse.shopping.domain.payment

interface OrderRepository {
    suspend fun order(cartItemIds: List<Long>): Result<Unit>
}
