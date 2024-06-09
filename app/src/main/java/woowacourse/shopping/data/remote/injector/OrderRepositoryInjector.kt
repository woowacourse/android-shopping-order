package woowacourse.shopping.data.remote.injector

import woowacourse.shopping.data.remote.datasource.cartItem.DefaultCartItemDataSource
import woowacourse.shopping.data.remote.datasource.order.DefaultOrderDataSource
import woowacourse.shopping.data.remote.repository.CartItemRepositoryImpl
import woowacourse.shopping.data.remote.repository.OrderRepositoryImpl
import woowacourse.shopping.domain.CartItemRepository
import woowacourse.shopping.domain.OrderRepository

object OrderRepositoryInjector {
    var instance: OrderRepository =
        OrderRepositoryImpl(
            DefaultOrderDataSource()
        )
        private set

    fun setInstance(orderRepository: OrderRepository) {
        instance = orderRepository
    }
}