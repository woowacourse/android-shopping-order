package woowacourse.shopping.data.di

import woowacourse.shopping.data.repository.DefaultCartRepository
import woowacourse.shopping.data.repository.DefaultCouponRepository
import woowacourse.shopping.data.repository.DefaultHistoryRepository
import woowacourse.shopping.data.repository.DefaultOrderRepository
import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository

class RepositoryModule(
    dataSourceModule: DataSourceModule,
) {
    val defaultCouponRepository: CouponRepository by lazy {
        DefaultCouponRepository(dataSourceModule.remoteCouponDataSource)
    }

    val defaultProductRepository: ProductRepository by lazy {
        DefaultProductRepository(dataSourceModule.remoteProductsDataSource)
    }

    val defaultHistoryRepository: HistoryRepository by lazy {
        DefaultHistoryRepository(dataSourceModule.defaultHistoryDataSource)
    }

    val defaultCartRepository: CartRepository by lazy {
        DefaultCartRepository(dataSourceModule.remoteCartDataSource)
    }

    val defaultOrderRepository: OrderRepository by lazy {
        DefaultOrderRepository(dataSourceModule.remoteOrderDataSource)
    }
}
