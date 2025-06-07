package woowacourse.shopping.data.di

import woowacourse.shopping.data.repository.DefaultCartRepository
import woowacourse.shopping.data.repository.DefaultCouponRepository
import woowacourse.shopping.data.repository.DefaultHistoryRepository
import woowacourse.shopping.data.repository.DefaultOrderRepository
import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository

class RepositoryModule(
    dataSourceModule: DataSourceModule,
) {
    val defaultCouponRepository: DefaultCouponRepository by lazy {
        DefaultCouponRepository(dataSourceModule.couponDataSource)
    }

    val defaultProductRepository: ProductRepository by lazy {
        DefaultProductRepository(dataSourceModule.productsDataSource)
    }

    val defaultHistoryRepository: HistoryRepository by lazy {
        DefaultHistoryRepository(dataSourceModule.defaultHistoryDataSource)
    }

    val defaultCartRepository: CartRepository by lazy {
        DefaultCartRepository(dataSourceModule.cartDataSource)
    }

    val defaultOrderRepository: OrderRepository by lazy {
        DefaultOrderRepository(dataSourceModule.orderDataSource)
    }
}
