package woowacourse.shopping.domain.repository

interface OrderRepository {
    fun order(cartItemIds: List<Long>)
}