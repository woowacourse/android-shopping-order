package woowacourse.shopping.di

import android.content.Context
import androidx.room.Room
import woowacourse.shopping.data.datasource.cart.CartRemoteDataSource
import woowacourse.shopping.data.datasource.cart.CartRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.coupon.CouponRemoteDataSource
import woowacourse.shopping.data.datasource.coupon.CouponRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.product.ProductRemoteDataSource
import woowacourse.shopping.data.datasource.product.ProductRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.recent.RecentProductDataSource
import woowacourse.shopping.data.datasource.recent.RoomRecentProductDataSource
import woowacourse.shopping.data.local.ShoppingDatabase
import woowacourse.shopping.data.remote.RetrofitProvider
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.CouponRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository

class AppContainer(
    context: Context,
) {
    private val database: ShoppingDatabase =
        Room
            .databaseBuilder(
                context.applicationContext,
                ShoppingDatabase::class.java,
                "shopping.db",
            ).fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    private val cartDataSource: CartRemoteDataSource =
        CartRemoteDataSourceImpl(
            RetrofitProvider.cartApi,
            RetrofitProvider.orderApi,
        )
    private val productRemoteDataSource: ProductRemoteDataSource =
        ProductRemoteDataSourceImpl(
            RetrofitProvider.productApi,
        )
    private val recentProductDataSource: RecentProductDataSource =
        RoomRecentProductDataSource(database.recentProductDao())

    private val couponDataSource: CouponRemoteDataSource =
        CouponRemoteDataSourceImpl(RetrofitProvider.couponApi)

    val cartRepository: CartRepository = CartRepositoryImpl(cartDataSource)
    val productRepository: ProductRepository = ProductRepositoryImpl(productRemoteDataSource)
    val recentProductRepository: RecentProductRepository =
        RecentProductRepositoryImpl(recentProductDataSource)

    val couponRepository = CouponRepositoryImpl(couponDataSource)
}
