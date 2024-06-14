package woowacourse.shopping.data.cart

import androidx.annotation.VisibleForTesting
import woowacourse.shopping.data.cart.datasource.CartDataSourceInjector
import woowacourse.shopping.data.cart.order.OrderDataSourceImpl
import woowacourse.shopping.data.shopping.product.datasource.ProductDataSourceInjector
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.remote.service.OrderService

object CartRepositoryInjector {
    @Volatile
    private var instance: CartRepository? = null

    fun cartRepository(): CartRepository =
        instance ?: synchronized(this) {
            instance ?: CartRepositoryImpl(
                CartDataSourceInjector.cartDataSource(),
                ProductDataSourceInjector.productDataSource(),
                OrderDataSourceImpl(OrderService.instance()),
            ).also { instance = it }
        }

    @VisibleForTesting
    fun setCartRepository(cartRepository: CartRepository) {
        instance = cartRepository
    }

    @VisibleForTesting
    fun clear() {
        instance = null
    }
}
