package woowacourse.shopping

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import woowacourse.shopping.data.database.DbHelper
import woowacourse.shopping.data.local.RecentlyViewedProductMemorySource
import woowacourse.shopping.data.local.UserMemorySource
import woowacourse.shopping.data.remote.CartItemRemoteSource
import woowacourse.shopping.data.remote.OrderRemoteSource
import woowacourse.shopping.data.remote.ProductRemoteSource
import woowacourse.shopping.data.remote.ServerConfiguration
import woowacourse.shopping.data.remote.UserRemoteSource
import woowacourse.shopping.data.remote.retrofit.CartItemRetrofitService
import woowacourse.shopping.data.remote.retrofit.OrderRetrofitService
import woowacourse.shopping.data.remote.retrofit.ProductRetrofitService
import woowacourse.shopping.data.remote.retrofit.UserRetrofitService
import woowacourse.shopping.data.repository.DefaultCartItemRepository
import woowacourse.shopping.data.repository.DefaultOrderRepository
import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.data.repository.DefaultRecentlyViewedProductRepository
import woowacourse.shopping.data.repository.DefaultUserRepository

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
