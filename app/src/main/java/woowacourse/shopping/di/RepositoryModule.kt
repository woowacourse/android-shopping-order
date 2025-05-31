package woowacourse.shopping.di

import woowacourse.shopping.di.DatabaseModule.database
import woowacourse.shopping.di.NetworkModule.cartApi
import woowacourse.shopping.di.NetworkModule.orderApi
import woowacourse.shopping.di.NetworkModule.productApi

object RepositoryModule {
    val cartRepository: woowacourse.shopping.domain.repository.CartRepository by lazy {
        woowacourse.shopping.data.repository
            .CartRepository(cartApi)
    }

    val productRepository: woowacourse.shopping.domain.repository.ProductRepository by lazy {
        woowacourse.shopping.data.repository
            .ProductRepository(productApi)
    }

    val historyRepository: woowacourse.shopping.domain.repository.HistoryRepository by lazy {
        woowacourse.shopping.data.repository
            .HistoryRepository(database.historyDao())
    }

    val orderRepository: woowacourse.shopping.domain.repository.OrderRepository by lazy {
        woowacourse.shopping.data.repository
            .OrderRepository(orderApi)
    }
}
