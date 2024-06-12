package woowacourse.shopping.data.remote.injector

import woowacourse.shopping.data.remote.datasource.order.RetrofitOrderDataSource
import woowacourse.shopping.data.remote.repository.OrderRepositoryImpl
import woowacourse.shopping.domain.repository.OrderRepository

object OrderRepositoryInjector {
    var instance: OrderRepository =
        OrderRepositoryImpl(
            RetrofitOrderDataSource(),
        )
        private set

    fun setInstance(orderRepository: OrderRepository) {
        instance = orderRepository
    }
}
