package woowacourse.shopping.data.cart.datasource

import woowacourse.shopping.remote.service.CartService

object CartDataSourceInjector {
    private var instance: CartDataSource? = null

    fun cartDataSource(): CartDataSource =
        instance ?: synchronized(this) {
            instance ?: CartDataSourceImpl(
                CartService.instance(),
            )
        }.also { instance = it }
}
