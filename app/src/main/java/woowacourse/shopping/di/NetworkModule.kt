package woowacourse.shopping.di

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.api.CartApi
import woowacourse.shopping.data.api.OrderApi
import woowacourse.shopping.data.api.ProductApi
import woowacourse.shopping.data.interceptor.ShoppingAuthInterceptor
import woowacourse.shopping.di.PreferenceModule.authSharedPreference

object NetworkModule {
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient
            .Builder()
            .addInterceptor(
                ShoppingAuthInterceptor(authSharedPreference),
            ).build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.TECHCOURSE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val productApi: ProductApi by lazy {
        retrofit.create(ProductApi::class.java)
    }
    val cartApi: CartApi by lazy {
        retrofit.create(CartApi::class.java)
    }
    val orderApi: OrderApi by lazy {
        retrofit.create(OrderApi::class.java)
    }
}
