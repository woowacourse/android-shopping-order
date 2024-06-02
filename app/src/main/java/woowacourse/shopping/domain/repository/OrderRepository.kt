package woowacourse.shopping.domain.repository

interface OrderRepository {
    fun insertOrderByIds(cartItemsIds: List<Int>): Result<Unit>
}
