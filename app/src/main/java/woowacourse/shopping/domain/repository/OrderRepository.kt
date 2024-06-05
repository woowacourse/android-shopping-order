package woowacourse.shopping.domain.repository

interface OrderRepository {
    fun order(cartIds: List<Long>)
}
