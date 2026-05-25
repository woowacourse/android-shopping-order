package woowacourse.shopping.data.repository

interface PaymentRepository {
    suspend fun createOrder(cartItemIds: List<String>)
}
