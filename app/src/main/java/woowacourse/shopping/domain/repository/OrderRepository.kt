package woowacourse.shopping.domain.repository

interface OrderRepository {
    suspend fun completeOrder(cartItemIds: List<Long>): Result<Unit>

    companion object {
        const val SHIPPING_FEE = 3_000
    }
}
