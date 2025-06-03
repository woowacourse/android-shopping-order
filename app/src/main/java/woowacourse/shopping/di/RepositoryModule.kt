package woowacourse.shopping.di

import woowacourse.shopping.di.DatabaseModule.database
import woowacourse.shopping.di.NetworkModule.cartApi
import woowacourse.shopping.di.NetworkModule.couponApi
import woowacourse.shopping.di.NetworkModule.orderApi
import woowacourse.shopping.di.NetworkModule.productApi
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository

object RepositoryModule {
    val cartRepository: CartRepository by lazy {
        woowacourse.shopping.data.repository
            .CartRepository(cartApi)
    }

    val productRepository: ProductRepository by lazy {
        woowacourse.shopping.data.repository
            .ProductRepository(productApi)
    }

    val historyRepository: HistoryRepository by lazy {
        woowacourse.shopping.data.repository
            .HistoryRepository(database.historyDao())
    }

    val orderRepository: OrderRepository by lazy {
        woowacourse.shopping.data.repository
            .OrderRepository(orderApi)
    }

    val couponRepository: CouponRepository by lazy {
        woowacourse.shopping.data.repository
            .CouponRepository(couponApi)
    }
}
