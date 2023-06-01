package woowacourse.shopping.data.order

class OrderRepositoryImpl(private val orderDataSource: OrderDataSource) : OrderRepository {
    override fun order(onSuccess: () -> Unit, onFailure: () -> Unit) {
        orderDataSource.order(onSuccess = {
            onSuccess()
        }, onFailure = {
            onFailure()
        })
    }
}
