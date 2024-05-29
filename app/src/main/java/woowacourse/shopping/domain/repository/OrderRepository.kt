package woowacourse.shopping.domain.repository

interface OrderRepository {
    fun insertOrder(cartItemsIds: List<Int>): Result<Unit>
}
