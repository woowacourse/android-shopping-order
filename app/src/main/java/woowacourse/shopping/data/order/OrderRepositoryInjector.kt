package woowacourse.shopping.data.order

import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.remote.service.OrderService

object OrderRepositoryInjector {
    private var instance: OrderRepository? = null

    fun orderRepository(): OrderRepository = instance ?: synchronized(this) {
        instance ?: DefaultOrderRepository(
            DefaultOrderDataSource(
                OrderService.instance()
            )
        ).also {
            instance = it
        }
    }
}