package woowacourse.shopping.data.di

import woowacourse.shopping.data.NetworkResultHandler
import woowacourse.shopping.data.datasource.local.LocalHistoryDataSource
import woowacourse.shopping.data.datasource.remote.RemoteCartDataSource
import woowacourse.shopping.data.datasource.remote.RemoteCouponDataSource
import woowacourse.shopping.data.datasource.remote.RemoteOrderDataSource
import woowacourse.shopping.data.datasource.remote.RemoteProductsDataSource

class DataSourceModule(
    local: DatabaseModule,
    remote: NetworkModule,
    networkResultHandler: NetworkResultHandler,
) {
    val defaultHistoryDataSource: LocalHistoryDataSource by lazy { LocalHistoryDataSource(local.historyDao) }

    val remoteCouponDataSource: RemoteCouponDataSource by lazy { RemoteCouponDataSource(remote.couponService, networkResultHandler) }

    val remoteCartDataSource: RemoteCartDataSource by lazy { RemoteCartDataSource(remote.cartService, networkResultHandler) }

    val remoteProductsDataSource: RemoteProductsDataSource by lazy { RemoteProductsDataSource(remote.productService, networkResultHandler) }

    val remoteOrderDataSource: RemoteOrderDataSource by lazy { RemoteOrderDataSource(remote.orderService, networkResultHandler) }
}
