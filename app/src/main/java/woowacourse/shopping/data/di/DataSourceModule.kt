package woowacourse.shopping.data.di

import woowacourse.shopping.data.NetworkResultHandler
import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.datasource.CouponDataSource
import woowacourse.shopping.data.datasource.OrderDataSource
import woowacourse.shopping.data.datasource.ProductsDataSource
import woowacourse.shopping.data.datasource.history.DefaultHistoryDataSource

class DataSourceModule(
    local: DatabaseModule,
    remote: NetworkModule,
    networkResultHandler: NetworkResultHandler,
) {
    val defaultHistoryDataSource: DefaultHistoryDataSource by lazy { DefaultHistoryDataSource(local.historyDao) }

    val couponDataSource: CouponDataSource by lazy { CouponDataSource(remote.couponService, networkResultHandler) }

    val cartDataSource: CartDataSource by lazy { CartDataSource(remote.cartService, networkResultHandler) }

    val productsDataSource: ProductsDataSource by lazy { ProductsDataSource(remote.productService, networkResultHandler) }

    val orderDataSource: OrderDataSource by lazy { OrderDataSource(remote.orderService, networkResultHandler) }
}
