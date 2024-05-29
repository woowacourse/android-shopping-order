package woowacourse.shopping.domain.repository

interface OrderRepository {
    fun orderShoppingCart(ids: List<Int>)
}
