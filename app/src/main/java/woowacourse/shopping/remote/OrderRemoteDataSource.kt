package woowacourse.shopping.remote

class OrderRemoteDataSource(private val orderApiService: OrderApiService) {
    fun order(cartItemIds: List<Long>) {
        orderApiService.createOrder(cartItemIds)
    }
}