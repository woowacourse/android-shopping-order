package woowacourse.shopping.data.di

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.api.ProductApi
import woowacourse.shopping.data.dao.ProductDao

object NetworkModule {
    private lateinit var productDao: ProductDao

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient
            .Builder()
            .build()
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

    fun init(productDao: ProductDao) {
        this.productDao = productDao
    }
}
