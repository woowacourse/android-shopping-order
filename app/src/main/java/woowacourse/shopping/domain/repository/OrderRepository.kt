package woowacourse.shopping.domain.repository

interface OrderRepository {
    suspend fun orderShoppingCart(ids: List<Int>): Result<Unit>
}
