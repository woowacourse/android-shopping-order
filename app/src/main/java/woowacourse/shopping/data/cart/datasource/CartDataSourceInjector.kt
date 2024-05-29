package woowacourse.shopping.data.cart.datasource

import woowacourse.shopping.data.common.ioExecutor
import woowacourse.shopping.remote.service.CartService

object CartDataSourceInjector {
    private var instance: CartDataSource? = null

    fun cartDataSource(): CartDataSource =
        instance ?: synchronized(this) {
            instance ?: DefaultCartDataSource(
                ioExecutor,
                CartService.instance()
            )
        }.also { instance = it }
}
