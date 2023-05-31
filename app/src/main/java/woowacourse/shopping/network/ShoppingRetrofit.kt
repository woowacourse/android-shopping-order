package woowacourse.shopping.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ShoppingRetrofit {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    val retrofit: Retrofit = Retrofit.Builder().baseUrl(ServerConfiguration.host.url)
        .addConverterFactory(MoshiConverterFactory.create(moshi)).build()
}
