package woowacourse.shopping.data.product

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import woowacourse.shopping.utils.ServerConfiguration

interface ProductRemoteService {

    @GET("products")
    fun requestProducts(): Call<List<ProductDto>>

    @GET("products/{productId}")
    fun requestProduct(@Path("productId") productId: Long): Call<ProductDto>

    companion object {

        private val INSTANCE: ProductRemoteService by lazy {
            Retrofit.Builder()
                .baseUrl(ServerConfiguration.host.url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ProductRemoteService::class.java)
        }

        fun getInstance(): ProductRemoteService = INSTANCE
    }
}
