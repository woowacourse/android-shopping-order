package woowacourse.shopping.domain.repository.order

interface OrderRepository {
    fun order(cartItemIds: List<Long>)
}
