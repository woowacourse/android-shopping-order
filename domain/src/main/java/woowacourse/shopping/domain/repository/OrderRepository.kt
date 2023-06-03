package woowacourse.shopping.domain.repository

interface OrderRepository {
    fun addOrder(
        basketProductsId: List<Int>,
        usingPoint: Int,
        orderTotalPrice: Int,
        onReceived: (Result<Int>) -> Unit
    )
}
