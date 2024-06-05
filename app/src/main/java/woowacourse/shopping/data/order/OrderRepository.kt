package woowacourse.shopping.data.order

interface OrderRepository {
    fun order(cartIds: List<Long>)
}
