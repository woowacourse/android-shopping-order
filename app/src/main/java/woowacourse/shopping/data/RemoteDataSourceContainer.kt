package woowacourse.shopping.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import woowacourse.shopping.data.remote.CartItemRemoteSource
import woowacourse.shopping.data.remote.OrderRemoteSource
import woowacourse.shopping.data.remote.ProductRemoteSource
import woowacourse.shopping.data.remote.ServerConfiguration
import woowacourse.shopping.data.remote.UserRemoteRemoteSource
import woowacourse.shopping.data.remote.retrofit.CartItemRetrofitService
import woowacourse.shopping.data.remote.retrofit.OrderRetrofitService
import woowacourse.shopping.data.remote.retrofit.ProductRetrofitService
import woowacourse.shopping.data.remote.retrofit.UserRetrofitService

class RemoteDataSourceContainer {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder().baseUrl(ServerConfiguration.host.url)
            .addConverterFactory(MoshiConverterFactory.create(moshi)).build()
    }

    val product by lazy {
        ProductRemoteSource(retrofit.create(ProductRetrofitService::class.java))
    }

    val cart by lazy {
        CartItemRemoteSource(retrofit.create(CartItemRetrofitService::class.java))
    }

    val user by lazy {
        UserRemoteRemoteSource(retrofit.create(UserRetrofitService::class.java))
    }

    val order by lazy {
        OrderRemoteSource(retrofit.create(OrderRetrofitService::class.java))
    }
}
