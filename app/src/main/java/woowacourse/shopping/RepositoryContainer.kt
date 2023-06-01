package woowacourse.shopping

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import woowacourse.shopping.data.cart.CartItemRemoteSource
import woowacourse.shopping.data.cart.DefaultCartItemRepository
import woowacourse.shopping.data.database.DbHelper
import woowacourse.shopping.data.order.DefaultOrderRepository
import woowacourse.shopping.data.order.OrderRemoteSource
import woowacourse.shopping.data.product.DefaultProductRepository
import woowacourse.shopping.data.product.ProductRemoteSource
import woowacourse.shopping.data.recentlyviewedproduct.DefaultRecentlyViewedProductRepository
import woowacourse.shopping.data.recentlyviewedproduct.RecentlyViewedProductMemorySource
import woowacourse.shopping.data.user.DefaultUserRepository
import woowacourse.shopping.data.user.UserMemorySource
import woowacourse.shopping.data.user.UserRemoteSource
import woowacourse.shopping.network.ServerConfiguration
import woowacourse.shopping.network.retrofit.CartItemRetrofitService
import woowacourse.shopping.network.retrofit.OrderRetrofitService
import woowacourse.shopping.network.retrofit.ProductRetrofitService
import woowacourse.shopping.network.retrofit.UserRetrofitService

class RepositoryContainer(context: Context) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder().baseUrl(ServerConfiguration.host.url)
            .addConverterFactory(MoshiConverterFactory.create(moshi)).build()
    }

    private val dbHelper = DbHelper(context).writableDatabase

    private val productRemoteSource by lazy {
        ProductRemoteSource(retrofit.create(ProductRetrofitService::class.java))
    }

    private val recentlyViewedProductMemorySource = RecentlyViewedProductMemorySource(dbHelper)

    private val cartItemRemoteSource by lazy {
        CartItemRemoteSource(retrofit.create(CartItemRetrofitService::class.java))
    }

    private val userCacheDataSource = UserMemorySource()
    private val userRemoteSource by lazy {
        UserRemoteSource(retrofit.create(UserRetrofitService::class.java))
    }

    private val orderDataSource by lazy {
        OrderRemoteSource(retrofit.create(OrderRetrofitService::class.java))
    }

    val productRepository by lazy { DefaultProductRepository(productRemoteSource) }
    val recentlyViewedProductRepository by lazy {
        DefaultRecentlyViewedProductRepository(
            recentlyViewedProductMemorySource, productRemoteSource
        )
    }
    val cartItemRepository by lazy { DefaultCartItemRepository(cartItemRemoteSource) }
    val userRepository by lazy { DefaultUserRepository(userCacheDataSource, userRemoteSource) }
    val orderRepository by lazy { DefaultOrderRepository(orderDataSource) }
}
